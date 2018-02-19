By Chris Leung

# Introduction

This project is currently under development and unstable.

Stock Notifications is built of three applications:
1. [Front End](FrontEnd) - Integrated into your product page, this allows the user to submit a notification request to the REST API server.
2. [REST API Server](RestApi) - Primary data store and source of truth for the state of each notification.
3. [Notification Service](NotificationService) - Retrieves unsent notifications from the REST API server, checks inventory via the Shopify API, emails notifications to customers when products are back in stock, and updates the REST API server accordingly marking notifications as sent

Please see each individual application's README file for more information.

# Products and Variants

We define a "product" as one that has one or more "variants". For example, a red ball (product) that may come in different sizes (variants). A "product variant" (or "variant" for short) refers to a unique product, whereas a "product" refers generally to the product and all of its variants.

We understand that some stores may not be setup in this manner (e.g. in some stores, each product may be unique), so we've combined both "product" and "variant" into a single object interface "ProductVariant" to make any custom implementation easier.
