#!/bin/bash

#=====================================#
# DEV ONLY. Do not use in production.
#=====================================#


# --- CONFIGURATION ---
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
fi

KC_URL=${KC_URL:-"http://localhost:8085"}
ADMIN_USER=${KC_ADMIN_USER:-"admin"}
ADMIN_PASS=${KC_ADMIN_PASS:-"admin"}
REALM=${KC_REALM:-"mail-and-media-shop-realm"}
CLIENT_ID=${KC_CLIENT_ID:-"mail-and-media-shop-app"}
CLIENT_SECRET=${KC_CLIENT_SECRET}

echo "--------------------------------------------------"
echo "🔧 KEYCLOAK AUTOMATIC CONFIGURATION"
echo "--------------------------------------------------"

# --- WAIT FOR DATABASE SCHEMA AND MASTER REALM ---
echo "Waiting for Keycloak database schema and 'master' realm..."
sleep 20
echo "✅ Database ready."

# --- UPDATE KEYCLOAK SSL SETTINGS ---
echo "0. Forcing Keycloak to accept HTTP (No SSL)..."
docker exec keycloak-db psql -U ${KEYCLOAK_DB_USER} -d ${KEYCLOAK_DB_NAME} -c "UPDATE realm SET ssl_required = 'NONE' WHERE name = 'master';"

echo "0. Disabling HTTPS requirement in database..."
docker exec keycloak-db psql -U ${KEYCLOAK_DB_USER} -d ${KEYCLOAK_DB_NAME} -c "UPDATE realm SET ssl_required = 'NONE';"

docker compose restart keycloak

# Wait for Keycloak to be fully up (important for schema updates)
echo "Waiting for Keycloak to restart..."
sleep 7

# 1. Get Admin Token
echo "1. Authenticating as Admin..."

sleep 30

TOKEN=$(curl -s -X POST "$KC_URL/realms/master/protocol/openid-connect/token" \
     -d "client_id=admin-cli" \
     -d "grant_type=password" \
     -d "username=$ADMIN_USER" \
     -d "password=$ADMIN_PASS" | jq -r .access_token)

if [ "$TOKEN" == "null" ] || [ -z "$TOKEN" ]; then
    echo "❌ ERROR: Could not get admin token!"
    exit 1
fi

# 2. Create Realm
echo "2. Creating Realm: $REALM..."
curl -s -X POST "$KC_URL/admin/realms" \
     -H "Authorization: Bearer $TOKEN" \
     -H "Content-Type: application/json" \
     -d "{
       \"realm\": \"$REALM\",
       \"enabled\": true,
       \"sslRequired\": \"none\"
     }"

# FORCE HTTPS DISABLEMENT FOR ALL REALMS (AFTER CREATION)
echo "2.1 Disabling HTTPS for all realms in database..."
docker exec keycloak-db psql -U ${KEYCLOAK_DB_USER} -d ${KEYCLOAK_DB_NAME} -c "UPDATE realm SET ssl_required = 'NONE';"


# 3. Create Roles
echo "3. Creating Roles: USER, ADMIN..."
for ROLE in USER ADMIN; do
  curl -s -X POST "$KC_URL/admin/realms/$REALM/roles" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{\"name\": \"$ROLE\"}"
done

# 4. Create Client
echo "4. Creating Client: $CLIENT_ID..."
curl -s -X POST "$KC_URL/admin/realms/$REALM/clients" \
  -H "Authorization: Bearer $TOKEN" \
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
  }"

# 5. Add Role Mapper (IMPORTANT for Spring Security)
echo "5. Adding Realm Role Mapper to Client..."
CLIENT_UUID=$(curl -s -X GET "$KC_URL/admin/realms/$REALM/clients?clientId=$CLIENT_ID" -H "Authorization: Bearer $TOKEN" | jq -r '.[0].id')

curl -s -X POST "$KC_URL/admin/realms/$REALM/clients/$CLIENT_UUID/protocol-mappers/models" \
  -H "Authorization: Bearer $TOKEN" \
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
  }"

# --- 5.1 SET DEFAULT ROLES FOR NEW USERS (Google/Social) ---
echo "5.1 Setting USER as default role for all new users..."

# Get the USER role object (we need its full JSON)
USER_ROLE_DATA=$(curl -s -X GET "$KC_URL/admin/realms/$REALM/roles/USER" \
     -H "Authorization: Bearer $TOKEN")

# The default role name in Keycloak is usually: default-roles-<realm-name-in-lowercase>
DEFAULT_ROLE_NAME="default-roles-$(echo $REALM | tr '[:upper:]' '[:lower:]')"

# Get the default role ID
DEFAULT_ROLE_ID=$(curl -s -X GET "$KC_URL/admin/realms/$REALM/roles/$DEFAULT_ROLE_NAME" \
     -H "Authorization: Bearer $TOKEN" | jq -r .id)

if [ "$DEFAULT_ROLE_ID" != "null" ]; then
    # Add USER role as a composite to the default role
    curl -s -X POST "$KC_URL/admin/realms/$REALM/roles-by-id/$DEFAULT_ROLE_ID/composites" \
         -H "Authorization: Bearer $TOKEN" \
         -H "Content-Type: application/json" \
         -d "[$USER_ROLE_DATA]"
    echo "5.1 ✅ Role 'USER' is now a default role for the realm."
else
    echo "5.1 ❌ ERROR: Could not find default role $DEFAULT_ROLE_NAME"
fi


# 6. Add Google Identity Provider (OPTIONAL)
if [ ! -z "$GOOGLE_CLIENT_ID" ] && [ ! -z "$GOOGLE_CLIENT_SECRET" ]; then
    echo "6. Configuring Google Identity Provider..."
    
    # Check if the provider already exists to avoid creating a duplicate
    IDP_EXISTS=$(curl -s -X GET "$KC_URL/admin/realms/$REALM/identity-provider/instances/google" \
         -H "Authorization: Bearer $TOKEN" | jq -r .alias)

    if [ "$IDP_EXISTS" == "google" ]; then
        echo "⚠️ Google IDP already exists. Skipping creation."
    else
        curl -s -X POST "$KC_URL/admin/realms/$REALM/identity-provider/instances" \
             -H "Authorization: Bearer $TOKEN" \
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
             }"
        echo "7. ✅ Google Identity Provider added successfully!"
    fi
else
    echo "⏭️ Skipping Google IDP configuration (GOOGLE_CLIENT_ID not set)."
fi


# 7. Restart Keycloak
echo ""
echo "8. Restarting Keycloak to apply changes..."
docker compose restart keycloak

echo "--------------------------------------------------"
echo "✅ CONFIGURATION COMPLETE"
echo "--------------------------------------------------"
