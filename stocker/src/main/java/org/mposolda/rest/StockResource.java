package org.mposolda.rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class StockResource {
//
//    @Context
//    protected ClientConnection connection;

    @Context
    protected HttpHeaders headers;

    public StockResource() {
//        this.auth = auth;
//        this.realm = realm;
//        this.tokenManager = tokenManager;
//        this.adminEvent = adminEvent.realm(realm).resource(ResourceType.REALM);
    }
}
