# Stage 1: Resolve and download dependencies
FROM eclipse-temurin:21-jdk-jammy as deps

WORKDIR /build

# Copy the mvnw wrapper with executable permissions
COPY --chmod=0755 mvnw mvnw
COPY .mvn/ .mvn/

# Download dependencies as a separate step to take advantage of Docker's caching
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 ./mvnw dependency:go-offline -DskipTests

# Stage 2: Build the application
FROM deps as package

WORKDIR /build

COPY ./src src/
RUN --mount=type=bind,source=pom.xml,target=pom.xml \
    --mount=type=cache,target=/root/.m2 \
    ./mvnw package -DskipTests && \
    mv target/$(./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout)-$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout).jar target/app.jar

# Stage 3: Extract the application into separate layers
FROM package as extract

WORKDIR /build

RUN java -Djarmode=layertools -jar target/app.jar extract --destination target/extracted

# Stage 4: Final stage for running the application
FROM eclipse-temurin:21-jre-jammy AS final

# Create a non-privileged user
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

# Copy the application.properties file into the container
COPY src/main/resources/application.properties /app/application.properties

# Copy the extracted layers from the "extract" stage
COPY --from=extract build/target/extracted/dependencies/ ./
COPY --from=extract build/target/extracted/spring-boot-loader/ ./
COPY --from=extract build/target/extracted/snapshot-dependencies/ ./
COPY --from=extract build/target/extracted/application/ ./

# Expose the application port
EXPOSE 8080

# Set environment variables for Firebase and MongoDB
ENV FIREBASE_SERVICE_ACCOUNT_FILE_PATH=/app/config/key/chatwithme-033-firebase-adminsdk-stxjp-4933c87581.json
ENV SPRING_DATA_MONGODB_URI=mongodb+srv://root:root@chatapp.axbza.mongodb.net/chatapp?retryWrites=true&w=majority
ENV SPRING_DATA_MONGODB_DATABASE=chatapp

# Run the application
ENTRYPOINT [ "java", "org.springframework.boot.loader.launch.JarLauncher" ]