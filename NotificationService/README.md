# Description

This Java application implements a service that automatically emails customers when items they have requested notifications for are back in stock. It does this by periodically (1) retrieving new notifications from a database, (2) checking inventory levels of an online store, (3) emailing customers for items that are back in stock, and (4) updating the database that the notification has been sent.

# Requirements

Currently the application immediately supports Shopify. It also requires a database server with web access, along with an SMTP sever to send emails.

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