package org.mposolda.drools.uripolicytest.template;

import org.drools.template.DataProvider;

import java.util.Iterator;

/**
 * Drools DataProvider for providing data about URI policies configured by user. Data are used to compile Drools template into
 * real rules for Drools engine
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class URIPolicyTemplateDataProvider implements DataProvider {

    private final Iterator<URIPolicy> uriTemplateIterator;

    public URIPolicyTemplateDataProvider(Iterator<URIPolicy> uriTemplateIterator) {
        this.uriTemplateIterator = uriTemplateIterator;
    }

    @Override
    public boolean hasNext() {
        return uriTemplateIterator.hasNext();
    }

    @Override
    public String[] next() {
        URIPolicy next = uriTemplateIterator.next();
        return new String[] {
                String.valueOf(next.getPriority()),
                String.valueOf(next.getPriority() - 1),
                next.getUriPattern(),
                next.getQueryParamsCondition(),
                next.getAllowedRealmRoles(),
                next.getDeniedRealmRoles(),
                next.getAllowedApplicationRoles(),
                next.getDeniedApplicationRoles(),
                next.getAllowedUsers(),
                next.getDeniedUsers()
        };
    }
}
