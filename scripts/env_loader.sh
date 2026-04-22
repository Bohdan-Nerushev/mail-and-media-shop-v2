#!/bin/bash
# Safe environment variable loader that respects existing environment variables (CI/CD)

load_env_safe() {
    local env_file="$1"
    if [ -f "$env_file" ]; then
        log_info "Loading environment variables from $(basename "$env_file") (safe mode)"
        while IFS='=' read -r key value || [ -n "$key" ]; do
            # Skip comments and empty lines
            [[ "$key" =~ ^#.*$ ]] && continue
            [[ -z "$key" ]] && continue
            
            # Trim possible whitespace
            key=$(echo "$key" | xargs)
            value=$(echo "$value" | xargs)
            
            # Export if the variable is NOT set OR if it is EMPTY
            if [ -z "${!key+x}" ] || [ -z "${!key}" ]; then
                export "$key=$value"
            else
                log_info "Skipping $key: already set in environment (CI/CD priority)"
            fi
        done < "$env_file"
    fi
}

# Run it for the project root .env
if [ -n "$PROJECT_ROOT" ]; then
    load_env_safe "$PROJECT_ROOT/.env"
else
    load_env_safe ".env"
fi
