// js/config.js
// Frontend / backend separation.
//
// Set APP_API_BASE_URL to the backend URL when the frontend is served
// from a different origin (different port, domain, or static host).
//
//   ''                                                      -> same origin (Spring Boot serves both)
//   'https://peershpere-production.up.railway.app/api'      -> backend on Railway
//
// Called by api.js. Load this BEFORE api.js on every page.
window.APP_API_BASE_URL = 'https://peershpere-production.up.railway.app/api';