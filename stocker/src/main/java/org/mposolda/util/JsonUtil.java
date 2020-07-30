package org.mposolda.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.mposolda.client.JsonSerialization;
import org.mposolda.reps.DatabaseRep;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class JsonUtil {

    public static DatabaseRep loadDatabase(String companiesJsonFileLocation) {
        return loadFileToJson(companiesJsonFileLocation, DatabaseRep.class);
    }

    public static <T> T loadFileToJson(String fileLocation, Class<T> clazz) {
        try {
            return JsonSerialization.readValue(new FileInputStream(fileLocation), clazz);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static void saveJsonToFile(String fileLocation, Object rep) {
        try {
            FileOutputStream stream = new FileOutputStream(fileLocation);
            JsonSerialization.prettyMapper.writeValue(stream, rep);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
