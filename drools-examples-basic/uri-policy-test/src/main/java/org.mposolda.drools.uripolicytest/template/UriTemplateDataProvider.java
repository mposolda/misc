package org.mposolda.drools.uripolicytest.template;

import org.drools.template.DataProvider;

import java.util.Iterator;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class UriTemplateDataProvider implements DataProvider {

    private final Iterator<UriTemplate> uriTemplateIterator;

    public UriTemplateDataProvider(Iterator<UriTemplate> uriTemplateIterator) {
        this.uriTemplateIterator = uriTemplateIterator;
    }

    @Override
    public boolean hasNext() {
        return uriTemplateIterator.hasNext();
    }

    @Override
    public String[] next() {
        UriTemplate next = uriTemplateIterator.next();
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
