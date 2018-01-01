const STATUS_DIV_ID = "#stock-notification-status"
const NOTIFICATION_FORM_ID ="#stock-notification-form"
const NOTIFICATION_FORM_SKU_ID = "#stock-notification-sku";
const NOTIFICATION_FORM_EMAIL_ID = "#stock-notification-email";
const NOTIFICATION_FORM_EMAIL_INPUT_ID = "#stock-notification-email-input";
const INPUT_SKU_ID = "#product-selector";
const PRODUCT_TYPE = "cell phone";

/**
 * Sets up and displays the modal
 */
function openStockNotificationForm() {
  $(NOTIFICATION_FORM_EMAIL_ID).show();
  $(NOTIFICATION_FORM_EMAIL_INPUT_ID).val("");
  $(STATUS_DIV_ID).empty();
  $(NOTIFICATION_FORM_ID).modal();
}

/**
 * Handles form submission in demo.html
 * @param  {Object} form  The form
 * @return {Boolean}      Returns false to prevent the page from reloading
 */
function onSubmit(form){
  // Copy required data into hidden form fields
  $(NOTIFICATION_FORM_SKU_ID).val($(INPUT_SKU_ID).val());
  var json = getFormDataAsJSON(form);
  if(isValidEmail(json['email'])) {
    $(STATUS_DIV_ID).text("Submitting...");
    submitNotification(json).then(function(response) {
      if(response['saved']) {
        $(NOTIFICATION_FORM_EMAIL_ID).hide();
        $(STATUS_DIV_ID).html("Your notification has been saved. <a href='#' rel='modal:close'>Close</a>");
      } else {
        $(STATUS_DIV_ID).html(`You have already registered for a notification for this ${PRODUCT_TYPE}.`);
      }
    }).catch(function(error) {
      $(STATUS_DIV_ID).html(`Sorry, a problem occurred when submitting your request. Please contact our customer support.`);
    });
  } else {
    $(STATUS_DIV_ID).text("The email address you entered is invalid");
  }
  return false;
}
