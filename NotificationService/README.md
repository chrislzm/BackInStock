# Installation Notes

* Rename application.properties.blank to application.properties and add/update values

# Enabling HTTPS

To enable HTTPS please ensure:
1. The REST API URL in application.properties has "https" as the protocol identifier (not http)
2. If you have a self-signed certificate it will need to be added to your Java runtime's CA store. For information on how to do this, please see the [REST API server README file](../RestApi/README.md)

# Usage

Compile and run the application using `./gradlew bootRun`

Build the JAR file using `./gradlew build` then run the JAR file using `./build/libs/notification-service-0.1.0.jar`
* Please note that the JAR file is "executable" because we have enabled the "executable" option in build.gradle. If this does not work on your system, feel free to remove this from build.gradle and execute the application with the command `java -jar build/libs/notification-service-0.1.0.jar`