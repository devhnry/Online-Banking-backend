# Use an official OpenJDK 21 image as a parent image
FROM eclipse-temurin:21-jdk-alpine
# Set the working directory inside the container
WORKDIR /app
# Copy the compiled JAR file from the local machine to the container
COPY target/Online-BankingSystem-0.0.1-SNAPSHOT.jar /app/onlinebanking.jar
# Expose port 8080 if your app runs on this port
EXPOSE 6010
# Run the JAR file
CMD ["java", "-jar", "/app/onlinebanking.jar"]

# Define build arguments
ARG DB_USERNAME
ARG DB_PASSWORD

# Set environment variables from build arguments
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}