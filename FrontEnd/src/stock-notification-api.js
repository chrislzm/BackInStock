const SERVER_URL = 'http://service.artofsilk.com:8080'
const HEADERS = {
  'Content-Type': 'application/json'
}

const submitNotification = (body) => (
  fetch(`${SERVER_URL}/notifications`, {
    method: 'POST',
    headers: HEADERS,
    body:JSON.stringify(body)
   })
  .then(res => res.json())
)
