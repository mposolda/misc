package org.mposolda.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.mposolda.util.PropertiesUtil;

/**
 * Root resource for admin console and admin REST API
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/")
public class AdminRoot {
    protected static final Logger logger = Logger.getLogger(AdminRoot.class);

    public static final String THEME_RESOURCES_MESSAGES = "theme/base/admin/messages/";

//    @Context
//    protected ClientConnection clientConnection;

    @Context
    protected HttpRequest request;

    @Context
    protected HttpResponse response;

    @Context
    protected UriInfo uriInfo;

//    protected AppAuthManager authManager;
//    protected TokenManager tokenManager;
//
//    @Context
//    protected KeycloakSession session;

    public AdminRoot() {
//        this.tokenManager = new TokenManager();
//        this.authManager = new AppAuthManager();
    }

    public static UriBuilder adminBaseUrl(UriInfo uriInfo) {
        return adminBaseUrl(uriInfo.getBaseUriBuilder());
    }

    public static UriBuilder adminBaseUrl(UriBuilder base) {
        return base.path(AdminRoot.class);
    }

    /**
     * Convenience path to master realm admin console
     *
     * @exclude
     * @return
     */
    @GET
    public Response masterRealmAdminConsoleRedirect() {
        URI location = uriInfo.getBaseUriBuilder().path(AdminRoot.class).path(AdminRoot.class, "getAdminConsole").path("/").build();
        return Response.status(302).location(location).build();
    }

    /**
     * Convenience path to master realm admin console
     *
     * @exclude
     * @return
     */
    @Path("index.{html:html}") // expression is actually "index.html" but this is a hack to get around jax-doclet bug
    @GET
    public Response masterRealmAdminConsoleRedirectHtml() {
        return masterRealmAdminConsoleRedirect();
    }

//    protected RealmModel locateRealm(String name, RealmManager realmManager) {
//        RealmModel realm = realmManager.getRealmByName(name);
//        if (realm == null) {
//            throw new NotFoundException("Realm not found.  Did you type in a bad URL?");
//        }
//        session.getContext().setRealm(realm);
//        return realm;
//    }


    public static UriBuilder adminConsoleUrl(UriInfo uriInfo) {
        return adminConsoleUrl(uriInfo.getBaseUriBuilder());
    }

    public static UriBuilder adminConsoleUrl(UriBuilder base) {
        return adminBaseUrl(base).path(AdminRoot.class, "getAdminConsole");
    }

    /**
     * path to realm admin console ui
     *
     * @exclude
     * @return
     */
    @Path("/console")
    public AdminConsole getAdminConsole() {
//        RealmManager realmManager = new RealmManager(session);
//        RealmModel realm = locateRealm(name, realmManager);
        AdminConsole service = new AdminConsole();
        ResteasyProviderFactory.getInstance().injectProperties(service);
        return service;
    }


//    protected AdminAuth authenticateRealmAdminRequest(HttpHeaders headers) {
//        String tokenString = authManager.extractAuthorizationHeaderToken(headers);
//        if (tokenString == null) throw new NotAuthorizedException("Bearer");
//        AccessToken token;
//        try {
//            JWSInput input = new JWSInput(tokenString);
//            token = input.readJsonContent(AccessToken.class);
//        } catch (JWSInputException e) {
//            throw new NotAuthorizedException("Bearer token format error");
//        }
//        String realmName = token.getIssuer().substring(token.getIssuer().lastIndexOf('/') + 1);
//        RealmManager realmManager = new RealmManager(session);
//        RealmModel realm = realmManager.getRealmByName(realmName);
//        if (realm == null) {
//            throw new NotAuthorizedException("Unknown realm in token");
//        }
//        session.getContext().setRealm(realm);
//        AuthenticationManager.AuthResult authResult = authManager.authenticateBearerToken(session, realm, session.getContext().getUri(), clientConnection, headers);
//        if (authResult == null) {
//            logger.debug("Token not valid");
//            throw new NotAuthorizedException("Bearer");
//        }
//
//        ClientModel client = realm.getClientByClientId(token.getIssuedFor());
//        if (client == null) {
//            throw new NotFoundException("Could not find client for authorization");
//
//        }
//
//        return new AdminAuth(realm, authResult.getToken(), authResult.getUser(), client);
//    }

    public static UriBuilder stocksUrl(UriInfo uriInfo) {
        return stocksUrl(uriInfo.getBaseUriBuilder());
    }

    public static UriBuilder stocksUrl(UriBuilder base) {
        return adminBaseUrl(base).path(AdminRoot.class, "getStocksAdmin");
    }

    /**
     * Base Path to stock admin REST interface
     *
     * @param headers
     * @return
     */
    @Path("stock")
    public Object getStocksAdmin(@Context final HttpHeaders headers) {
//        if (request.getHttpMethod().equals(HttpMethod.OPTIONS)) {
//            return new AdminCorsPreflightService(request);
//        }

//        AdminAuth auth = authenticateRealmAdminRequest(headers);
//        if (auth != null) {
//            logger.debug("authenticated admin access for: " + auth.getUser().getUsername());
//        }

//        Cors.add(request).allowedOrigins(auth.getToken()).allowedMethods("GET", "PUT", "POST", "DELETE").exposedHeaders("Location").auth().build(response);

        StockResource adminResource = new StockResource();
        ResteasyProviderFactory.getInstance().injectProperties(adminResource);
        return adminResource;
    }

//    /**
//     * General information about the server
//     *
//     * @param headers
//     * @return
//     */
//    @Path("serverinfo")
//    public Object getServerInfo(@Context final HttpHeaders headers) {
//        if (request.getHttpMethod().equals(HttpMethod.OPTIONS)) {
//            return new AdminCorsPreflightService(request);
//        }
//
//        AdminAuth auth = authenticateRealmAdminRequest(headers);
//        if (!AdminPermissions.realms(session, auth).isAdmin()) {
//            throw new ForbiddenException();
//        }
//
//        if (auth != null) {
//            logger.debug("authenticated admin access for: " + auth.getUser().getUsername());
//        }
//
//        Cors.add(request).allowedOrigins(auth.getToken()).allowedMethods("GET", "PUT", "POST", "DELETE").auth().build(response);
//
//        ServerInfoAdminResource adminResource = new ServerInfoAdminResource();
//        ResteasyProviderFactory.getInstance().injectProperties(adminResource);
//        return adminResource;
//    }

//    public static Theme getTheme(KeycloakSession session, RealmModel realm) throws IOException {
//        return session.theme().getTheme(Theme.Type.ADMIN);
//    }

//    public static Properties getMessages(String lang) {
//        try {
//            //Theme theme = getTheme(session, realm);
//            Locale locale = lang != null ? Locale.forLanguageTag(lang) : Locale.ENGLISH;
//            return theme.getMessages(locale);
//        } catch (IOException e) {
//            logger.error("Failed to load messages from theme", e);
//            return new Properties();
//        }
//    }

    public static Properties getMessages(String lang, String... bundles) {
        Properties compound = new Properties();
        for (String bundle : bundles) {
            Properties current = getMessages(lang, bundle);
            compound.putAll(current);
        }
        return compound;
    }

    private static Properties getMessages(String lang, String bundle) {
        String resourcePath = THEME_RESOURCES_MESSAGES + bundle + "_" + lang.toString() + ".properties";
        return PropertiesUtil.loadProperties(resourcePath);
    }
}
