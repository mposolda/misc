package org.mposolda.drools.uripolicytest.template;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class UriPolicyTemplateTrigger {

    public static void main(String[] args) throws Exception {
        UriTemplate template1 = new UriTemplate(10, "^/something/kokos$", "reqParams.get(\"param1\") == value1");
        UriTemplate template2 = new UriTemplate(10, "^/something/([abc].*)$", "reqParams.get(\"param1\") == value1");
        List<UriTemplate> uriTemplates = new ArrayList<UriTemplate>();
        uriTemplates.add(template1);
        uriTemplates.add(template2);

        String template = buildTemplate(uriTemplates);
        System.out.println(template);
        // RuleBase ruleBase = initDrools();

    }

    private static String buildTemplate(List<UriTemplate> uriTemplates) {
        InputStream templateStream = UriPolicyTemplateTrigger.class.getResourceAsStream("uriPolicyTemplateTest.drl");
        UriTemplateDataProvider tdp = new UriTemplateDataProvider(uriTemplates.iterator());
        DataProviderCompiler converter = new DataProviderCompiler();
        final String drl = converter.compile(tdp, templateStream);
        return drl;
    }

    /*
    private static RuleBase initDrools() throws IOException, DroolsParserException {
        PackageBuilder packageBuilder = new PackageBuilder();


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
    } */
}
