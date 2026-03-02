#!/bin/bash

# Get the absolute path to the project root
# (dirname $0) — this is the folder where the script is located (e2e_tests/).
# We go one level up (/..) to reach the actual project root.
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
echo "Project Root: $PROJECT_ROOT"

# Load environment variables from .env if it exists
if [ -f "$PROJECT_ROOT/.env" ]; then
    echo "Loading environment variables from .env"
    export $(grep -v '^#' "$PROJECT_ROOT/.env" | xargs)
fi

# Set default port if not provided
export APP_PORT=${APP_PORT:-8080}
echo "Using Application Port: $APP_PORT"

# 1. Stop processes on the specified port (if any exist)
echo "Stopping any existing application on port $APP_PORT..."
fuser -k $APP_PORT/tcp || true
sleep 2

# 2. Build the JAR
echo "Building the application..."
mvn clean package -DskipTests

# 3. Set up Python Virtual Environment (fixes PEP 668 error)
echo "Setting up Python virtual environment..."
VENV_DIR="$PROJECT_ROOT/venv"

# Try to use Python 3.12 (if available), otherwise fall back to python3
PYTHON_EXEC=$(which python3.12 || which python3)
echo "Using Python: $PYTHON_EXEC"

if [ ! -d "$VENV_DIR" ]; then
    $PYTHON_EXEC -m venv "$VENV_DIR"
fi

# Activate venv
source "$VENV_DIR/bin/activate"

# Install dependencies inside venv
echo "Installing Python dependencies (requests)..."
pip install --upgrade pip --quiet
pip install requests --quiet

# 4. Start the Spring Boot application
echo "Starting the application on port $APP_PORT..."
java -Dserver.port=$APP_PORT -jar "$PROJECT_ROOT/target/mail-and-media-shop-1.0-SNAPSHOT.jar" > "$PROJECT_ROOT/app.log" 2>&1 &
APP_PID=$!

# 5. Wait until the app is ready (Health Check)
echo "Waiting for the application to be ready on localhost:$APP_PORT (Actuator)..."
MAX_RETRIES=30
COUNT=0
until $(curl --output /dev/null --silent --head --fail http://localhost:$APP_PORT/actuator/health); do
    printf '.'
    sleep 2
    COUNT=$((COUNT+1))
    if [ $COUNT -eq $MAX_RETRIES ]; then
        echo -e "\nError: Application failed to start in time. Check app.log"
        kill $APP_PID
        exit 1
    fi
done
echo -e "\nApplication is UP and running!"

# 6. Run E2E tests
echo "Running E2E tests..."
# Ensure PYTHONPATH includes the e2e_tests folder for proper imports
export PYTHONPATH="$PROJECT_ROOT/e2e_tests"

# Set defaults for host if not provided
export APP_HOST="${APP_HOST:-http://localhost}"
echo "Tests will run against: $APP_HOST:$APP_PORT"

# Run the main script
python3 "$PROJECT_ROOT/e2e_tests/main.py"
TEST_EXIT_CODE=$?

# 7. Shutdown phase
echo "Shutting down the application (PID: $APP_PID)..."
kill $APP_PID
sleep 2
deactivate # exit virtual environment

if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "E2E Tests: PASSED"
else
    echo "E2E Tests: FAILED (Exit Code: $TEST_EXIT_CODE)"
fi

exit $TEST_EXIT_CODE