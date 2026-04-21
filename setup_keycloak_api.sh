#!/bin/bash

#=====================================#
# DEV ONLY. Do not use in production.
#=====================================#

set -euo pipefail

# --------------------------------------------------
# [00] LOAD ENVIRONMENT CONFIGURATION
# --------------------------------------------------
if [ -f .env ]; then
  set -a
  . ./.env
  set +a
fi

KC_URL=${KC_URL:-"http://localhost:8085"}
ADMIN_USER=${KC_ADMIN_USER:-"admin"}
ADMIN_PASS=${KC_ADMIN_PASS:-"admin"}

REALM=${KC_REALM:-"mail-and-media-shop-realm"}
CLIENT_ID=${KC_CLIENT_ID:-"mail-and-media-shop-app"}
CLIENT_SECRET=${KC_CLIENT_SECRET:-""}

USER_NAME=${USER_NAME:-"postman_user2"}
USER_EMAIL=${USER_EMAIL:-"postman2@example.com"}
USER_PASSWORD=${USER_PASSWORD:-"password123"}

echo "--------------------------------------------------"
echo "🔧 KEYCLOAK AUTOMATIC CONFIGURATION"
echo "--------------------------------------------------"

# --------------------------------------------------
# [01] VALIDATE REQUIRED ENV VARIABLES
# --------------------------------------------------
echo "[01] Validating required environment variables..."

if [ -z "$CLIENT_SECRET" ]; then
  echo "❌ KC_CLIENT_SECRET is not set"
  exit 1
fi

if [ -z "${KEYCLOAK_DB_USER:-}" ] || [ -z "${KEYCLOAK_DB_NAME:-}" ]; then
  echo "❌ KEYCLOAK_DB_USER or KEYCLOAK_DB_NAME is not set"
  exit 1
fi

sleep 20

echo "✅ Required environment variables are present"

# --------------------------------------------------
# [02] WAIT FOR DATABASE READINESS
# --------------------------------------------------
echo "[02] Waiting for Keycloak database readiness..."

until docker exec keycloak-db psql -U "$KEYCLOAK_DB_USER" -d "$KEYCLOAK_DB_NAME" -c "SELECT 1;" >/dev/null 2>&1; do
  echo "⏳ Database is not ready yet..."
  sleep 3
done

echo "✅ Database is ready"

# --------------------------------------------------
# [03] FORCE HTTP / DISABLE SSL IN DATABASE
# --------------------------------------------------
echo "[03] Forcing Keycloak to accept HTTP (No SSL)..."

docker exec keycloak-db psql -U "$KEYCLOAK_DB_USER" -d "$KEYCLOAK_DB_NAME" \
  -c "UPDATE realm SET ssl_required = 'NONE' WHERE name = 'master';"

docker exec keycloak-db psql -U "$KEYCLOAK_DB_USER" -d "$KEYCLOAK_DB_NAME" \
  -c "UPDATE realm SET ssl_required = 'NONE';"

echo "✅ SSL requirement disabled in database"

# --------------------------------------------------
# [04] RESTART KEYCLOAK
# --------------------------------------------------
echo "[04] Restarting Keycloak..."
docker compose restart keycloak

# --------------------------------------------------
# [05] WAIT FOR KEYCLOAK READINESS
# --------------------------------------------------
echo "[05] Waiting for Keycloak readiness..."

until curl -s -f "$KC_URL/realms/master/.well-known/openid-configuration" >/dev/null 2>&1; do
  echo "⏳ Keycloak is not ready yet..."
  sleep 5
done

echo "✅ Keycloak is ready"

# --------------------------------------------------
# [06] AUTHENTICATE AS ADMIN
# --------------------------------------------------
echo "[06] Authenticating as admin..."

ADMIN_TOKEN_RESPONSE=$(curl -sS -X POST "$KC_URL/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=admin-cli" \
  -d "grant_type=password" \
  -d "username=$ADMIN_USER" \
  -d "password=$ADMIN_PASS")

ADMIN_TOKEN=$(echo "$ADMIN_TOKEN_RESPONSE" | jq -r '.access_token // empty')

if [ -z "$ADMIN_TOKEN" ]; then
  echo "❌ Failed to retrieve admin token"
  echo "Keycloak response:"
  echo "$ADMIN_TOKEN_RESPONSE" | jq . 2>/dev/null || echo "$ADMIN_TOKEN_RESPONSE"
  exit 1
fi

echo "✅ Admin token received"

# --------------------------------------------------
# [07] CREATE REALM
# --------------------------------------------------
echo "[07] Creating realm: $REALM..."

REALM_CREATE_RESPONSE=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" -X POST "$KC_URL/admin/realms" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"realm\": \"$REALM\",
    \"enabled\": true,
    \"sslRequired\": \"none\"
  }")

REALM_CREATE_BODY=$(printf '%s' "$REALM_CREATE_RESPONSE" | sed '$d')
REALM_CREATE_STATUS=$(printf '%s' "$REALM_CREATE_RESPONSE" | tail -n1 | sed 's/HTTP_STATUS://')

if [ "$REALM_CREATE_STATUS" = "201" ]; then
  echo "✅ Realm created"
elif [ "$REALM_CREATE_STATUS" = "409" ]; then
  echo "⚠️ Realm already exists"
  [ -n "$REALM_CREATE_BODY" ] && (echo "$REALM_CREATE_BODY" | jq . 2>/dev/null || echo "$REALM_CREATE_BODY")
else
  echo "❌ Failed to create realm"
  echo "HTTP status: $REALM_CREATE_STATUS"
  [ -n "$REALM_CREATE_BODY" ] && (echo "$REALM_CREATE_BODY" | jq . 2>/dev/null || echo "$REALM_CREATE_BODY")
  exit 1
fi

# --------------------------------------------------
# [08] FORCE HTTP AGAIN AFTER REALM CREATION
# --------------------------------------------------
echo "[08] Disabling HTTPS requirement for all realms in database again..."

docker exec keycloak-db psql -U "$KEYCLOAK_DB_USER" -d "$KEYCLOAK_DB_NAME" \
  -c "UPDATE realm SET ssl_required = 'NONE';"

echo "✅ HTTPS requirement disabled again"

# --------------------------------------------------
# [09] CREATE REALM ROLES
# --------------------------------------------------
echo "[09] Creating realm roles: USER, ADMIN..."

for ROLE in USER ADMIN; do
  ROLE_CREATE_RESPONSE=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" -X POST "$KC_URL/admin/realms/$REALM/roles" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{\"name\": \"$ROLE\"}")

  ROLE_CREATE_BODY=$(printf '%s' "$ROLE_CREATE_RESPONSE" | sed '$d')
  ROLE_CREATE_STATUS=$(printf '%s' "$ROLE_CREATE_RESPONSE" | tail -n1 | sed 's/HTTP_STATUS://')

  if [ "$ROLE_CREATE_STATUS" = "201" ]; then
    echo "✅ Role '$ROLE' created"
  elif [ "$ROLE_CREATE_STATUS" = "409" ]; then
    echo "⚠️ Role '$ROLE' already exists"
    [ -n "$ROLE_CREATE_BODY" ] && (echo "$ROLE_CREATE_BODY" | jq . 2>/dev/null || echo "$ROLE_CREATE_BODY")
  else
    echo "❌ Failed to create role '$ROLE'"
    echo "HTTP status: $ROLE_CREATE_STATUS"
    [ -n "$ROLE_CREATE_BODY" ] && (echo "$ROLE_CREATE_BODY" | jq . 2>/dev/null || echo "$ROLE_CREATE_BODY")
    exit 1
  fi
done

# --------------------------------------------------
# [10] CREATE CLIENT
# --------------------------------------------------
echo "[10] Creating client: $CLIENT_ID..."

CLIENT_CREATE_RESPONSE=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" -X POST "$KC_URL/admin/realms/$REALM/clients" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"clientId\": \"$CLIENT_ID\",
    \"enabled\": true,
    \"protocol\": \"openid-connect\",
    \"publicClient\": false,
    \"secret\": \"$CLIENT_SECRET\",
    \"directAccessGrantsEnabled\": true,
    \"serviceAccountsEnabled\": true,
    \"redirectUris\": [\"*\"],
    \"webOrigins\": [\"*\"]
  }")

CLIENT_CREATE_BODY=$(printf '%s' "$CLIENT_CREATE_RESPONSE" | sed '$d')
CLIENT_CREATE_STATUS=$(printf '%s' "$CLIENT_CREATE_RESPONSE" | tail -n1 | sed 's/HTTP_STATUS://')

if [ "$CLIENT_CREATE_STATUS" = "201" ]; then
  echo "✅ Client created"
elif [ "$CLIENT_CREATE_STATUS" = "409" ]; then
  echo "⚠️ Client already exists"
  [ -n "$CLIENT_CREATE_BODY" ] && (echo "$CLIENT_CREATE_BODY" | jq . 2>/dev/null || echo "$CLIENT_CREATE_BODY")
else
  echo "❌ Failed to create client"
  echo "HTTP status: $CLIENT_CREATE_STATUS"
  [ -n "$CLIENT_CREATE_BODY" ] && (echo "$CLIENT_CREATE_BODY" | jq . 2>/dev/null || echo "$CLIENT_CREATE_BODY")
  exit 1
fi

# --------------------------------------------------
# [11] FETCH CLIENT UUID
# --------------------------------------------------
echo "[11] Fetching client UUID..."

CLIENT_UUID_RESPONSE=$(curl -sS -X GET "$KC_URL/admin/realms/$REALM/clients?clientId=$CLIENT_ID" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

CLIENT_UUID=$(echo "$CLIENT_UUID_RESPONSE" | jq -r '.[0].id // empty')

if [ -z "$CLIENT_UUID" ]; then
  echo "❌ Failed to retrieve client UUID"
  echo "Keycloak response:"
  echo "$CLIENT_UUID_RESPONSE" | jq . 2>/dev/null || echo "$CLIENT_UUID_RESPONSE"
  exit 1
fi

echo "✅ Client UUID: $CLIENT_UUID"

# --------------------------------------------------
# [12] ADD REALM ROLE MAPPER TO CLIENT
# --------------------------------------------------
echo "[12] Adding realm role mapper to client..."

MAPPER_CREATE_RESPONSE=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" -X POST "$KC_URL/admin/realms/$REALM/clients/$CLIENT_UUID/protocol-mappers/models" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"realm roles mapper\",
    \"protocol\": \"openid-connect\",
    \"protocolMapper\": \"oidc-usermodel-realm-role-mapper\",
    \"config\": {
      \"claim.name\": \"roles\",
      \"jsonType.label\": \"String\",
      \"multivalued\": \"true\",
      \"access.token.claim\": \"true\",
      \"id.token.claim\": \"true\",
      \"userinfo.token.claim\": \"true\"
    }
  }")

MAPPER_CREATE_BODY=$(printf '%s' "$MAPPER_CREATE_RESPONSE" | sed '$d')
MAPPER_CREATE_STATUS=$(printf '%s' "$MAPPER_CREATE_RESPONSE" | tail -n1 | sed 's/HTTP_STATUS://')

if [ "$MAPPER_CREATE_STATUS" = "201" ]; then
  echo "✅ Realm role mapper added"
elif [ "$MAPPER_CREATE_STATUS" = "409" ]; then
  echo "⚠️ Realm role mapper already exists"
  [ -n "$MAPPER_CREATE_BODY" ] && (echo "$MAPPER_CREATE_BODY" | jq . 2>/dev/null || echo "$MAPPER_CREATE_BODY")
else
  echo "❌ Failed to add realm role mapper"
  echo "HTTP status: $MAPPER_CREATE_STATUS"
  [ -n "$MAPPER_CREATE_BODY" ] && (echo "$MAPPER_CREATE_BODY" | jq . 2>/dev/null || echo "$MAPPER_CREATE_BODY")
  exit 1
fi

# --------------------------------------------------
# [13] SET USER ROLE AS DEFAULT FOR NEW USERS
# --------------------------------------------------
echo "[13] Setting USER as default role for all new users..."

USER_ROLE_DATA=$(curl -sS -X GET "$KC_URL/admin/realms/$REALM/roles/USER" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

DEFAULT_ROLE_NAME="default-roles-$(echo "$REALM" | tr '[:upper:]' '[:lower:]')"

DEFAULT_ROLE_RESPONSE=$(curl -sS -X GET "$KC_URL/admin/realms/$REALM/roles/$DEFAULT_ROLE_NAME" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

DEFAULT_ROLE_ID=$(echo "$DEFAULT_ROLE_RESPONSE" | jq -r '.id // empty')

if [ -n "$DEFAULT_ROLE_ID" ]; then
  DEFAULT_COMPOSITE_RESPONSE=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" -X POST "$KC_URL/admin/realms/$REALM/roles-by-id/$DEFAULT_ROLE_ID/composites" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "[$USER_ROLE_DATA]")

  DEFAULT_COMPOSITE_BODY=$(printf '%s' "$DEFAULT_COMPOSITE_RESPONSE" | sed '$d')
  DEFAULT_COMPOSITE_STATUS=$(printf '%s' "$DEFAULT_COMPOSITE_RESPONSE" | tail -n1 | sed 's/HTTP_STATUS://')

  if [ "$DEFAULT_COMPOSITE_STATUS" = "204" ]; then
    echo "✅ Role 'USER' is now a default role for the realm"
  elif [ "$DEFAULT_COMPOSITE_STATUS" = "409" ]; then
    echo "⚠️ Role 'USER' is already part of the default role"
    [ -n "$DEFAULT_COMPOSITE_BODY" ] && (echo "$DEFAULT_COMPOSITE_BODY" | jq . 2>/dev/null || echo "$DEFAULT_COMPOSITE_BODY")
  else
    echo "❌ Failed to add USER role to default role"
    echo "HTTP status: $DEFAULT_COMPOSITE_STATUS"
    [ -n "$DEFAULT_COMPOSITE_BODY" ] && (echo "$DEFAULT_COMPOSITE_BODY" | jq . 2>/dev/null || echo "$DEFAULT_COMPOSITE_BODY")
    exit 1
  fi
else
  echo "❌ Could not find default role: $DEFAULT_ROLE_NAME"
  echo "Keycloak response:"
  echo "$DEFAULT_ROLE_RESPONSE" | jq . 2>/dev/null || echo "$DEFAULT_ROLE_RESPONSE"
  exit 1
fi

# --------------------------------------------------
# [14] CONFIGURE GOOGLE IDENTITY PROVIDER (OPTIONAL)
# --------------------------------------------------
if [ -n "${GOOGLE_CLIENT_ID:-}" ] && [ -n "${GOOGLE_CLIENT_SECRET:-}" ]; then
  echo "[14] Configuring Google Identity Provider..."

  IDP_EXISTS_RESPONSE=$(curl -sS -X GET "$KC_URL/admin/realms/$REALM/identity-provider/instances/google" \
    -H "Authorization: Bearer $ADMIN_TOKEN")

  IDP_EXISTS=$(echo "$IDP_EXISTS_RESPONSE" | jq -r '.alias // empty')

  if [ "$IDP_EXISTS" = "google" ]; then
    echo "⚠️ Google IDP already exists. Skipping creation."
  else
    GOOGLE_IDP_CREATE_RESPONSE=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" -X POST "$KC_URL/admin/realms/$REALM/identity-provider/instances" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H "Content-Type: application/json" \
      -d "{
        \"alias\": \"google\",
        \"displayName\": \"Google\",
        \"providerId\": \"google\",
        \"enabled\": true,
        \"trustEmail\": true,
        \"storeToken\": false,
        \"addReadTokenRoleOnCreate\": false,
        \"authenticateByDefault\": false,
        \"linkOnly\": false,
        \"firstBrokerLoginFlowAlias\": \"first broker login\",
        \"config\": {
          \"clientId\": \"$GOOGLE_CLIENT_ID\",
          \"clientSecret\": \"$GOOGLE_CLIENT_SECRET\",
          \"useJwksUrl\": \"true\",
          \"defaultScope\": \"openid email profile\"
        }
      }")

    GOOGLE_IDP_CREATE_BODY=$(printf '%s' "$GOOGLE_IDP_CREATE_RESPONSE" | sed '$d')
    GOOGLE_IDP_CREATE_STATUS=$(printf '%s' "$GOOGLE_IDP_CREATE_RESPONSE" | tail -n1 | sed 's/HTTP_STATUS://')

    if [ "$GOOGLE_IDP_CREATE_STATUS" = "201" ]; then
      echo "✅ Google Identity Provider added successfully"
    else
      echo "❌ Failed to create Google Identity Provider"
      echo "HTTP status: $GOOGLE_IDP_CREATE_STATUS"
      [ -n "$GOOGLE_IDP_CREATE_BODY" ] && (echo "$GOOGLE_IDP_CREATE_BODY" | jq . 2>/dev/null || echo "$GOOGLE_IDP_CREATE_BODY")
      exit 1
    fi
  fi
else
  echo "[14] Skipping Google IDP configuration (GOOGLE_CLIENT_ID / GOOGLE_CLIENT_SECRET not set)"
fi

# --------------------------------------------------
# [15] CREATE USER
# --------------------------------------------------
echo "[15] Creating user..."

USER_CREATE_RESPONSE=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" -X POST "$KC_URL/admin/realms/$REALM/users" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$USER_NAME\",
    \"firstName\": \"John\",
    \"lastName\": \"Doe\",
    \"email\": \"$USER_EMAIL\",
    \"enabled\": true,
    \"credentials\": [{
      \"type\": \"password\",
      \"value\": \"$USER_PASSWORD\",
      \"temporary\": false
    }]
  }")

USER_CREATE_BODY=$(printf '%s' "$USER_CREATE_RESPONSE" | sed '$d')
USER_CREATE_STATUS=$(printf '%s' "$USER_CREATE_RESPONSE" | tail -n1 | sed 's/HTTP_STATUS://')

if [ "$USER_CREATE_STATUS" = "201" ]; then
  echo "✅ User created"
elif [ "$USER_CREATE_STATUS" = "409" ]; then
  echo "⚠️ User already exists"
  [ -n "$USER_CREATE_BODY" ] && (echo "$USER_CREATE_BODY" | jq . 2>/dev/null || echo "$USER_CREATE_BODY")
else
  echo "❌ Failed to create user"
  echo "HTTP status: $USER_CREATE_STATUS"
  [ -n "$USER_CREATE_BODY" ] && (echo "$USER_CREATE_BODY" | jq . 2>/dev/null || echo "$USER_CREATE_BODY")
  exit 1
fi

# --------------------------------------------------
# [16] FETCH USER ID
# --------------------------------------------------
echo "[16] Fetching user ID..."

USER_ID_RESPONSE=$(curl -sS -X GET "$KC_URL/admin/realms/$REALM/users?username=$USER_NAME" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

USER_ID=$(echo "$USER_ID_RESPONSE" | jq -r '.[0].id // empty')

if [ -z "$USER_ID" ]; then
  echo "❌ Failed to retrieve user ID"
  echo "Keycloak response:"
  echo "$USER_ID_RESPONSE" | jq . 2>/dev/null || echo "$USER_ID_RESPONSE"
  exit 1
fi

echo "✅ User ID: $USER_ID"

# --------------------------------------------------
# [17] FETCH USER ROLE ID
# --------------------------------------------------
echo "[17] Fetching role ID for USER..."

ROLE_ID_RESPONSE=$(curl -sS -X GET "$KC_URL/admin/realms/$REALM/roles/USER" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

ROLE_ID=$(echo "$ROLE_ID_RESPONSE" | jq -r '.id // empty')

if [ -z "$ROLE_ID" ]; then
  echo "❌ Failed to retrieve role ID"
  echo "Keycloak response:"
  echo "$ROLE_ID_RESPONSE" | jq . 2>/dev/null || echo "$ROLE_ID_RESPONSE"
  exit 1
fi

echo "✅ Role ID: $ROLE_ID"

# --------------------------------------------------
# [18] ASSIGN ROLE TO USER
# --------------------------------------------------
echo "[18] Assigning USER role to user..."

ROLE_ASSIGN_RESPONSE=$(curl -sS -w "\nHTTP_STATUS:%{http_code}" -X POST "$KC_URL/admin/realms/$REALM/users/$USER_ID/role-mappings/realm" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "[{
    \"id\": \"$ROLE_ID\",
    \"name\": \"USER\"
  }]")

ROLE_ASSIGN_BODY=$(printf '%s' "$ROLE_ASSIGN_RESPONSE" | sed '$d')
ROLE_ASSIGN_STATUS=$(printf '%s' "$ROLE_ASSIGN_RESPONSE" | tail -n1 | sed 's/HTTP_STATUS://')

if [ "$ROLE_ASSIGN_STATUS" = "204" ]; then
  echo "✅ Role assigned"
else
  echo "❌ Failed to assign role"
  echo "HTTP status: $ROLE_ASSIGN_STATUS"
  [ -n "$ROLE_ASSIGN_BODY" ] && (echo "$ROLE_ASSIGN_BODY" | jq . 2>/dev/null || echo "$ROLE_ASSIGN_BODY")
  exit 1
fi

# --------------------------------------------------
# [19] LOGIN AS USER AND GET USER TOKEN
# --------------------------------------------------
echo "[19] Logging in as user..."

USER_TOKEN_RESPONSE=$(curl -sS -X POST "$KC_URL/realms/$REALM/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=$CLIENT_ID" \
  -d "client_secret=$CLIENT_SECRET" \
  -d "grant_type=password" \
  -d "username=$USER_NAME" \
  -d "password=$USER_PASSWORD")

USER_TOKEN=$(echo "$USER_TOKEN_RESPONSE" | jq -r '.access_token // empty')

if [ -z "$USER_TOKEN" ]; then
  echo "❌ Failed to retrieve user token"
  echo "Keycloak response:"
  echo "$USER_TOKEN_RESPONSE" | jq . 2>/dev/null || echo "$USER_TOKEN_RESPONSE"
  exit 1
fi

echo "✅ User token received"

# --------------------------------------------------
# [20] RESULT OUTPUT
# --------------------------------------------------
echo "=================================================="
echo "🎉 FLOW COMPLETED SUCCESSFULLY"
echo "User Token (truncated): ${USER_TOKEN:0:30}..."
echo "=================================================="
echo "--------------------------------------------------"
echo "✅ CONFIGURATION COMPLETE"
echo "--------------------------------------------------"

# --------------------------------------------------
# [21] RESTART KEYCLOAK TO APPLY CHANGES
# --------------------------------------------------
echo "[21] Restarting Keycloak to apply changes..."
docker compose restart keycloak

# --------------------------------------------------
# [22] WAIT FOR KEYCLOAK READINESS AFTER RESTART
# --------------------------------------------------
echo "[22] Waiting for Keycloak after final restart..."

until curl -s -f "$KC_URL/realms/master/.well-known/openid-configuration" >/dev/null 2>&1; do
  echo "⏳ Keycloak is not ready yet after restart..."
  sleep 5
done

echo "✅ Keycloak is ready after restart"
echo "=================================================="
echo "🚀 KEYCLOAK AUTOMATION FINISHED"
echo "=================================================="