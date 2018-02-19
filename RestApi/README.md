# Back in Stock - Database Server REST API

This Java application implements a REST API for a MongoDB server that contains all stock notification information. This database is the primary data store and source of truth for the state of all notifications.

### API Endpoints

| Method | EndPoint            | Parameters              | Auth | Description                       |
|--------|---------------------|-------------------------|------|-----------------------------------|
| POST   | /notifications      | none                    | No   | Submit new notification           |
| GET    | /notifications      | none                    | Yes  | Get all notifications             |
|        |                     | sent=[boolean]          |      | Query: notification sent status   |
|        |                     | createdDate=[unix date] |      | Query: notification created after |
| GET    | /notifications/{id} | none                    | Yes  | Get a single notification         |
| PUT    | /notifications/{id} | none                    | Yes  | Update a notification             |
| DELETE | /notifications/{id} | none                    | Yes  | Delete a notification             |

**Auth = Yes** means that HTTP basic authentication is required to access these endpoints.

For the `POST` and `PUT` methods: The HTTP body content most contain a the Notification object in JSON.

### Notification Object

The Notification JSON, Java, and MongoDB collection object all have the same structure:

* `id` (String): The unique ID for the object - Generated automatically by this API when the notification is created
* `email` (String): Email address of the customer that requested the notification - Submitted by the [Frontend](../FrontEnd)
* `variantId` (Integer): The product variant ID that is out stock/this notification is for - Submitted by the [Frontend](../FrontEnd)
* `createdDate` (Unix time): The date this notification was created - Generated automatically by this API when the notification is created
* `sent` (boolean): Whether an email notification has been sent to the customer - Updated by the [Notification Service](../NotificationService)
* `sentDate` (Unix time): The date the notification was sent to the customer - Updated by the [Notification Service](../NotificationService)

## Prerequisites

1. Virtual private server/dedicated server with:
    * Java
    * MongoDB
    * Static IP/domain name
2. Gradle (required to compile this application, but not required to run it)
3. SSL certificate (optional, but highly recommended)
    * See **Enabling SSL** below for more information 

## Installing

1. Open server network ports for inbound connections (defaults: 8080 for http, and 8090 for https)
2. Rename application.properties.blank to application.properties and update values
3. Build this application's JAR file on a system that has Java and Gradle installed using the command `./gradlew build`

## Deployment

Copy the executable `RestApi-0.0.1-SNAPSHOT.jar` file to your server run it. If the JAR does not execute on your system, execute the application with the command `java -jar RestApi-0.0.1-SNAPSHOT.jar`. You may need to remove the `executable = true` line from `build.gradle` and recompile the application first.

For instructions on running this REST API as a service, or if you are having trouble executing the JAR file, see: https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html

## Enabling HTTPS

In order to enable HTTPS, you will need a SSL certificate.

### Method 1: Generate a self-signed certificate

Disadvantage: Users must explicitly trust your certificate in order to connect via HTTPS. This normally needs to be done manually, which is undesirable for most websites.

1. Run the keytool command `keytool -genkey -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650` and ensure:
    * You correctly enter the hostname of the server that you will be running this application on. The keytool may ask "What is your first and last name?"--the hostname should go here. 
    * Remember the password and update the `application.properties` file `server.ssl.key-store-password` with this password
    * The generated `keystore.p12` is in the same directory as this application's JAR file
2. Generate a .crt file which will need to be added to the Java CA store on the system running the [Notification Service](../NotificationService). Do this with command `openssl pkcs12 -in keystore.p12 -clcerts -nokeys -out keystore.crt`. Then add the file to your JVM's cacerts file using the commend: `sudo keytool -importcert -file keystore.crt -alias stocknotificationsrestapi -keystore $(/usr/libexec/java_home)/jre/lib/security/cacerts -storepass changeit`. Note that the alias in the Java CA store has been set to `stocknotificationsrestapi`
    * If you make a mistake in the step above, remove the certificate from the Java CA store using the command `sudo keytool -delete -alias stocknotificationsrestapi -keystore $(/usr/libexec/java_home)/jre/lib/security/cacerts -storepass changeit`

### Method 2: Obtain a certificate from a certificate authority.

Disadvantage: May take quite a bit of setup and/or cost money.

For a free CA-signed SSL Certificate visit [Let's Encrypt](https://letsencrypt.org). [This is a good guide](https://coderwall.com/p/e7gzbq/https-with-certbot-for-nginx-on-amazon-linux) on setting up a a Let's Encrypt SSL certificate with an Amazon Linux server.

Whichever method you choose, ensure the SSL settings in application.properties have been properly updated.

## Author

Chris Leung - [chrislzm](https://github.com/chrislzm)
