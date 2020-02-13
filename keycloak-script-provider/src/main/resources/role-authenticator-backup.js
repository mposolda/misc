// REPRODUCER FOR https://issues.redhat.com/browse/KEYCLOAK-12221
//
// import enum for error lookup
AuthenticationFlowError = Java.type("org.keycloak.authentication.AuthenticationFlowError");

/**
 * An example authenticate function.
 *
 * The following variables are available for convenience:
 * user - current user {@see org.keycloak.models.UserModel}
 * realm - current realm {@see org.keycloak.models.RealmModel}
 * session - current KeycloakSession {@see org.keycloak.models.KeycloakSession}
 * httpRequest - current HttpRequest {@see org.jboss.resteasy.spi.HttpRequest}
 * script - current script {@see org.keycloak.models.ScriptModel}
 * authenticationSession - current authentication session {@see org.keycloak.sessions.AuthenticationSessionModel}
 * LOG - current logger {@see org.jboss.logging.Logger}
 *
 * You one can extract current http request headers via:
 * httpRequest.getHttpHeaders().getHeaderString("Forwarded")
 *
 * @param context {@see org.keycloak.authentication.AuthenticationFlowContext}
 */
function authenticate(context) {

    var rolename = 'alteria_admins';
    var username = user ? user.username : "anonymous";
    //var realmRole1 = session.getContext().getRealm().getRole("alteria_admins");

    var rrole = realm.getRole(rolename);
    LOG.info(script.name + " trace auth for: " + username + ", Role found?:" + rrole);

    if (rrole === null) {
        LOG.info(script.name + "ERROR: No '" + rolename + "' for realm '" + realm.getName() + "' found.");
        return context.failure(AuthenticationFlowError.INVALID_USER);
    }

    if (user.hasRole(rrole)){
        return context.success();
    }

    return context.failure(AuthenticationFlowError.INVALID_USER);
    //return;
    //return denyAccess(context, rrole);
}

function denyAccess(context, rrole) {
    var formBuilder = context.form();
    var client = session.getContext().getClient();
    var description = !rrole.getAttribute('deniedMessage').isEmpty() ? rrole.getAttribute('deniedMessage') : ['Access Denied'];
    var form = formBuilder
        .setAttribute('clientUrl', client.getRootUrl())
        .setAttribute('clientName', client.getName())
        .setAttribute('description', description[0])
        .createForm('denied-auth.ftl');
    return context.failure(AuthenticationFlowError.INVALID_USER, form);
 }
