# Use official Oracle OpenJDK 21 JDK image as the base image for the build stage
FROM openjdk:21-jdk-oracle as build

# Set working directory inside the container to /app
WORKDIR /app

# Copy the built JAR file from the target folder on the host machine into the container at /app/app.jar
COPY target/*.jar /app/app.jar

# Expose port 8080 so it can be accessed from outside the container
EXPOSE 8080

# Define the default command to run when the container starts
# It runs the Java application by executing the JAR file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]