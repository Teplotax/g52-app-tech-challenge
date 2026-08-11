#!/bin/bash
set -e

template="/opt/keycloak/g52-realm.json.template"
output="/opt/keycloak/data/import/g52-realm.json"

content="$(cat "$template")"
content="${content//\$\{KEYCLOAK_CLIENT_ID\}/$KEYCLOAK_CLIENT_ID}"
content="${content//\$\{KEYCLOAK_CLIENT_SECRET\}/$KEYCLOAK_CLIENT_SECRET}"
printf '%s' "$content" > "$output"

exec /opt/keycloak/bin/kc.sh start-dev --import-realm
