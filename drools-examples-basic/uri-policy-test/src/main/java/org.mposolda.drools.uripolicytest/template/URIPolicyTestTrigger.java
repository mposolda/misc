package org.mposolda.drools.uripolicytest.template;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.template.DataProviderCompiler;
import org.mposolda.drools.uripolicytest.*;

import java.io.*;
import java.util.*;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class URIPolicyTestTrigger {

    public static void main(String[] args) throws Exception {
        DroolsPolicy policy = new DroolsPolicy();
        policy.init();

        URIPolicyEntry policy1 = URIPolicyEntry.createEntry(10, "/something/amos",
                "requestParam(\"param1\").toString() == \"value1\" && requestParam(\"param2\").toInt() >= 10",
                "\"role1\", \"role2\"", "\"molok\"", null, null, "\"joohn\"", null);

        URIPolicyEntry policy2 = URIPolicyEntry.createEntry(8, "/something/([abc].*)",
                "requestParam(\"param1\").toString() == \"value1\" && requestParam(\"param2\").toInt() >= 10",
                "\"role1\", $uriMatcher.group(1)", null, null, null, "\"joohn\"", null);

        URIPolicyEntry policy3 = URIPolicyEntry.createEntry(8, "/something/{ $token.username }",
                "requestParam(\"param1\").toString() == $uriMatcher.group(1)", "\"role1\", \"role2\"", null, null, null, "\"john\"", null);

        List<URIPolicyEntry> uriPolicies = new ArrayList<URIPolicyEntry>();
        uriPolicies.add(policy1);
        uriPolicies.add(policy2);
        uriPolicies.add(policy3);

        policy.addURIPolicyEntries(uriPolicies);


        RequestInfo request = new RequestInfo("/something/john");
        request.addRequestParam("param1", "john");
        request.addRequestParam("param2", "10");

        List<String> realmRoles =  Arrays.asList(new String[]{"amos", "kolok", "bar"});
        List<String> appRoles =  Arrays.asList(new String[]{"appr1", "appr2", "appr3"});
        Token token = new Token("john", realmRoles, appRoles);

        AuthorizationDecision decision = policy.isRequestAuthorized(request, token);
        System.out.println("DECISION: " + decision);

    }
}
