package org.mposolda.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.mposolda.StockerServer;
import org.mposolda.util.FreemarkerUtil;
import org.mposolda.util.PropertiesUtil;
import org.mposolda.util.Version;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AdminConsole {
    protected static final Logger logger = Logger.getLogger(AdminConsole.class);

//    @Context
//    protected ClientConnection clientConnection;

    @Context
    protected HttpRequest request;

    @Context
    protected HttpResponse response;

    @Context
    protected UriInfo uriInfo;

//    @Context
//    protected KeycloakSession session;

    @Context
    protected Providers providers;

//    protected AppAuthManager authManager;
//    protected RealmModel realm;

    public AdminConsole() {
//        this.realm = realm;
//        this.authManager = new AppAuthManager();
    }

//    public static class WhoAmI {
//        protected String userId;
//        protected String realm;
//        protected String displayName;
//
//        @JsonProperty("createRealm")
//        protected boolean createRealm;
//        @JsonProperty("realm_access")
//        protected Map<String, Set<String>> realmAccess = new HashMap<String, Set<String>>();
//
//        public WhoAmI() {
//        }
//
//        public WhoAmI(String userId, String realm, String displayName, boolean createRealm, Map<String, Set<String>> realmAccess) {
//            this.userId = userId;
//            this.realm = realm;
//            this.displayName = displayName;
//            this.createRealm = createRealm;
//            this.realmAccess = realmAccess;
//        }
//
//        public String getUserId() {
//            return userId;
//        }
//
//        public void setUserId(String userId) {
//            this.userId = userId;
//        }
//
//        public String getRealm() {
//            return realm;
//        }
//
//        public void setRealm(String realm) {
//            this.realm = realm;
//        }
//
//        public String getDisplayName() {
//            return displayName;
//        }
//
//        public void setDisplayName(String displayName) {
//            this.displayName = displayName;
//        }
//
//        public boolean isCreateRealm() {
//            return createRealm;
//        }
//
//        public void setCreateRealm(boolean createRealm) {
//            this.createRealm = createRealm;
//        }
//
//        public Map<String, Set<String>> getRealmAccess() {
//            return realmAccess;
//        }
//
//        public void setRealmAccess(Map<String, Set<String>> realmAccess) {
//            this.realmAccess = realmAccess;
//        }
//    }
//
//    /**
//     * Adapter configuration for the admin console for this realm
//     *
//     * @return
//     */
//    @Path("config")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @NoCache
//    public ClientManager.InstallationAdapterConfig config() {
//        ClientModel consoleApp = realm.getClientByClientId(Constants.ADMIN_CONSOLE_CLIENT_ID);
//        if (consoleApp == null) {
//            throw new NotFoundException("Could not find admin console client");
//        }
//        return new ClientManager(new RealmManager(session)).toInstallationRepresentation(realm, consoleApp, session.getContext().getUri().getBaseUri());    }
//
//    /**
//     * Permission information
//     *
//     * @param headers
//     * @return
//     */
//    @Path("whoami")
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @NoCache
//    public Response whoAmI(final @Context HttpHeaders headers) {
//        RealmManager realmManager = new RealmManager(session);
//        AuthenticationManager.AuthResult authResult = authManager.authenticateBearerToken(session, realm, session.getContext().getUri(), clientConnection, headers);
//        if (authResult == null) {
//            return Response.status(401).build();
//        }
//        UserModel user= authResult.getUser();
//        String displayName;
//        if ((user.getFirstName() != null && !user.getFirstName().trim().equals("")) || (user.getLastName() != null && !user.getLastName().trim().equals(""))) {
//            displayName = user.getFirstName();
//            if (user.getLastName() != null) {
//                displayName = displayName != null ? displayName + " " + user.getLastName() : user.getLastName();
//            }
//        } else {
//            displayName = user.getUsername();
//        }
//
//        RealmModel masterRealm = getAdminstrationRealm(realmManager);
//        Map<String, Set<String>> realmAccess = new HashMap<String, Set<String>>();
//        if (masterRealm == null)
//            throw new NotFoundException("No realm found");
//        boolean createRealm = false;
//        if (realm.equals(masterRealm)) {
//            logger.debug("setting up realm access for a master realm user");
//            createRealm = user.hasRole(masterRealm.getRole(AdminRoles.CREATE_REALM));
//            addMasterRealmAccess(realm, user, realmAccess);
//        } else {
//            logger.debug("setting up realm access for a realm user");
//            addRealmAccess(realm, user, realmAccess);
//        }
//
//        return Response.ok(new WhoAmI(user.getId(), realm.getName(), displayName, createRealm, realmAccess)).build();
//    }
//
//    private void addRealmAccess(RealmModel realm, UserModel user, Map<String, Set<String>> realmAdminAccess) {
//        RealmManager realmManager = new RealmManager(session);
//        ClientModel realmAdminApp = realm.getClientByClientId(realmManager.getRealmAdminClientId(realm));
//        Set<RoleModel> roles = realmAdminApp.getRoles();
//        for (RoleModel role : roles) {
//            if (!user.hasRole(role)) continue;
//            if (!realmAdminAccess.containsKey(realm.getName())) {
//                realmAdminAccess.put(realm.getName(), new HashSet<String>());
//            }
//            realmAdminAccess.get(realm.getName()).add(role.getName());
//        }
//
//    }
//
//    private void addMasterRealmAccess(RealmModel masterRealm, UserModel user, Map<String, Set<String>> realmAdminAccess) {
//        List<RealmModel> realms = session.realms().getRealms();
//        for (RealmModel realm : realms) {
//            ClientModel realmAdminApp = realm.getMasterAdminClient();
//            Set<RoleModel> roles = realmAdminApp.getRoles();
//            for (RoleModel role : roles) {
//                if (!user.hasRole(role)) continue;
//                if (!realmAdminAccess.containsKey(realm.getName())) {
//                    realmAdminAccess.put(realm.getName(), new HashSet<String>());
//                }
//                realmAdminAccess.get(realm.getName()).add(role.getName());
//            }
//        }
//    }

//    /**
//     * Logout from the admin console
//     *
//     * @return
//     */
//    @Path("logout")
//    @GET
//    @NoCache
//    public Response logout() {
//        URI redirect = AdminRoot.adminConsoleUrl(session.getContext().getUri(UrlType.ADMIN)).build(realm.getName());
//
//        return Response.status(302).location(
//                OIDCLoginProtocolService.logoutUrl(session.getContext().getUri(UrlType.ADMIN)).queryParam("redirect_uri", redirect.toString()).build(realm.getName())
//        ).build();
//    }
//
//    protected RealmModel getAdminstrationRealm(RealmManager realmManager) {
//        return realmManager.getKeycloakAdminstrationRealm();
//    }

    /**
     * Main page of this realm's admin console
     *
     * @return
     * @throws URISyntaxException
     */
    @GET
    @NoCache
    public Response getMainPage() throws IOException {
        if (!uriInfo.getRequestUri().getPath().endsWith("/")) {
            return Response.status(302).location(uriInfo.getRequestUriBuilder().path("/").build()).build();
        } else {
            //Theme theme = AdminRoot.getTheme(session, realm);

            Map<String, Object> map = new HashMap<>();

            URI adminBaseUri = uriInfo.getBaseUri();
            String adminBaseUrl = adminBaseUri.toString();
            if (adminBaseUrl.endsWith("/")) {
                adminBaseUrl = adminBaseUrl.substring(0, adminBaseUrl.length() - 1);
            }

            URI authServerBaseUri = uriInfo.getBaseUri();
            String authServerBaseUrl = authServerBaseUri.toString();
            if (authServerBaseUrl.endsWith("/")) {
                authServerBaseUrl = authServerBaseUrl.substring(0, authServerBaseUrl.length() - 1);
            }

            map.put("authServerUrl", authServerBaseUrl);
            map.put("authUrl", adminBaseUrl);
            map.put("consoleBaseUrl", uriInfo.getRequestUri().getPath());

            String resourcesUrl = StockerServer.CONTEXT_ROOT + "/resources/" + Version.RESOURCES_VERSION+ "/admin/base";
            map.put("resourceUrl", resourcesUrl);
            map.put("resourceCommonUrl", resourcesUrl);
            //map.put("resourceCommonUrl", Urls.themeRoot(adminBaseUri).getPath() + "/common/keycloak");
            map.put("masterRealm", "master");
            map.put("resourceVersion", Version.RESOURCES_VERSION);
            map.put("properties", PropertiesUtil.loadProperties("theme/base/admin/resources/theme.properties"));

            FreemarkerUtil freeMarkerUtil = new FreemarkerUtil();
            String result = freeMarkerUtil.processTemplate(map, "index.ftl");
            Response.ResponseBuilder builder = Response.status(Response.Status.OK).type(MediaType.TEXT_HTML_UTF_8).language(Locale.ENGLISH).entity(result);

            // Replace CSP if admin is hosted on different URL
//            if (!adminBaseUri.equals(authServerBaseUri)) {
//                session.getProvider(SecurityHeadersProvider.class).options().allowFrameSrc(UriUtils.getOrigin(authServerBaseUri));
//            }

            return builder.build();
        }
    }

    @GET
    @Path("{indexhtml: index.html}") // this expression is a hack to get around jaxdoclet generation bug.  Doesn't like index.html
    public Response getIndexHtmlRedirect() {
        return Response.status(302).location(uriInfo.getRequestUriBuilder().path("../").build()).build();
    }

    @GET
    @Path("messages.json")
    @Produces(MediaType.APPLICATION_JSON)
    public Properties getMessages(@QueryParam("lang") String lang) {
        return AdminRoot.getMessages(lang, "admin-messages");
    }

}