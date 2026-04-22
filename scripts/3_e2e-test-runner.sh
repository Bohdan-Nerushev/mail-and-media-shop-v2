#!/bin/bash
# ==============================================================================
# PHASE 3: Health Check
# ==============================================================================

# Source common utilities and load environment
#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/utils.sh"
source "$(dirname "${BASH_SOURCE[0]}")/env_loader.sh"

export APP_PORT=${APP_PORT:-8090}
export APP_HOST="${APP_HOST:-http://localhost}"
export HEALTH_ENDPOINT="$APP_HOST:$APP_PORT/actuator/health"

log_info "Waiting for application to be healthy at $HEALTH_ENDPOINT..."

MAX_RETRIES=40
COUNT=0
READY=false
LAST_RESPONSE=""
LAST_HTTP_CODE="000"

while [ $COUNT -lt "$MAX_RETRIES" ]; do
    LAST_RESPONSE=$(curl --silent --show-error --max-time 5 "$HEALTH_ENDPOINT" 2>/dev/null || true)
    LAST_HTTP_CODE=$(curl --silent --output /dev/null --write-out "%{http_code}" --max-time 5 "$HEALTH_ENDPOINT" 2>/dev/null || true)

    if [ "$LAST_HTTP_CODE" = "200" ] && echo "$LAST_RESPONSE" | grep -q '"status":"UP"'; then
        READY=true
        break
    fi

    printf '.'
    sleep 3
    COUNT=$((COUNT+1))

    if (( COUNT % 10 == 0 )); then
        echo " ($COUNT/$MAX_RETRIES attempts completed, last HTTP code: $LAST_HTTP_CODE)"
    fi
done

echo ""

if [ "$READY" = false ]; then
    log_error "Application failed to become healthy."
    log_error "Last HTTP code: $LAST_HTTP_CODE"
    log_error "Last response: $LAST_RESPONSE"
    log_error "Container logs for 'app':"
    docker compose logs app | tail -n 100
    error_exit "Readiness check timed out."
fi

log_info "Application is fully initialized and READY."

# ==============================================================================
# PHASE 4: Python Environment & Test Execution
# ==============================================================================

VENV_DIR="$PROJECT_ROOT/e2e_tests/venv"

# Use python3.12 or fallback to python3
PYTHON_EXEC=$(which python3.12 || which python3)

if [ -z "$PYTHON_EXEC" ]; then
    error_exit "Python 3 not found."
fi

if [ ! -d "$VENV_DIR" ]; then
    log_info "Creating virtual environment..."
    $PYTHON_EXEC -m venv "$VENV_DIR" || error_exit "Venv creation failed."
fi

log_info "Activating venv and installing requirements..."
source "$VENV_DIR/bin/activate" || error_exit "Venv activation failed."
pip install -r "$PROJECT_ROOT/e2e_tests/requirements.txt" --quiet || error_exit "Pip install failed."

log_info "Executing Python E2E tests..."
export PYTHONPATH="$PROJECT_ROOT/e2e_tests"

python3 "$PROJECT_ROOT/e2e_tests/main.py"
TEST_EXIT_CODE=$?

# ==============================================================================
# PHASE 5: Reporting & Log Capture
# ==============================================================================

# Capture container logs for analysis (essential for GitLab CI artifacts)
log_info "Capturing container logs for 'app'..."
docker compose logs app > "$PROJECT_ROOT/app_container.log"
log_info "App logs saved to app_container.log"

if [ $TEST_EXIT_CODE -eq 0 ]; then
    log_info "RESULT: E2E Tests PASSED"
else
    log_error "RESULT: E2E Tests FAILED (Exit Code: $TEST_EXIT_CODE)"
fi

exit $TEST_EXIT_CODE