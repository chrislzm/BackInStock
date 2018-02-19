/**
 * @fileoverview Stock Notifications Frontend - Helper Functions
 * @author Chris Leung
*/

/**
 * Converts form data to a JSON object
 * @param  {Object} form HTML DOM Form Object
 * @return {Object}      JSON object
 */
function getFormDataAsJSON(form){
  var $form = $(form)
  var unindexed_array = $form.serializeArray();
  var indexed_array = {};
  $.map(unindexed_array, function(n, i){
      indexed_array[n['name']] = n['value'];
  });
  return indexed_array;
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
