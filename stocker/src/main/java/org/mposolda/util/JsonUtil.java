package org.mposolda.util;

import java.io.FileInputStream;
import java.io.IOException;

import org.mposolda.client.JsonSerialization;
import org.mposolda.reps.DatabaseRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JsonUtil {

    public static DatabaseRep loadDatabase(String companiesJsonFileLocation) {
        try {
            return JsonSerialization.readValue(new FileInputStream(companiesJsonFileLocation), DatabaseRep.class);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
