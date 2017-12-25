const NOTIFICATION_FORM_SKU_ID = "#stock-notification-sku";
const INPUT_SKU_ID = "#product-selector";

function onSubmit(form){
  // Copy required data into hidden form fields
  $(NOTIFICATION_FORM_SKU_ID).val($(INPUT_SKU_ID).val());
  var json = getFormDataAsJSON(form);
  console.log(json);
  if(!isValidEmail(json["email"])) alert("Please enter a valid email address");
  return false;
}

function getFormDataAsJSON(form){
  var $form = $(form)
  // Create JSON Object
  var unindexed_array = $form.serializeArray();
  var indexed_array = {};
  $.map(unindexed_array, function(n, i){
      indexed_array[n['name']] = n['value'];
  });
  return indexed_array;
}

function isValidEmail(address)
{
 if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(address)) return true;
 else return false;
}
