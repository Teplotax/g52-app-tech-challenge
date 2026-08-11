FROM quay.io/keycloak/keycloak:24.0
COPY g52-realm.json /opt/keycloak/g52-realm.json.template
COPY entrypoint.sh /opt/keycloak/entrypoint.sh
ENTRYPOINT ["/bin/sh", "/opt/keycloak/entrypoint.sh"]
