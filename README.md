# Back in Stock

Make it easy for your customers to sign up for and receive back in stock email alerts.

This full-stack application is composed of the following components:
1. [Frontend](FrontEnd) - Widget that allows a customer to request a back in stock notification on your website.
2. [Notification Service](NotificationService) - Emails notifications to customers when products are back in stock.
3. [Database REST API](RestApi) - CRUD API that stores all notification data.
  * FrontEnd - Creates new notifications in this database
  * Notification Service - Reads and updates this database's notifications
4. [Objects](Objects) - Java objects that are shared between components.

Please see each component's README file for more information.

Screenshots:

![Example Notification Form](doc/sample2.png "Example Notification Form")

![Example Email Notification](doc/sample.png "Example Email Notification")

## Prerequisites

1. Ecommerce website with products (for the Frontend)
2. API access to your ecommerce platform (for the Notification Service)
2. Virtual private server or better (for the Database REST API)

## Products and Variants

We define a "product" as one that has one or more "variants". For example, a red ball (product) that may come in different sizes (variants). A "product variant" (or "variant" for short) refers to a unique product, whereas a "product" refers generally to the product and all of its variants.

We understand that some stores may not be setup in this manner (e.g. in some stores, each product may be unique), so we've combined both "product" and "variant" into a single object interface "ProductVariant" to make any custom implementation easier.
