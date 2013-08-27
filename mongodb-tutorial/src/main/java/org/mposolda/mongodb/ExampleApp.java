package org.mposolda.mongodb;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ExampleApp {

    public static void main(String[] args) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
        // mongoClient.setWriteConcern(WriteConcern.JOURNALED);

        printDBNames(mongoClient);

        DB javaDB = mongoClient.getDB("java");
        printCollectionNames(javaDB);

        DBCollection orders = javaDB.getCollection("orders");
        //writeInitialOrders(orders);
        printOrders(orders);
        // removeOrders(orders);

        mongoClient.close();
    }

    private static void printDBNames(MongoClient mongoClient) {
        System.out.println("GOING TO PRINT DATABASE NAMES: ");
        List<String> dbNames = mongoClient.getDatabaseNames();
        printList(dbNames);
        System.out.println("----------------------------------");
    }

    private static void printCollectionNames(DB javaDB) {
        System.out.println("GOING TO PRINT COLLECTIONS OF DATABASE: " + javaDB.getName());
        Set<String> collectionNames = javaDB.getCollectionNames();
        printList(collectionNames);
        System.out.println("----------------------------------");
    }

    private static void printList(Collection<String> list) {
        for (String item : list) {
            System.out.println(item);
        }
    }


    private static void writeInitialOrders(DBCollection orders) {
        BasicDBObject dbObject = new BasicDBObject("cust_id", 2).append("order_id", 3);
        BasicDBObject subitem1 = new BasicDBObject("item_name", "item5").append("cost", 50);
        BasicDBObject subitem2 = new BasicDBObject("item_name", "item6").append("cost", 10);
        dbObject.append("items", new BasicDBObject[] { subitem1, subitem2});

        WriteResult result = orders.insert(dbObject);
        System.out.println("Added item to orders with result: " + result);
    }

    private static void printOrders(DBCollection orders) {
        System.out.println("find()");
        DBCursor cursor = orders.find();
        printCursor(cursor);

        long count = orders.count();
        System.out.println("orders count: " + count);
        System.out.println("---------------");

        System.out.println("find({cust_id, 1})");
        cursor = orders.find(new BasicDBObject("cust_id", 1));
        printCursor(cursor);
    }

    private static void printCursor(DBCursor cursor) {
        for (DBObject dbObject : cursor) {
            System.out.println(dbObject);
        }
        System.out.println("---------------");
    }
}
