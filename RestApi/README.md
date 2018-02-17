# Introduction

This application implements a REST API for a MongoDB database that contains all stock notification information. This database is the primary data store and source of truth for the state of each notification.

The database contains a single collection, and each object in the collection contains seven key/value mappings:
* _id: The unique ID for the object
* _class: The fully qualified Java class name
* email: Email address of the customer that requested the notification
* variantId: The product variant ID that is out stock/this notification is for
* createdDate: The date this notification was created
* sent (boolean): Whether an email notification has been sent to the customer
* sentDate: The date the notification was emailed to the customer

# Installation Notes

1. Install MongoDB service
2. Install Java
3. Install Gradle
4. Open network ports 8080 and 8090 for inbound connections
5. Setup SSL in one of the following ways:
    1. Generate your own
        * Use the command `keytool -genkey -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650`. Remember your password, make sure the file is in the same directory as the JAR file, and also make sure that you set the hostname the same as the server you will be running this application on. The keytool may ask "What is your first and last name?"--the hostname should go here. 
        * You'll also want to generate a .crt file which will need to be added to the NotificationService Java CA store . You can do this using the command `openssl pkcs12 -in keystore.p12 -clcerts -nokeys -out keystore.crt`. Then add the file to your JVM's cacerts file using the commend: `sudo keytool -importcert -file keystore.crt -alias stocknotificationsrestapi -keystore $(/usr/libexec/java_home)/jre/lib/security/cacerts -storepass changeit`. Note that the alias in the Java CA store is set to `stocknotificationsrestapi`
        * If you make a mistake in the above step, you can remove the certificate from the Java CA store using the command `sudo keytool -delete -alias stocknotificationsrestapi -keystore $(/usr/libexec/java_home)/jre/lib/security/cacerts -storepass changeit`
        * Note that `server.ssl.key-store-password` in the application.properties file must match the password you used when you generated the certificate
    2. Create/Use Existing Certificate: Update SSL parameters under SSL Certificate accordingly in: src/main/resouces/application.properties
        * Visit https://letsencrypt.org for information on getting a free SSL certificate
6. Rename application.properties.blank to application.properties and add/update values

For more instructions on running the REST API as a service, or if you are having trouble executing the JAR file, see: https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html

# Running

Compile and run the application using `./gradlew bootRun`

Build the JAR file using `./gradlew build` then run the JAR file using `./build/libs/RestApi-0.0.1-SNAPSHOT.jar`
* Please note that the JAR file is "executable" because we have enabled the "executable" option in build.gradle. If this does not work on your system, feel free to remove this from build.gradle and execute the application with the command `java -jar build/libs/RestApi-0.0.1-SNAPSHOT.jar`