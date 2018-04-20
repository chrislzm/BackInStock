# Back in Stock

Make it easy for customers to sign up for and receive back in stock email alerts! Built-in support for Shopify, but can be modified to work with other e-commerce platforms.

![Example Notification Form](FrontEnd/doc/sample2.jpg "Example Notification Form")

![Example Email Notification](NotificationService/doc/sample.jpg "Example Email Notification")

This application is composed of three components:

1. [Frontend](FrontEnd) - Allows a customer to request a back in stock notification on your website
2. [Notification Service](NotificationService) - Monitors inventory and notifications, emails customers when products are back in stock
3. [Database REST API](RestApi) - Interface to the database, which stores all notification data
    * **Frontend** creates new notifications in this database
    * **Notification Service** reads from and updates this database

## Prerequisites

1. Ecommerce website with API access
2. SMTP email account
3. Virtual private server or better

## Installing & Deploying

Please refer to each component's README file for instructions:
* [Frontend `README.md`](FrontEnd/README.md)
* [Notification Service `README.md`](NotificationService/README.md)
* [Database REST API `README.md`](RestApi/README.md)

## Built with

* [Spring](https://spring.io/) - Java framework used for the backend
* [Simple Java Mail](http://www.simplejavamail.org/)
* [Simple Logging Facade for Java](https://www.slf4j.org/)
* [Gradle](https://gradle.org/) - Java dependency management
* [MongoDB](https://www.mongodb.com/) - NoSQL Database
* [npm](https://www.npmjs.com/) - JavaScript dependency management
* [jQuery](https://jquery.com/) - Frontend JavaScript library
* [jQuery Modal](jquerymodal.com) - Frontend modal form

## "Product" and "Variant" Definitions

These two terms are used throughout this project's code and documentation.
* **Product** refers generally to a product and all of its variants. A product can have one or more **variants**, e.g. a ball (product) can be sold in different colors and sizes (variants).
* **Variants** may also be referred to as **product variants**, with exception of the [ProductVariant interface](Objects/src/main/java/com/chrisleung/notifications/objects/ProductVariant.java), where together the two words refer to the combination of a variant's data with its respective product data into a single object.

## License

Copyright (c) 2018 [Chris Leung](https://github.com/chrislzm)

Licensed under the MIT License. You may obtain a copy of the License in the [`LICENSE`](LICENSE) file included with this project.
