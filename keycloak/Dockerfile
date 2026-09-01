FROM quay.io/keycloak/keycloak:24.0
USER root
COPY g52-realm.json /opt/keycloak/g52-realm.json.template
COPY entrypoint.sh /opt/keycloak/entrypoint.sh
RUN mkdir -p /opt/keycloak/data/import \
    && chmod -R a+rwX /opt/keycloak/data/import /opt/keycloak/g52-realm.json.template /opt/keycloak/entrypoint.sh
USER 1000
ENTRYPOINT ["/bin/bash", "/opt/keycloak/entrypoint.sh"]
