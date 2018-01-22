# Introduction

This project is currently under development and unstable.

Stock Notifications is built of three applications:
1. [Front End](FrontEnd) - Integrated into your product page, this allows the user to submit a notification request to the REST API server.
2. [REST API Server](RestApi) - Primary data store and source of truth for the state of each notification.
3. [Notification Service](NotificationService) - Retrieves unsent notifications from the REST API server, checks inventory via the Shopify API, emails notifications to customers when products are back in stock, and updates the REST API server accordingly marking notifications as sent

Please see each individual application's README file for more information.
