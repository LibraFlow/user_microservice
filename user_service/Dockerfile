FROM gradle:8.6.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/app/build/libs/*.jar app.jar

ENV PORT=8087
ENV SPRING_PROFILES_ACTIVE=prod
ENV INSTANCE_CONNECTION_NAME=libraflow-457510:europe-west4:user-service-db
ENV SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
ENV SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
# ENV SPRING_DATASOURCE_DATABASE=db6
ENV SPRING_DATASOURCE_DATABASE=${SPRING_DATASOURCE_DATABASE}
ENV KAFKA_BOOTSTRAP_SERVERS=${KAFKA_BOOTSTRAP_SERVERS}
ENV KAFKA_AUTO_CONFIGURE_EXCLUDE=${KAFKA_AUTO_CONFIGURE_EXCLUDE}

EXPOSE 8087

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8087/actuator/health || exit 1
    
# Create startup script
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'echo "Starting application with environment variables:"' >> /app/start.sh && \
    echo 'echo "PORT: $PORT"' >> /app/start.sh && \
    echo 'echo "SPRING_PROFILES_ACTIVE: $SPRING_PROFILES_ACTIVE"' >> /app/start.sh && \
    echo 'echo "INSTANCE_CONNECTION_NAME: $INSTANCE_CONNECTION_NAME"' >> /app/start.sh && \
    echo 'echo "SPRING_DATASOURCE_DATABASE: $SPRING_DATASOURCE_DATABASE"' >> /app/start.sh && \
    echo 'echo "KAFKA_BOOTSTRAP_SERVERS: $KAFKA_BOOTSTRAP_SERVERS"' >> /app/start.sh && \
    echo 'echo "KAFKA_AUTO_CONFIGURE_EXCLUDE: $KAFKA_AUTO_CONFIGURE_EXCLUDE"' >> /app/start.sh && \
    echo 'exec java -Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh
    
CMD ["/app/start.sh"] 