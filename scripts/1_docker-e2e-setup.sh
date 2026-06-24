#!/bin/bash

# ==============================================================================
# PHASE 1: Environment Initialization & Docker Verification
# ==============================================================================
set -o pipefail

# Source common utilities and load environment
source "$(dirname "${BASH_SOURCE[0]}")/utils.sh"
source "$(dirname "${BASH_SOURCE[0]}")/env_loader.sh"

# Configurable Parameters
export APP_PORT=${APP_PORT:-8090}
export APP_HOST="${APP_HOST:-http://localhost}"
HEALTH_ENDPOINT="$APP_HOST:$APP_PORT/actuator/health"

# Verify Docker tools
check_dependencies docker curl

# Check if 'docker compose' (plugin) works
if ! docker compose version &> /dev/null; then
    error_exit "Docker Compose plugin is not installed or not working."
fi

# ==============================================================================
# PHASE 2: Cleanup & Container Orchestration
# ==============================================================================

log_info "Ensuring clean environment (stopping any existing containers)..."
docker compose down -v 2>/dev/null || true

# Ensure logs directory exists and is writable by non-root container users
mkdir -p "$PROJECT_ROOT/logs"
chmod 1777 "$PROJECT_ROOT/logs" 2>/dev/null || true

# Ensure certs directory exists
mkdir -p "$PROJECT_ROOT/certs"

if command -v mkcert &> /dev/null; then
    log_info "mkcert detected. Copying root CA to certs/rootCA.pem..."
    cp "$(mkcert -CAROOT)/rootCA.pem" "$PROJECT_ROOT/certs/rootCA.pem" 2>/dev/null || true
fi

if [ ! -f "$PROJECT_ROOT/certs/keycloak-cert.pem" ] || [ ! -f "$PROJECT_ROOT/certs/keycloak-key.pem" ]; then
    log_info "Certificates not found. Generating self-signed certificates..."
    openssl req -x509 -newkey rsa:4096 -nodes -sha256 \
      -keyout "$PROJECT_ROOT/certs/keycloak-key.pem" \
      -out "$PROJECT_ROOT/certs/keycloak-cert.pem" \
      -subj "/CN=keycloak" \
      -days 365 \
      -addext "subjectAltName=DNS:keycloak,DNS:localhost,IP:127.0.0.1" || error_exit "Certificate generation failed."
fi

if [ ! -f "$PROJECT_ROOT/certs/truststore.jks" ]; then
    log_info "Java truststore not found. Generating truststore.jks..."
    keytool -importcert -noprompt \
      -keystore "$PROJECT_ROOT/certs/truststore.jks" \
      -storepass changeit \
      -alias keycloak \
      -file "$PROJECT_ROOT/certs/keycloak-cert.pem" || error_exit "Java truststore generation failed."
fi

log_info "Building and starting infrastructure containers..."
docker compose up -d keycloak-db keycloak shop_db redis || error_exit "Docker Compose infrastructure start failed."

log_info "Starting keycloak-setup (Keycloak configuration automation)..."
docker compose up --no-build keycloak-setup || error_exit "Keycloak setup failed."

log_info "Building and starting application..."
docker compose up -d --build app || error_exit "Docker Compose app start failed."

# ==============================================================================
# PHASE 3: Network Integration (for CI/CD environments)
# ==============================================================================

log_info "Connecting CI container to Docker Compose network..."
# Determine network name (preserving hyphens)
COMPOSE_PROJECT_NAME=$(basename "$PROJECT_ROOT" | tr '[:upper:]' '[:lower:]' | sed 's/[^a-z0-9_-]//g')
NETWORK_NAME="${COMPOSE_PROJECT_NAME}_default"

# Get current container ID more reliably
if [ -f /proc/self/cgroup ]; then
    CURRENT_CONTAINER=$(cat /proc/self/cgroup | grep "docker" | head -n 1 | cut -d '/' -f 3 2>/dev/null)
fi

if [ -z "$CURRENT_CONTAINER" ]; then
    CURRENT_CONTAINER=$(hostname)
fi

log_info "Determined current container/host ID: $CURRENT_CONTAINER"

# Check if we are actually inside a container
if docker inspect "$CURRENT_CONTAINER" &>/dev/null; then
    log_info "Attempting to connect container $CURRENT_CONTAINER to network $NETWORK_NAME"
    docker network connect "$NETWORK_NAME" "$CURRENT_CONTAINER" 2>/dev/null || log_info "Already connected or connection failed."
    
    # Verify connection
    if docker inspect "$CURRENT_CONTAINER" -f '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}' | grep -q "$NETWORK_NAME"; then
        log_info "✅ Successfully connected to $NETWORK_NAME"
    else
        log_error "❌ Failed to connect to $NETWORK_NAME. Communication might fail."
    fi
else
    log_info "Running on host or could not find container ID in Docker. Skipping network connect."
fi

# Debug: Show networks for this container
log_info "Current container networks:"
docker inspect "$CURRENT_CONTAINER" -f '{{range $k,$v := .NetworkSettings.Networks}}{{$k}} {{end}}' 2>/dev/null || echo "Unknown"

# ==============================================================================
# PHASE 5: Reporting & Log Capture
# ==============================================================================

# Capture container logs for analysis (essential for GitLab CI artifacts)
log_info "Capturing container logs for 'app'..."
mkdir -p "$PROJECT_ROOT/logs"
docker compose logs app > "$PROJECT_ROOT/logs/app_container.log"
log_info "App logs saved to logs/app_container.log"