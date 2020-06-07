package org.mposolda.rest;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

/**
 * Theme resource
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@Path("/resources")
public class ThemeResource {

    protected static final Logger logger = Logger.getLogger(ThemeResource.class);

    /**
     * Get theme content
     *
     * @param themeType
     * @param themeName
     * @param path
     * @return
     */
    @GET
    @Path("/{version}/{themeType}/{themeName}/{path:.*}")
    public Response getResource(@PathParam("version") String version, @PathParam("themeType") String themeType, @PathParam("themeName") String themeName, @PathParam("path") String path) {
        try {
            // TODO: Improve if needed
            String loadingPath = "theme/" + themeName + "/" + themeType + "/resources/" + path;
            logger.infof("Loading resources from %s", loadingPath);

            InputStream resource = getClass().getClassLoader().getResourceAsStream(loadingPath);
//            Theme theme = session.theme().getTheme(themeName, Theme.Type.valueOf(themType.toUpperCase()));
//            InputStream resource = theme.getResourceAsStream(path);

            if (resource != null) {
                return Response.ok(resource).type(MimeTypeUtil.getContentType(path)).cacheControl(CacheControlUtil.getDefaultCacheControl()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error("Problem when serving theme request", e);
            return Response.serverError().build();
        }
    }

}
