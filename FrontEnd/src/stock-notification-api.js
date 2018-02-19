/**
 * @fileoverview Stock Notifications Database REST API settings and helper functions
 * @author Chris Leung
*/

const API_URL = 'http://localhost:8080/notifications'
const HEADERS = {
  'Content-Type': 'application/json'
}

function submitNotification(body) {
  return fetch(API_URL, {
    method: 'POST',
    headers: HEADERS,
    body:JSON.stringify(body)
   })
  .then(function(res) {
    return res.json();
  })
}
