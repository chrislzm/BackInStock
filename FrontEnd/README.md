# Back In Stock - Frontend

This is an example of a widget that allows customers to register for back in stock notifications. It can be modified and integrated with your existing website.

Example ([src/demo.html](src/demo.html)):

![Demo Notification Form](doc/sample.png "Demo Notification Form")

## Prerequisites

* [Stock Notifications Database REST API](../RestApi) must be running
* To send email notifications to customers, the [Notification Service](../NotificationService) needs to be running. (However it's not required in order for customers to sign up for notifications.)

## Installing

1. Download this repository
2. Download the [jQuery](https://jquery.com/) and [jQuery Modal](https://jquerymodal.com) dependencies. Either:
    * Install [npm](https://www.npmjs.com/) and run `npm install` in the `src` directory to automatically download the dependencies, or
    * Download the packages manually from their websites and update `demo.html` with the path to the assets
3. Update `API_URL` in `stock-notification-api.js` with the full url of the Stock Notifications Database REST API endpoint
4. Update `#product-selector` option values in `src/demo.html` with actual IDs of product variants that are in stock in your store. (This way, when you run the [Notification Service](../NotificationService), it will detect notifications for the back-in-stock products and dispatch email notifications. This will allow you to verify that everything has been configured properly.)

Now open `src/demo.html` in your browser and give it a run!

## Deployment

1. Upload the jQuery and jQuery Modal dependencies to your website and update the code on your product page to include the dependencies
3. Copy the entire `#stock-notification-form` code from `demo.html` into your product Page and update:
    1. `#stock-notification-image-container`'s `<img src>` attribute with the URL to the product/variant image
    2. `#stock-notification-product-title` text with the product title
4. In `stock-notification-frontend.js`, update `INPUT_VARIANT_SELECTOR` with the ID of the select element that contains the title text of the variant and the variant ID. If your product page isn't set up this way, you will need to modify this logic.
5. Implement logic on your product page to show a "Notify Me When Available" button (that opens the modal, e.g. using an `onclick="openStockNotificationForm()"` attribute) whenever a product variant is out of stock.

## Author

Chris Leung - [chrislzm](https://github.com/chrislzm)
