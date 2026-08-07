FROM quay.io/keycloak/keycloak:24.0
COPY g52-realm.json /opt/keycloak/data/import/g52-realm.json
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev", "--import-realm"]
