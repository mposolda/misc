package org.mposolda.drools.uripolicytest.template;

import org.drools.template.DataProvider;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Drools DataProvider for providing data about URI policies configured by user. Data are used to compile Drools template into
 * real rules for Drools engine
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class URIPolicyTemplateDataProvider implements DataProvider {

    private final URIPolicyEntry uriPolicyEntry;
    private boolean processed = false;

    private static AtomicInteger counter = new AtomicInteger(0);

    public URIPolicyTemplateDataProvider(URIPolicyEntry uriPolicyEntry) {
        this.uriPolicyEntry = uriPolicyEntry;
    }

    @Override
    public boolean hasNext() {
        return !processed;
    }

    @Override
    public String[] next() {
        processed = true;

        return new String[] {
                String.valueOf(counter.getAndIncrement()),
                String.valueOf(uriPolicyEntry.getPriority()),
                String.valueOf(uriPolicyEntry.getPriority() - 1),
                uriPolicyEntry.getUriPattern(),
                uriPolicyEntry.getQueryParamsCondition(),
                uriPolicyEntry.getAllowedRealmRoles(),
                uriPolicyEntry.getDeniedRealmRoles(),
                uriPolicyEntry.getAllowedApplicationRoles(),
                uriPolicyEntry.getDeniedApplicationRoles(),
                uriPolicyEntry.getAllowedUsers(),
                uriPolicyEntry.getDeniedUsers()
        };
    }
}
