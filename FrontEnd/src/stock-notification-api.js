/**
 * @fileoverview Stock Notifications Database REST API settings and helper functions
 * @author Chris Leung
*/

const API_URL = 'http://localhost:8080/notifications'
const HEADERS = {
  'Content-Type': 'application/json'
}

/**
 * Submits a notification to the Database REST API
 * @param  {Object} notification Object with "email" and "variantId" keys and values
 * @return {Promise}             Returns a promise with the response from the API
 */
function submitNotification(notification) {
  return fetch(API_URL, {
    method: 'POST',
    headers: HEADERS,
    body:JSON.stringify(notification)
   })
  .then(function(res) {
    return res.json();
  })
}
