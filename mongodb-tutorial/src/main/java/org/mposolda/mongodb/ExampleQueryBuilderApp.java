package org.mposolda.mongodb;

import java.util.regex.Pattern;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ExampleQueryBuilderApp {

    public static void main(String[] args) throws Exception {
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        DB keycloakDB = mongoClient.getDB("keycloak");

        DBCollection userCollection = keycloakDB.getCollection("userrr");

        String s = "joe anthon";
        s = s.trim();
        Pattern caseInsensitivePattern = Pattern.compile("(?i:" + s + ")");

        QueryBuilder nameBuilder;
        int spaceInd = s.lastIndexOf(" ");

        // Case when we have search string like "ohn Bow". Then firstName must end with "ohn" AND lastName must start with "bow" (everything case-insensitive)
        if (spaceInd != -1) {
            String firstName = s.substring(0, spaceInd);
            String lastName = s.substring(spaceInd + 1);
            System.out.println("firstName: " + firstName + ", lastName: " + lastName);
            Pattern firstNamePattern =  Pattern.compile("(?i:" + firstName + "$)");
            Pattern lastNamePattern =  Pattern.compile("(?i:^" + lastName + ")");
            nameBuilder = new QueryBuilder().and(
                    new QueryBuilder().put("firstName").regex(firstNamePattern).get(),
                    new QueryBuilder().put("lastName").regex(lastNamePattern).get()
            );
        } else {
            // Case when we have search without spaces like "foo". The firstName OR lastName could be "foo"
            nameBuilder = new QueryBuilder().or(
                    new QueryBuilder().put("firstName").regex(caseInsensitivePattern).get(),
                    new QueryBuilder().put("lastName").regex(caseInsensitivePattern).get()
            );
        }

        QueryBuilder builder = new QueryBuilder().and(
                new QueryBuilder().put("realmId").is(123).get(),
                new QueryBuilder().or(
                        new QueryBuilder().put("loginName").regex(caseInsensitivePattern).get(),
                        new QueryBuilder().put("email").regex(caseInsensitivePattern).get(),
                        nameBuilder.get()

                ).get()
        );


        DBCursor cursor = userCollection.find(builder.get());

        while (cursor.hasNext()) {
            System.out.println(cursor.next());
        }
    }
}
