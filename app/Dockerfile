FROM eclipse-temurin:21-jre

RUN apt-get update && apt-get install -y curl unzip && rm -rf /var/lib/apt/lists/*

# agente java do new relic (apm); só liga com NEW_RELIC_AGENT_ENABLED=true + license key (vem do k8s)
RUN curl -sSfL https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip -o /tmp/newrelic.zip \
    && unzip -q /tmp/newrelic.zip -d / \
    && rm /tmp/newrelic.zip
ENV NEW_RELIC_AGENT_ENABLED=false \
    NEW_RELIC_LOG_FILE_NAME=STDOUT

WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-javaagent:/newrelic/newrelic.jar", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
