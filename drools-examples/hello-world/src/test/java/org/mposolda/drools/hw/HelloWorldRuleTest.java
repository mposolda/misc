package org.mposolda.drools.hw;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.drools.compiler.PackageBuilderErrors;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Unit test for simple Message.
 */
public class HelloWorldRuleTest {

    @Test
    public void shouldFireHelloWorld() throws IOException, DroolsParserException {
        RuleBase ruleBase = initialiseDrools();
        WorkingMemory workingMemory = initializeMessageObjects(ruleBase);
        int expectedNumberOfRulesFired = 2;

        int actualNumberOfRulesFired = workingMemory.fireAllRules();

        Assert.assertEquals(actualNumberOfRulesFired, expectedNumberOfRulesFired);
    }

    private RuleBase initialiseDrools() throws IOException, DroolsParserException {
        PackageBuilder packageBuilder = readRuleFiles();
        return addRulesToWorkingMemory(packageBuilder);
    }

    private PackageBuilder readRuleFiles() throws DroolsParserException, IOException {
        PackageBuilder packageBuilder = new PackageBuilder();

        String ruleFile = "/org/mposolda/drools/hw/resource/helloWorld.drl";
        Reader reader = getRuleFileAsReader(ruleFile);
        packageBuilder.addPackageFromDrl(reader);

        String ruleFile2 = "/org/mposolda/drools/hw/resource/actionRule.drl";
        Reader reader2 = getRuleFileAsReader(ruleFile2);
        packageBuilder.addPackageFromDrl(reader2);

        assertNoRuleErrors(packageBuilder);

        return packageBuilder;
    }

    private Reader getRuleFileAsReader(String ruleFile) {
        InputStream resourceAsStream = getClass().getResourceAsStream(ruleFile);

        return new InputStreamReader(resourceAsStream);
    }

    private void assertNoRuleErrors(PackageBuilder packageBuilder) {
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
    }

    private RuleBase addRulesToWorkingMemory(PackageBuilder packageBuilder) {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package rulesPackage = packageBuilder.getPackage();
        ruleBase.addPackage(rulesPackage);

        return ruleBase;
    }

    private WorkingMemory initializeMessageObjects(RuleBase ruleBase) {
        WorkingMemory workingMemory = ruleBase.newStatefulSession();

        createMessages(workingMemory);

        return workingMemory;
    }

    private void createMessages(WorkingMemory workingMemory) {
        Message helloMessage = new Message();
        helloMessage.setType("Hello");
        workingMemory.insert(helloMessage);

        Message message10 = new Message();
        message10.setType("Message 10");
        message10.setMessageValue(10);
        workingMemory.insert(message10);

        Message message20 = new Message();
        message20.setType("Message 20");
        message20.setMessageValue(20);
        workingMemory.insert(message20);
    }
}
