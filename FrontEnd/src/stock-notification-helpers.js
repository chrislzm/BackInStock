/**
 * @fileoverview Stock Notifications Frontend - Helper Functions
 * @author Chris Leung
*/

/**
 * Converts form data to an object where the field name is mapped to its value
 * @param  {Object} form HTML DOM Form Object
 * @return {Object}      Object with form input name:value mappings
 */
function getFormDataAsObject(form){
  var $form = $(form)
  var serializedArray = $form.serializeArray();
  var output = {};
  $.map(serializedArray, function(n, i){
      output[n['name']] = n['value'];
  });
  return output;
}

/**
 * Checks if string contains a valid email address format
 * @param  {String}  address email
 * @return {Boolean}
 */
function isValidEmail(address)
{
 if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(address)) return true;
 else return false;
}
