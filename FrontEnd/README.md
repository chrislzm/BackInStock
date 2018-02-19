# Frontend - Stock Notification Form

This is an example stock notification widget that allows customers to register for stock notifications. It can be modified and integrated with your existing online storefront.

Example form ([src/demo.html](src/demo.html)):

![Demo Notification Form](doc/sample.png "Demo Notification Form")

## Prerequisites

Requires the [Stock Notifications Database REST API](../RestApi) to be running.

## Installing

1. Download this repository
2. Download the [jQuery](https://jquery.com/) and [jQuery Modal](https://jquerymodal.com) dependencies. Either:
  * Install [npm](https://www.npmjs.com/) and run `npm install` in the `src` directory to automatically download the dependencies, or
  * Download the packages manually from their websites and update `demo.html` with the path to the assets
3. Update `API_URL` in `stock-notification-api.js` with the full url of the Stock Notifications Database REST API endpoint
4. Optionally update `#product-selector` option values in `demo.html` with actual product variant IDs of product variants that are in stock. (This way, when you run the [Notification Service](../NotificationService), it will detect notifications for the back-in-stock products and will dispatch email notifications. This will also allow you to verify everything has been configured properly.)

Now just open `demo.html` in your browser and give it a run!

## Deployment

1. Upload the jQuery and jQuery Modal dependencies to your website and update the code on your product page to include the dependencies
3. Copy the entire `#stock-notification-form` code from `demo.html` into your product Page and update:
  * `#stock-notification-image-container`'s `<img src>` attribute with the URL to the product/variant image
  * `#stock-notification-product-title` text with the product title
4. In `stock-notification-frontend.js`, update `INPUT_VARIANT_SELECTOR` with the ID of the select element that contains the title text of the variant and the variant ID. If your product page isn't set up this way, you will need to modify this logic.
5. Implement logic on your product page to show a "Notify Me When Available" button (that opens the modal, e.g. using an `onclick="openStockNotificationForm()"` event attribute) whenever a product variant is out of stock.

## Author

Chris Leung - [chrislzm](https://github.com/chrislzm)
