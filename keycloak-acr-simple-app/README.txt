- Assumption Wildfly with this app on "http://localhost:8180/acr-app" and Keycloak on "http://localhost:8081/auth" . Wildfly was started with:
```
./standalone.sh -Djboss.socket.binding.port-offset=100
```
Keycloak with the server on embedded undertow.

- Keycloak must have client "acr-app" in the realm "test". Browser flow must be configured with ACR support

App running on "http://localhost:8180/acr-app"