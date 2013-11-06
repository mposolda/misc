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
        URIPolicy policy1 = new URIPolicy(10, "\"^/something/amos$\"",
                "requestParam(\"param1\").toString() == \"value1\" && requestParam(\"param2\").toInt() >= 10",
                "\"role1\", \"role2\"", "\"molok\"", null, null, "\"joohn\"", null);
        URIPolicy policy2 = new URIPolicy(8, "\"^/something/([abc].*)$\"",
                "requestParam(\"param1\").toString() == \"value1\" && requestParam(\"param2\").toInt() >= 10",
                "\"role1\", $uriMatcher.group(1)", null, null, null, "\"joohn\"", null);
        URIPolicy policy3 = new URIPolicy(8, "\"^/something/(\" + $token.username + \")$\"",
                "requestParam(\"param1\").toString() == $uriMatcher.group(1)", "\"role1\", \"role2\"", null, null, null, "\"john\"", null);
        List<URIPolicy> uriPolicies = new ArrayList<URIPolicy>();
        uriPolicies.add(policy1);
        uriPolicies.add(policy2);
        uriPolicies.add(policy3);

        String template = buildTemplate(uriPolicies);
        System.out.println(template);


        RuleBase ruleBase = initDrools(template);

        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        RulesProcessingResult rulesProcessingResult = new RulesProcessingResult();
        workingMemory.insert(rulesProcessingResult);

        EndSemaphore endSemaphore = new EndSemaphore();
        workingMemory.insert(endSemaphore);

        RequestInfo uriInput = new RequestInfo("/something/john");
        uriInput.addRequestParam("param1", "john");
        uriInput.addRequestParam("param2", "10");
        workingMemory.insert(uriInput);

        List<String> realmRoles =  Arrays.asList(new String[]{"amos", "kolok", "bar"});
        List<String> appRoles =  Arrays.asList(new String[]{"appr1", "appr2", "appr3"});
        Token token = new Token("john", realmRoles, appRoles);
        workingMemory.insert(token);

        URIMatcherCache cache = new URIMatcherCache();
        workingMemory.insert(cache);

        //workingMemory.addEventListener(new DebugAgendaEventListener());
        //workingMemory.addEventListener( new DebugWorkingMemoryEventListener() );

        int numberOfFiredPolicies = workingMemory.fireAllRules();
        System.out.println("numberOfFiredPolicies=" + numberOfFiredPolicies + ", rules=" + rulesProcessingResult.getDecision());

    }

    private static String buildTemplate(List<URIPolicy> uriPolicies) {
        InputStream templateStream = URIPolicyTestTrigger.class.getResourceAsStream("URIPolicyTemplate.drl");
        URIPolicyTemplateDataProvider tdp = new URIPolicyTemplateDataProvider(uriPolicies.iterator());
        DataProviderCompiler converter = new DataProviderCompiler();
        String drl = converter.compile(tdp, templateStream);
        return drl;
    }


    private static RuleBase initDrools(String templateString) throws IOException, DroolsParserException {
        PackageBuilder packageBuilder = new PackageBuilder();

        // Add DRL with functions
        InputStream resourceAsStream = URIPolicyTestTrigger.class.getResourceAsStream("URIPolicyFunctions.drl");
        Reader reader = new InputStreamReader(resourceAsStream);
        packageBuilder.addPackageFromDrl(reader);

        // Add DRL based on template
        reader = new StringReader(templateString);
        packageBuilder.addPackageFromDrl(reader);

        PackageBuilderErrors errors = packageBuilder.getErrors();
        if (errors.getErrors().length > 0) {
            StringBuilder errorMessages = new StringBuilder();
            errorMessages.append("Found errors in package builder\n");
            for (int i = 0; i < errors.getErrors().length; i++) {
                DroolsError errorMessage = errors.getErrors()[i];
                errorMessages.append(errorMessage);
                errorMessages.append("\n");
            }
            errorMessages.append("Could not parse knowledge");

            throw new IllegalArgumentException(errorMessages.toString());
        }

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        org.drools.rule.Package rulesPackage = packageBuilder.getPackage();
        ruleBase.addPackage(rulesPackage);
        return ruleBase;
    }
}
