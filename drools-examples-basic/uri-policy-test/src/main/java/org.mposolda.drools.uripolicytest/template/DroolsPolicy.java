package org.mposolda.drools.uripolicytest.template;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.template.DataProviderCompiler;
import org.mposolda.drools.uripolicytest.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DroolsPolicy {

    private RuleBase ruleBase;


    public void init() {
        ruleBase = RuleBaseFactory.newRuleBase();

//        // Add DRL with functions
//        InputStream functionsFileStream = DroolsPolicy.class.getResourceAsStream("URIPolicyFunctions.drl");
//        Reader functionsFileReader = new InputStreamReader(functionsFileStream);
//        addPackageToRuleBase(functionsFileReader);
    }


    public void addURIPolicyEntry(URIPolicyEntry uriPolicyEntry) {
        // Create String containing rules for all uriPolicyEntries
        InputStream templateStream = DroolsPolicy.class.getResourceAsStream("URIPolicyTemplate.drl");
        URIPolicyTemplateDataProvider tdp = new URIPolicyTemplateDataProvider(uriPolicyEntry);
        DataProviderCompiler converter = new DataProviderCompiler();
        String drl = converter.compile(tdp, templateStream);

        // TODO:Logging
        System.out.println("------------ ADDING NEW POLICIES INTO DROOLS ENGINE ----------------------");
        System.out.println(drl);
        System.out.println("------------ END ADDING NEW POLICIES INTO DROOLS ENGINE ------------------");

        // Then add it into drools RuleBase
        addPackageToRuleBase(drl);
    }


    public AuthorizationDecision isRequestAuthorized(RequestInfo request, Token token) {
        WorkingMemory workingMemory = null;

        try {
            workingMemory = ruleBase.newStatefulSession();

            RulesProcessingResult rulesProcessingResult = new RulesProcessingResult();
            workingMemory.insert(rulesProcessingResult);

            EndSemaphore endSemaphore = new EndSemaphore();
            workingMemory.insert(endSemaphore);

            URIMatcherCache cache = new URIMatcherCache();
            workingMemory.insert(cache);

            workingMemory.insert(request);

            workingMemory.insert(token);

            // Uncomment for drools debugging (TODO: should be somehow configurable...)
            //workingMemory.addEventListener(new DebugAgendaEventListener());
            //workingMemory.addEventListener( new DebugWorkingMemoryEventListener() );

            int numberOfFiredPolicies = workingMemory.fireAllRules();

            // TODO: Replace with logging
            System.out.println("numberOfFiredPolicies=" + numberOfFiredPolicies + ", result=" + rulesProcessingResult.getDecision());
            return rulesProcessingResult.getDecision();
        } finally {
            if (workingMemory != null) {
                workingMemory.dispose();
            }
        }
    }


    protected void addPackageToRuleBase(String drl) {
        Reader uriPolicyEntriesReader = new StringReader(drl);
        addPackageToRuleBase(uriPolicyEntriesReader);
    }


    protected void addPackageToRuleBase(Reader packageReader) {
        PackageBuilder packageBuilder = new PackageBuilder();

        try {
            // Add DRL with functions
            InputStream functionsFileStream = DroolsPolicy.class.getResourceAsStream("URIPolicyFunctions.drl");
            Reader functionsFileReader = new InputStreamReader(functionsFileStream);

            packageBuilder.addPackageFromDrl(functionsFileReader);
            packageBuilder.addPackageFromDrl(packageReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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

        org.drools.rule.Package rulesPackage = packageBuilder.getPackage();
        ruleBase.addPackage(rulesPackage);
    }
}
