package org.mposolda.drools.uripolicytest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mposolda.drools.uripolicytest.template.DroolsPolicy;
import org.mposolda.drools.uripolicytest.template.URIPolicyEntry;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DroolsPolicyTest {

    private static DroolsPolicy droolsPolicy;

    @BeforeClass
    public static void initDrools() {
        droolsPolicy = new DroolsPolicy();
        droolsPolicy.init();

        // Rule specifies that READ requests to '/droolsTest/foo' are accepted for members of realm roles "role1" and "role2"
        URIPolicyEntry rule1 = URIPolicyEntry.createEntry(8, "/droolsTest/foo", null,
                "READ", "\"role1\", \"role2\"", null, null, null, null, null);

        // Rule specifies that requests to '/droolsTest/*' are accepted if params condition matched as well
        URIPolicyEntry rule2 = URIPolicyEntry.createEntry(9, "/droolsTest/*", "requestParam(\"param1\").toString() == \"foo\" && requestParam(\"param2\").toInt() >= 10",
                "*" ,null , null, null, null, "\"*\"", null);

        // Rule specifies that requests, which permits everything if URI matches regex and all params condition matched as well
        URIPolicyEntry rule3 = URIPolicyEntry.createEntry(9, "/droolsTest/*/bar/([abc].*)", "requestParam(\"param1\").toString() == $uriMatcher.group(1) && requestParam(\"param2\").toString() == $uriMatcher.group(2) && requestParam(\"param3\").toString() == $token.username",
                "*" ,null , null, null, null, "\"*\"", null);


        // Rule specifies that URI like '/droolsTest/foo' is available for user with username 'foo' (Last part must be username of current user)
        URIPolicyEntry rule4 = URIPolicyEntry.createEntry(10, "/droolsTest/{ $token.username }", null,
                "*", null, null, null, null, "$token.username", null);

        // Rule specifies that URI like '/droolsTest/foo' is available for user, which has realmRole 'foo' (Last part must be some realmRole of current user)
        URIPolicyEntry rule5 = URIPolicyEntry.createEntry(10, "/droolsTest/{ any($token.realmRoles) }", null,
                "*", null, null, null, null, "\"*\"", null);

        // Rule specifies that all requests to '/droolsTest/foo' are accepted for members of realm roles "role3" (similar to rule1, but this is for all requests)
        // NOTE: Read requests to /droolsTest/foo will be preferably processed by rule1 because it has bigger priority
        URIPolicyEntry rule6 = URIPolicyEntry.createEntry(5, "/droolsTest/foo", null,
                "*", null, "\"role1\"", null, null, "\"*\"", null);


        // Killer rule with big priority. Automatically denies all requests if user is member of realm role "evilRole"
        URIPolicyEntry rule7 = URIPolicyEntry.createEntry(20, "/droolsTest/*", null,
                "*", null, "\"evilRole\"", null, null, null, null);

        droolsPolicy.addURIPolicyEntry(rule1);
        droolsPolicy.addURIPolicyEntry(rule2);
        droolsPolicy.addURIPolicyEntry(rule3);
        droolsPolicy.addURIPolicyEntry(rule4);
        droolsPolicy.addURIPolicyEntry(rule5);
        droolsPolicy.addURIPolicyEntry(rule6);
        droolsPolicy.addURIPolicyEntry(rule7);
    }

    @Test
    public void testPolicy() {
        Token john = new Token("john", Arrays.asList(new String[]{ "role1" }), Collections.EMPTY_LIST);
        Token evil = new Token("someEvilUser", Arrays.asList(new String[]{ "evilRole" }), Collections.EMPTY_LIST);

        RequestInfo request1 = new RequestInfo("/droolsTest/foo", RequestType.READ);
        // Accepted because of rule1
        Assert.assertEquals(AuthorizationDecision.ACCEPT, droolsPolicy.isRequestAuthorized(request1, john));
        // Rejected because of rule7, which has biggest priority and so it effectively rejects all requests of user with role 'evilRole'
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request1, evil));

        RequestInfo request2 = new RequestInfo("/droolsTest/foo/bar", RequestType.READ);
        // Ignored because there is not matching rule (Rule1 is just for /droolsTest/foo but not for /droolsTest/foo/*
        Assert.assertEquals(AuthorizationDecision.IGNORE, droolsPolicy.isRequestAuthorized(request2, john));
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request2, evil));

        RequestInfo request3 = new RequestInfo("/droolsTest/foo/bar", RequestType.READ);
        request3.addRequestParam("param1", "foo");
        request3.addRequestParam("param2", "11");
        // Accepted because of rule2 (Both URI and parameter conditions match)
        Assert.assertEquals(AuthorizationDecision.ACCEPT, droolsPolicy.isRequestAuthorized(request3, john));
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request3, evil));

        RequestInfo request4 = new RequestInfo("/droolsTest/foo/bar", RequestType.READ);
        request4.addRequestParam("param1", "foo");
        request4.addRequestParam("param2", "9");
        // Ignored. Doesn't match rule2 because param2 is lower than 10
        Assert.assertEquals(AuthorizationDecision.IGNORE, droolsPolicy.isRequestAuthorized(request4, john));
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request4, evil));

        RequestInfo request5 = new RequestInfo("/droolsTest/foo/bar/baz", RequestType.READ);
        request5.addRequestParam("param1", "foo");
        request5.addRequestParam("param2", "baz");
        request5.addRequestParam("param3", "john");
        // Accepted because of rule3
        Assert.assertEquals(AuthorizationDecision.ACCEPT, droolsPolicy.isRequestAuthorized(request5, john));
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request5, evil));

        RequestInfo request6 = new RequestInfo("/droolsTest/foo/bar/baz", RequestType.READ);
        request6.addRequestParam("param1", "foo");
        request6.addRequestParam("param2", "baz");
        request6.addRequestParam("param3", "mary");
        // Ignored. Doesn't match rule3 because param3 has different value than actual username (john)
        Assert.assertEquals(AuthorizationDecision.IGNORE, droolsPolicy.isRequestAuthorized(request6, john));
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request6, evil));

        RequestInfo request7 = new RequestInfo("/droolsTest/foo/bar/baz", RequestType.READ);
        request7.addRequestParam("param1", "foo");
        request7.addRequestParam("param2", "baaz");
        request7.addRequestParam("param3", "john");
        // Ignored. Doesn't match rule3 because param2 has different value than the parsed value from regex from URI (baz)
        Assert.assertEquals(AuthorizationDecision.IGNORE, droolsPolicy.isRequestAuthorized(request7, john));
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request7, evil));

        RequestInfo request8 = new RequestInfo("/droolsTest/john", RequestType.READ);
        // Accepted because of rule4, which allows every user to visit URI like "/droolsTest/foo" if his username is "foo"
        Assert.assertEquals(AuthorizationDecision.ACCEPT, droolsPolicy.isRequestAuthorized(request8, john));
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request8, evil));

        RequestInfo request9 = new RequestInfo("/droolsTest/mary", RequestType.READ);
        // Ignored. Doesn't match rule4 or any other rule
        Assert.assertEquals(AuthorizationDecision.IGNORE, droolsPolicy.isRequestAuthorized(request9, john));
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request9, evil));

        RequestInfo request10 = new RequestInfo("/droolsTest/role1", RequestType.READ);
        // Accepted because of rule5, which allows any user to visit URI like '/droolsTest/foo' if he has realmRole 'foo'
        Assert.assertEquals(AuthorizationDecision.ACCEPT, droolsPolicy.isRequestAuthorized(request10, john));
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request10, evil));

        RequestInfo request11 = new RequestInfo("/droolsTest/role2", RequestType.READ);
        // Ignored.
        Assert.assertEquals(AuthorizationDecision.IGNORE, droolsPolicy.isRequestAuthorized(request11, john));
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request11, evil));

        // Similar to request1 but with RequestType.CREATE. Now it's rejected because of rule6
        RequestInfo request12 = new RequestInfo("/droolsTest/foo", RequestType.CREATE);
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request12, john));
        Assert.assertEquals(AuthorizationDecision.REJECT, droolsPolicy.isRequestAuthorized(request12, evil));

    }
}
