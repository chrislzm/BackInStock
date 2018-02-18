# Stock Notifications Service

This Java application implements a service that automatically emails customers when product variants they have requested notifications for are back in stock. It does this by periodically:
1. Retrieving new notifications from a database
2. Checking inventory levels for the respective product variants in an online store
3. Emailing customers for any product variants that are back in stock
4. Updating the database that the notification has been sent

# Requirements

1. Java
2. Gradle
3. Online Store (currently supports Shopify, for other online stores see Developer Notes below)
4. SMTP server
5. Notifications Database server (included -- see [RestApi](../RestApi))

# Installation Notes

1. Install Java
2. Install Gradle
3. Download this repository
4. Update files in `src/main/resources`
* Rename application.properties.blank to application.properties and update values
* Update notification_email.html as desired -- this is the template for the email your customer will receive. 

# Usage

Compile and run the application using `./gradlew bootRun`

Build the JAR file using `./gradlew build` then run the JAR file using `./build/libs/notification-service-0.1.0.jar`
* Please note that the JAR file is "executable" because we have enabled the "executable" option in build.gradle. If this does not work on your system, feel free to remove this from build.gradle and execute the application with the command `java -jar build/libs/notification-service-0.1.0.jar`

# Enable HTTPS

For security, it's highly recommended (though not required) that you enable HTTPs when connecting to the database server. To do this:
1. Ensure the REST API URL in application.properties has "https" as the protocol identifier (not http)
2. If you have a self-signed certificate it will need to be added to your Java runtime's CA store. For information on how to do this, please see the [REST API server README file](../RestApi/README.md)