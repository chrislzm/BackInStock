Important: Be sure to update the following properties in src/main/resources/application.properties before running this application:
1. security.user.name
2. security.user.password
3. server.ssl.key-store-password

# Installation Notes

1. Install MongoDB service
2. Install Java
3. Install Gradle
4. Open network ports 8080 and 8090 for inbound connections
5. Setup SSL in one of the following ways:
 1. Quick method: Generate a user certificate using the command "keytool -genkey -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650", and make sure the file is in the same directory as the JAR file
 2. Use Existing Certificate: Update SSL parameters under SSL Certificate accordingly in: src/main/resouces/application.properties
6. Use "gradlew bootRun" to compile and run, or "gradlew build" to build an executable JAR file

For more instructions on running the REST API as a service, or if you are having trouble executing the JAR file, see: https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html
