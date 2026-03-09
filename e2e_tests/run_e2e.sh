#!/bin/bash

# ==============================================================================
# PHASE 1: Environment Initialization & Tool Verification
# ==============================================================================
# This phase prepares the execution environment.
# It resolves project paths, loads external configuration (.env),
# defines runtime constants, and verifies required system tools.
# The goal is to fail fast if the environment is not ready.

# Ensure that if any command in a pipeline fails,
# the entire pipeline is considered failed.
set -o pipefail

# Resolve the absolute path to the project root directory.
# ${BASH_SOURCE[0]} points to the current script location.
# We move one directory up because this script lives inside /e2e_tests.
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# ------------------------------------------------------------------------------
# Logging utilities
# ------------------------------------------------------------------------------

# Logs informational messages with a timestamp.
# Used for normal execution tracing.
log_info() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') [INFO] $1"
}

# Logs error messages with a timestamp to STDERR.
# Used for failure scenarios.
log_error() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') [ERROR] $1" >&2
}

log_info "Starting E2E Runner Script"
log_info "Project Root detected: $PROJECT_ROOT"

# ------------------------------------------------------------------------------
# Load environment variables from .env (if present)
# ------------------------------------------------------------------------------
# This allows configuration override without modifying the script.
# WARNING: export with xargs assumes simple KEY=VALUE format.
if [ -f "$PROJECT_ROOT/.env" ]; then
    log_info "Loading environment variables from .env (APP_PORT and APP_HOST will not be overwritten if already set)"
    # Load env vars but don't overwrite if already exported
    while IFS= read -r line || [[ -n "$line" ]]; do
        if [[ ! "$line" =~ ^# && "$line" =~ = ]]; then
            key=$(echo "$line" | cut -d= -f1)
            value=$(echo "$line" | cut -d= -f2-)
            if [[ -z "${!key}" ]]; then
                export "$key"="$value"
            fi
        fi
    done < "$PROJECT_ROOT/.env"
fi

# ------------------------------------------------------------------------------
# Configurable Runtime Parameters
# ------------------------------------------------------------------------------

# Use environment variable if provided, otherwise default to 8080.
export APP_PORT=${APP_PORT:-8080}

# Base host for test execution (can be overridden externally).
export APP_HOST="${APP_HOST:-http://localhost}"

# Expected name of the generated Spring Boot JAR artifact.
JAR_NAME="mail-and-media-shop-1.0-SNAPSHOT.jar"

# Log file locations for application output.
APP_LOG="$PROJECT_ROOT/app.log"
APP_LOG_OLD="$PROJECT_ROOT/app.log.old"

log_info "Configuration: HOST=$APP_HOST, PORT=$APP_PORT, JAR=$JAR_NAME"

# ------------------------------------------------------------------------------
# Health Check & Port Recovery Configuration
# ------------------------------------------------------------------------------

# Maximum number of retries when polling the health endpoint.
MAX_RETRIES=${HEALTH_CHECK_RETRIES:-30}

# Interval (in seconds) between health check attempts.
RETRY_INTERVAL=${HEALTH_CHECK_INTERVAL:-2}

# Maximum time to wait until a previously occupied port is released.
PORT_FREE_TIMEOUT=${PORT_FREE_TIMEOUT:-10}

# Spring Boot Actuator health endpoint.
HEALTH_ENDPOINT="http://localhost:$APP_PORT/actuator/health"

# ------------------------------------------------------------------------------
# Python Test Environment Configuration
# ------------------------------------------------------------------------------

# Required Python packages for E2E tests.
PYTHON_PACKAGES="requests"

# Virtual environment directory path.
VENV_DIR="$PROJECT_ROOT/venv"

# ------------------------------------------------------------------------------
# Centralized error exit function
# ------------------------------------------------------------------------------

# Prints error message and terminates script execution immediately.
error_exit() {
    log_error "$1"
    exit 1
}

# ------------------------------------------------------------------------------
# Verify required system tools
# ------------------------------------------------------------------------------

# Ensure critical dependencies exist before proceeding.
# If any required binary is missing, abort execution immediately.
log_info "Verifying required system tools..."
for tool in mvn java curl fuser; do
    if ! command -v "$tool" &> /dev/null; then
        error_exit "Required tool '$tool' is not installed or not in PATH."
    fi
done

# Log tool versions for audit/debug purposes.
log_info "Java version: $(java -version 2>&1 | head -n 1)"
log_info "Maven version: $(mvn -version | head -n 1)"
log_info "Curl version: $(curl --version | head -n 1)"

# ------------------------------------------------------------------------------
# Cleanup Handler
# ------------------------------------------------------------------------------

# This function ensures graceful shutdown of resources.
# It will always execute due to the EXIT trap.
cleanup() {

    # If application process exists, attempt graceful shutdown.
    if [ ! -z "$APP_PID" ] && ps -p "$APP_PID" > /dev/null; then
        log_info "Shutting down application (PID: $APP_PID)..."

        # First attempt: SIGTERM (graceful stop)
        kill "$APP_PID" 2>/dev/null || true

        # Wait up to 5 seconds for clean shutdown.
        timeout=5
        while ps -p "$APP_PID" > /dev/null && [ $timeout -gt 0 ]; do
            sleep 1
            ((timeout--))
        done

        # If still alive, force kill with SIGKILL.
        if ps -p "$APP_PID" > /dev/null; then
            log_info "Process $APP_PID stubborn, sending SIGKILL..."
            kill -9 "$APP_PID" 2>/dev/null || true
        fi

        log_info "Application process terminated."
    fi

    # Deactivate Python virtual environment if active.
    if [[ "$VIRTUAL_ENV" != "" ]]; then
        log_info "Deactivating Python virtual environment..."
        deactivate 2>/dev/null || true
    fi

    log_info "Script cleanup complete."
}

# Register cleanup function to run automatically on script exit.
trap cleanup EXIT

# ==============================================================================
# PHASE 2: Pre-test Cleanup & Port Recovery
# ==============================================================================
# Ensures no leftover process is occupying the target port.
# Prevents startup conflicts and port binding errors.

log_info "Ensuring port $APP_PORT is free (cleaning up legacy processes)..."

# Attempt to kill any process listening on the port.
fuser -k "$APP_PORT/tcp" 2>/dev/null || true

# Wait until the OS confirms port is fully released.
FREE_COUNT=0
while fuser "$APP_PORT/tcp" &>/dev/null; do
    if [ "$FREE_COUNT" -ge "$PORT_FREE_TIMEOUT" ]; then
        error_exit "Blocking: Port $APP_PORT is still occupied after $PORT_FREE_TIMEOUT seconds."
    fi
    log_info "Port $APP_PORT still occupied, waiting..."
    sleep 1
    FREE_COUNT=$((FREE_COUNT+1))
done

if [ "$FREE_COUNT" -gt 0 ]; then
    log_info "Port $APP_PORT confirmed free after recovery."
else
    log_info "Port $APP_PORT is clear."
fi

# ==============================================================================
# PHASE 3: Application Build
# ==============================================================================
# Executes Maven build lifecycle to produce the Spring Boot executable JAR.

log_info "Starting Maven build process (skipping tests)..."

# If Maven fails, abort immediately.
mvn clean package -DskipTests || error_exit "Maven execution failed. Build aborted."

# Validate build artifact presence.
if [ ! -f "$PROJECT_ROOT/target/$JAR_NAME" ]; then
    error_exit "Build artifact missing: Expected $PROJECT_ROOT/target/$JAR_NAME"
fi

log_info "Maven build successful. JAR created."

# ==============================================================================
# PHASE 4: Python Environment Setup
# ==============================================================================
# Prepares isolated Python environment for E2E test execution.

log_info "Initiating Python environment setup..."

# Attempt to detect modern Python interpreter.
PYTHON_EXEC=$(which python3.12 || which python3 || which python)

if [ -z "$PYTHON_EXEC" ]; then
    error_exit "Python 3 environment not found on this system."
fi

log_info "Using Python interpreter: $PYTHON_EXEC ($($PYTHON_EXEC --version))"

# Create virtual environment if it does not exist.
if [ ! -d "$VENV_DIR" ]; then
    log_info "Creating new virtual environment in $VENV_DIR"
    $PYTHON_EXEC -m venv "$VENV_DIR" || error_exit "Failed to create virtual environment."
else
    log_info "Existing virtual environment found in $VENV_DIR"
fi

# Activate the virtual environment.
log_info "Activating virtual environment..."
source "$VENV_DIR/bin/activate" || error_exit "Activation of Python venv failed."

# Upgrade pip and install test dependencies.
log_info "Upgrading pip and installing required packages: $PYTHON_PACKAGES"
pip install --upgrade pip --quiet || error_exit "Pip upgrade failed."
pip install $PYTHON_PACKAGES --quiet || error_exit "Dependency installation failed."

log_info "Python environment ready."

sleep 2
# ==============================================================================
# PHASE 5: Application Startup & Log Rotation
# ==============================================================================
# Rotates previous logs and launches the Spring Boot application in background.

if [ -f "$APP_LOG" ]; then
    log_info "Found existing application log. Rotating to $APP_LOG_OLD"
    mv "$APP_LOG" "$APP_LOG_OLD"
fi

log_info "Executing: java -jar target/$JAR_NAME on port $APP_PORT"

# Start application as background process.
java -Dserver.port=$APP_PORT -jar "$PROJECT_ROOT/target/$JAR_NAME" > "$APP_LOG" 2>&1 &
APP_PID=$!

log_info "Application background process ID: $APP_PID"

# Perform basic process-level validation after short delay.
sleep 3
if ! ps -p "$APP_PID" > /dev/null; then
    log_error "Application process died immediately after startup!"
    log_error "Last 20 lines of $APP_LOG:"
    cat "$APP_LOG"
    error_exit "Check application startup configuration."
fi

log_info "Application process is alive. Startup log snippet:"
head -n 20 "$APP_LOG"

# ==============================================================================
# PHASE 6: Health Check Verification
# ==============================================================================
# Poll Spring Boot Actuator health endpoint until service reports ready.

log_info "Polling health status at $HEALTH_ENDPOINT..."
log_info "Max retry attempts: $MAX_RETRIES (Interval: ${RETRY_INTERVAL}s)"

COUNT=0
READY=false

while [ $COUNT -lt "$MAX_RETRIES" ]; do
    if curl --output /dev/null --silent --head --fail "$HEALTH_ENDPOINT"; then
        READY=true
        break
    fi

    printf '.'
    sleep "$RETRY_INTERVAL"
    COUNT=$((COUNT+1))

    # Print progress every 5 attempts for long startups.
    if (( COUNT % 5 == 0 )); then
        echo " ($COUNT/$MAX_RETRIES attempts completed)"
    fi
done

if [ "$READY" = false ]; then
    log_error "Application health check failed after $MAX_RETRIES attempts."
    log_error "Check $APP_LOG for Spring Boot startup errors."
    error_exit "Readiness timeout reached."
fi

echo ""
log_info "Application is fully initialized and READY."

# ==============================================================================
# PHASE 7: E2E Test Execution
# ==============================================================================
# Executes the Python-based End-to-End test suite.

log_info "Preparing to execute Python E2E test suite..."
log_info "Target Environment: $APP_HOST:$APP_PORT"

export PYTHONPATH="$PROJECT_ROOT/e2e_tests"
TEST_SCRIPT="$PROJECT_ROOT/e2e_tests/main.py"

if [ ! -f "$TEST_SCRIPT" ]; then
    error_exit "Test entry point not found: $TEST_SCRIPT"
fi

log_info "Running: python3 main.py"

# Execute tests and capture exit code.
python3 "$TEST_SCRIPT"
TEST_EXIT_CODE=$?

# ==============================================================================
# PHASE 8: Summary Report
# ==============================================================================
# Final reporting phase. Exit code reflects test outcome.

log_info "Test suite execution completed."

if [ $TEST_EXIT_CODE -eq 0 ]; then
    log_info "Result: PASSED"
else
    log_error "Result: FAILED (Exit Code: $TEST_EXIT_CODE)"
    log_info "Checking why it failed..."
    if [ -f "$APP_LOG" ]; then
        log_info "Last 100 lines of application log ($APP_LOG):"
        tail -n 100 "$APP_LOG"
    else
        log_error "Application log file $APP_LOG missing!"
    fi
    log_info "Health endpoint status ($HEALTH_ENDPOINT):"
    curl -v --silent "$HEALTH_ENDPOINT" 2>&1 | head -n 20
fi

exit $TEST_EXIT_CODE