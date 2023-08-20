# Use a base image with the appropriate JDK and sbt installed
FROM hseeberger/scala-sbt:17.0.8_1.5.0_2.13.5

# Set the working directory inside the container
WORKDIR /social-media-app-scala

# Copy the project files to the container's working directory
COPY . .

# Build the Play application inside the container
RUN sbt stage

# Expose the port that the Play application will run on
EXPOSE 9000

# Start the Play application when the container starts
CMD ["target/universal/stage/bin/social-media-app-scala", "-Dplay.http.secret.key=changeme"]
