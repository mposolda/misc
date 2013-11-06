package org.mposolda.drools.uripolicytest;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderErrors;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * Hello world!
 *
 */
public class UriPolicyTrigger {

    public static void main( String[] args ) throws Exception {
        RuleBase ruleBase = initDrools();

        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        RulesProcessingResult rulesProcessingResult = new RulesProcessingResult();
        workingMemory.insert(rulesProcessingResult);

        RequestInfo uriInput = new RequestInfo("/kokos/mlok");
        uriInput.addRequestParam("param1", "value1");
        uriInput.addRequestParam("param2", "value2");
        workingMemory.insert(uriInput);

        List<String> roles =  Arrays.asList(new String[] {"mlok", "kolok", "bar"});
        Token token = new Token("mlok", roles, null);
        workingMemory.insert(token);

        URIMatcher mi = new URIMatcher();
        workingMemory.insert(mi);

        int numberOfFiredPolicies = workingMemory.fireAllRules();
        System.out.println("numberOfFiredPolicies=" + numberOfFiredPolicies + ", rules=" + rulesProcessingResult.getDecision());
    }

    private static RuleBase initDrools() throws IOException, DroolsParserException {
        PackageBuilder packageBuilder = new PackageBuilder();
        String ruleFile = "/org/mposolda/drools/uripolicytest/uriPolicyTest.drl";
        InputStream resourceAsStream = UriPolicyTrigger.class.getResourceAsStream(ruleFile);
        Reader reader = new InputStreamReader(resourceAsStream);
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
