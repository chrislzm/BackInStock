# Stock Notification Frontend

This frontend application is an example stock notification widget that allows customers to register for stock notifications. It can be modified and integrated with your existing online storefront.

The example registration form in our [demo.html](src/demo.html) file.
![Demo Notification Form](doc/sample.png "Demo Notification Form")

# Usage Notes

1. Please ensure you update the API_URL constant in stock-notification-api.js with the full url of the Stock Notifications REST API endpoint.
2. Install the npm tool and run the "npm install" command to download dependencies
3. In demo.html, optionally update #product-selector option values with actual product/variant IDs of your products that are in stock, so that when you run the Notification Service, it will detect the back-in-stock products, send email notifications, and confirm that everything is configured properly.
4. Open demo.html in a web browser and give it a run!
