package org.mposolda;

import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import freemarker.template.Configuration;
import freemarker.template.Template;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

/**
 * Hello world!
 *
 */
public class SparkFreemarkerSample
{
    public static void main( String[] args )
    {
        Spark.get(new Route("/hello") {

            @Override
            public Object handle(Request request, Response response) {
                response.header("Content-Type", "text/html");

                Configuration config = initFreemarker();
                String orderId = request.queryParams("orderId");
                if (orderId == null || orderId.length() == 0) {
                    return "You need to provide parameter 'orderId'";
                }

                try {
                    Object dbObject = readDataFromMongoDB(Integer.parseInt(orderId));
                    if (dbObject == null) {
                        return "Not found object with orderId " + orderId;
                    } else {
                        return processTemplate(config, "template.ftl", dbObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.status(500);
                    return "Error 500 - occured";
                }
            }

        });
    }

    private static Configuration initFreemarker() {
        Configuration config = new Configuration();
        config.setClassForTemplateLoading(SparkFreemarkerSample.class, "/");
        return config;
    }

    private static String processTemplate(Configuration config, String templatePath, Object templateParams) throws Exception {
        Template template = config.getTemplate(templatePath);
        StringWriter writer = new StringWriter();
        template.process(templateParams, writer);

        return writer.toString();
    }

    private static DBObject readDataFromMongoDB(int orderId) throws UnknownHostException {
        MongoClient client = null;
        try {
            client = new MongoClient("localhost", 27017);
            DB courseDB = client.getDB("course");
            DBCollection collection = courseDB.getCollection("orders");
            DBObject dbObject = collection.findOne(new BasicDBObject("orderId", orderId));
            return dbObject;
        } finally {
            client.close();
        }
    }
}
