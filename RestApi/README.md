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
    1. Generate your own
        * Use the command "keytool -genkey -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650". Remember your password, make sure the file is in the same directory as the JAR file, and also make sure that you set the hostname the same as the server you will be running this application on. The keytool may ask "What is your first and last name?"--the hostname should go here. 
        * You'll also want to generate a .crt file which will need to be added to the NotificationService Java CA store . You can do this using the command "openssl pkcs12 -in keystore.p12 -clcerts -nokeys -out keystore.crt". Then add the file to your JVM's cacerts file using the commend: "sudo keytool -importcert -file keystore.crt -alias stocknotificationsrestapi -keystore $(/usr/libexec/java_home)/jre/lib/security/cacerts -storepass changeit". Note that the alias in the Java CA store is set to "stocknotificationsrestapi"
        * If you make a mistake in the above step, you can remove the certificate from the Java CA store using the command "sudo keytool -delete -alias stocknotificationsrestapi -keystore $(/usr/libexec/java_home)/jre/lib/security/cacerts -storepass changeit"
        * Note that that server.ssl.key-store-password in the application.properties file must match the password you used when you generated the certificate
    2. Use Existing Certificate: Update SSL parameters under SSL Certificate accordingly in: src/main/resouces/application.properties
6. Use "gradlew bootRun" to compile and run, or "gradlew build" to build an executable JAR file

For more instructions on running the REST API as a service, or if you are having trouble executing the JAR file, see: https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html

# RunniUng

Compile and run the application using `./gradlew bootRun`

Build the JAR file using `./gradlew build` then run the JAR file using `./build/libs/RestApi-0.0.1-SNAPSHOT.jar`
* Please note that the JAR file is "executable" because we have enabled the "executable" option in build.gradle. If this does not work on your system, feel free to remove this from build.gradle and execute the application with the command `java -jar build/libs/RestApi-0.0.1-SNAPSHOT.jar`