# Back In Stock Notification Service

This Java application implements an automated notification service that emails customers when product variants they have requested notifications for are back in stock. It does this by periodically:
1. Retrieving new notifications from a database
2. Checking inventory levels for the respective product variants in an online store
3. Emailing customers for any product variants that are back in stock
4. Updating the database that the notification has been sent

Example notification email (email template is included and fully customizable):

![Example Email Notification](doc/sample.png "Sample Email Notification")

## Prerequisites

1. Java
2. Gradle
3. API Access to your ecommerce website
4. SMTP email account
5. Notifications Database server (included -- see the [Database Server REST API](../RestApi))

## Installing

1. Download this repository
2. If you are not using Shopify, you will need to implement interfaces for your ecommerce platform:
  * Implement the [`StoreApi`](../Objects/src/main/java/com/chrisleung/notifications/objects/StoreApi.java) and [`ProductVariant`](../Objects/src/main/java/com/chrisleung/notifications/objects/ProductVariant.java) interfaces. Refer to `ShopifyApi.java` and `ShopifyProductVariant.java` respectively as example implementations.
  * Update `Application.java` lines 42 and 61, replacing the default `ShopifyApi` with your own `StoreApi` implementation.
5. Rename `application.properties.blank` to `application.properties` and update values (see Deployment below for notes on Shopify settings)
6. Update `notification_email.html` template as desired -- this is the template for the email your customer will receive. 
7. Compile and run the application. Either:
  * Run the command `./gradlew bootRun`, or
  * Build an executable JAR file using `./gradlew build`, which will create a JAR file in `./build/libs/notification-service-0.1.0.jar`
    * If the JAR file does not execute on your system, execute the application with the command `java -jar build/libs/notification-service-0.1.0.jar`. You may need to remove the `executable = true` line from `build.gradle` and recompile the application first.

## Deployment

To use with Shopify:
1. Ensure each Shopify product variant has been assigned a corresponding product image. This can be done in either:
* Your Shopify admin panel under Products
* Exporting all products, making a backup copy, updating the variant image url in the CSV file, and uploading the modified file. (Be sure to enable the option to overwrite products with the same handle. If an error occurs, upload the backup copy to revert changes.)
2. Give this application API access by going to the "Apps" section in your Shopify store's admin panel. Click on "Manage Private Apps", then "Create a new private app". Follow the instructions to generate the key and password.
3. Update the Shopify configuration in the `application.properties` file. For the product and variant url settings, it should be `https://yourstorename.myshopify.com/admin/products/` and `https://yourstorename.myshopify.com/admin/variants/` respectively.

This application can be run as a service, or launched on-demand. 

## Enabling HTTPS

For security, it's highly recommended (though not required) that you enable HTTPs when connecting to the database server. To do this:
1. Ensure the REST API URL in `application.properties` has `https` as the protocol identifier (not `http`)
2. If you have a self-signed certificate it will need to be added to your Java runtime's CA store. For information on how to do this, please see the [REST API server README file](../RestApi/README.md)

## Author

Chris Leung - [chrislzm](https://github.com/chrislzm)
