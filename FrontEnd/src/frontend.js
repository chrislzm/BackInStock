const STATUS_DIV_ID = "#stock-notification-status"
const NOTIFICATION_FORM_ID ="#stock-notification-form"
const NOTIFICATION_FORM_VARIANT_ID = "#stock-notification-variant-id";
const NOTIFICATION_FORM_EMAIL_ID = "#stock-notification-email";
const NOTIFICATION_FORM_EMAIL_INPUT_ID = "#stock-notification-email-input";
const NOTIFICATION_FORM_VARIANT_TITLE = "#stock-notification-product-variant";
const INPUT_VARIANT_ID = "#product-selector";
const PRODUCT_TYPE = "cell phone";

/**
 * Sets up and displays the modal
 */
function openStockNotificationForm() {
  // Reset if we previously submitted
  $(NOTIFICATION_FORM_EMAIL_ID).show();
  $(NOTIFICATION_FORM_EMAIL_INPUT_ID).val("");
  $(STATUS_DIV_ID).empty();
  // Copy selected variant title
  $(NOTIFICATION_FORM_VARIANT_TITLE).text($(INPUT_VARIANT_ID+" option:selected").text());
  $(NOTIFICATION_FORM_ID).modal();
}

/**
 * Handles form submission in demo.html
 * @param  {Object} form  The form object
 * @return {Boolean}      Returns false to prevent the page from reloading
 */
function onSubmit(form){
  // Copy selected variant ID from product page into hidden form field
  $(NOTIFICATION_FORM_VARIANT_ID).val($(INPUT_VARIANT_ID).val());
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
