package org.mposolda.drools.uripolicytest.template;

import org.mposolda.drools.uripolicytest.*;

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
                "CREATE",
                "\"role1\", \"role2\"", "\"molok\"", null, null, "\"joohn\"", null);

        URIPolicyEntry policy2 = URIPolicyEntry.createEntry(9, "/something/([abc].*)",
                "requestParam(\"param1\").toString() == \"value1\" && requestParam(\"param2\").toInt() >= 10",
                "*",
                "\"role1\", $uriMatcher.group(1)", null, null, null, "\"joohn\"", null);

        URIPolicyEntry policy3 = URIPolicyEntry.createEntry(8, "/something/{ $token.username }",
                "requestParam(\"param1\").toString() == $uriMatcher.group(1)",
                "CREATE, READ",
                "\"role1\", \"role2\"", null, null, null, "\"john\"", null);

        // Increase priority to 8 will REJECT result!
        URIPolicyEntry policy4 = URIPolicyEntry.createEntry(7, "/something/john",
                null, "UPDATE, DELETE", null, "\"bar\"", null, null, "\"john\"", null);

        policy.addURIPolicyEntry(policy1);
        policy.addURIPolicyEntry(policy2);
        policy.addURIPolicyEntry(policy3);
        policy.addURIPolicyEntry(policy4);

        RequestInfo request = new RequestInfo("/something/john", RequestType.CREATE);
        request.addRequestParam("param1", "john");
        request.addRequestParam("param2", "10");

        List<String> realmRoles =  Arrays.asList(new String[]{"amos", "kolok", "bar"});
        List<String> appRoles =  Arrays.asList(new String[]{"appr1", "appr2", "appr3"});
        Token token = new Token("john", realmRoles, appRoles);

        AuthorizationDecision decision = policy.isRequestAuthorized(request, token);
        System.out.println("DECISION: " + decision);

    }
}
