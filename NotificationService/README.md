# Installation Notes

Be sure to update /src/main/resources/application.properties with the username, password, and URL of the REST API.

# Enabling HTTPS

To enable HTTPS please ensure you:
1. The REST API URL in application.properties has "https" as the protocol identifier (not http)
2. The certificate file has been added to your Java runtime's CA store. For information on how to do this, please see the [REST API server README file](../RestApi/README.md)