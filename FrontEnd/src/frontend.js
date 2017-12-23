function onSubmit( form ){
  var data = getFormData($("#stock-notification"));
  console.log(data);
  return false;
}

function getFormData($form){
  var unindexed_array = $form.serializeArray();
  var indexed_array = {};
  $.map(unindexed_array, function(n, i){
      indexed_array[n['name']] = n['value'];
  });
  return indexed_array;
}
