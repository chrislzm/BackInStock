const NOTIFICATION_FORM_ID = "stock-notification";
const NOTIFICATION_FORM_SKU_ID = "sku";
const INPUT_SKU_ID = "product-selector";

function onSubmit( form ){
  var data = getFormData($(`#${NOTIFICATION_FORM_ID}`));
  console.log(data);
  return false;
}

function getFormData($form){

  // Copy required data into hidden form fields
  copyInputValue(INPUT_SKU_ID,NOTIFICATION_FORM_SKU_ID);

  // Create JSON Object
  var unindexed_array = $form.serializeArray();
  var indexed_array = {};
  $.map(unindexed_array, function(n, i){
      indexed_array[n['name']] = n['value'];
  });
  if(isValidEmail(indexed_array["email"])) return indexed_array;
  else alert("Please enter a valid email address.");
}

function copyInputValue(id1,id2){
  document.getElementById(id2).value = document.getElementById(id1).value;
}

function isValidEmail(address)
{
 if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(address)) return true;
 else return false;
}
