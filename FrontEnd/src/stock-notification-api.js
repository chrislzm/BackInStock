const SERVER_URL = 'http://localhost:8080'
const HEADERS = {
  'Content-Type': 'application/json'
}

function submitNotification(body) {
  return fetch(SERVER_URL + "/notifications", {
    method: 'POST',
    headers: HEADERS,
    body:JSON.stringify(body)
   })
  .then(function(res) {
    return res.json();
  })
}
