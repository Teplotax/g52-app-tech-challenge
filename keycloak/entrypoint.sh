#!/bin/sh
set -e

sed \
  -e "s|\${KEYCLOAK_CLIENT_ID}|${KEYCLOAK_CLIENT_ID}|g" \
  -e "s|\${KEYCLOAK_CLIENT_SECRET}|${KEYCLOAK_CLIENT_SECRET}|g" \
  /opt/keycloak/g52-realm.json.template > /opt/keycloak/data/import/g52-realm.json

exec /opt/keycloak/bin/kc.sh start-dev --import-realm
