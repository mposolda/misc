package org.mposolda.rest;

import javax.ws.rs.core.CacheControl;

import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class CacheControlUtil {

    public static void noBackButtonCacheControlHeader() {
        //HttpResponse response = Resteasy.getContextData(HttpResponse.class);
        HttpResponse response = ResteasyProviderFactory.getInstance().getContextData(HttpResponse.class);
        response.getOutputHeaders().putSingle("Cache-Control", "no-store, must-revalidate, max-age=0");
    }

    public static CacheControl getDefaultCacheControl() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoTransform(false);
        //Integer maxAge = Config.scope("theme").getInt("staticMaxAge", 2592000);
        Integer maxAge = 2592000;
        if (maxAge != null && maxAge > 0) {
            cacheControl.setMaxAge(maxAge);
        } else {
            cacheControl.setNoCache(true);
        }
        return cacheControl;
    }

    public static CacheControl noCache() {

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMustRevalidate(true);
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);

        return cacheControl;
    }


}
