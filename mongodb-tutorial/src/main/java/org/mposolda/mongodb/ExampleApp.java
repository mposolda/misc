package org.mposolda.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
        DBCollection orderTimes = javaDB.getCollection("orderTimes");
        DBCollection customers = javaDB.getCollection("customers");

        orders.drop();
        orderTimes.drop();
        customers.drop();

        writeInitialCustomers(customers);
        readInitialCustomers(customers);

        testDAO(orders, orderTimes);

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


    private static void writeInitialCustomers(DBCollection customers) {
        List<String> embedded = new ArrayList<String>(); //Arrays.asList(new String[] { "Venca", "Jenda", "Cenda"});

        BasicDBObject dbObject = new BasicDBObject("firstName", "John").append("lastName", "Doe").append("createdData", new Date());
        dbObject.append("kids", embedded );

        WriteResult result = customers.insert(dbObject);
        System.out.println("Added customer with result: " + result);
    }

    private static void readInitialCustomers(DBCollection customers) {
        DBObject found = customers.findOne();
        System.out.println("Found object: " + found);

        List<String> kids = (List)found.get("kids");

        // Remove item "Venca" from kids
        BasicDBObject query = new BasicDBObject("_id", found.get("_id"));
        BasicDBObject pullCommand = new BasicDBObject("$pull", new BasicDBObject("kids", "Venca"));
        BasicDBObject pushCommand = new BasicDBObject("$push", new BasicDBObject("kids", "Zdenda"));
        customers.update(query, pullCommand);
        customers.update(query, pushCommand);

        DBObject found2 = customers.findOne();
        System.out.println("Found object after unshift: " + found2);
    }

    private static void testDAO(DBCollection orders, DBCollection orderTimes) {
        System.out.println("testDAO()");
        DAOApi dao = new DAO(orders, orderTimes);

        // Add some orders first
        String oid1 = dao.addOrder(1, 1);
        String oid2 = dao.addOrder(1, 2);
        String oid3 = dao.addOrder(2, 3);

        Order order1 = dao.getOrder(oid1);
        Order order2 = dao.getOrder(oid2);
        Order order3 = dao.getOrder(2, 3);
        Order order4 = dao.getOrder(2, 4);

        System.out.println(order1);
        System.out.println(order2);
        System.out.println(order3);
        System.out.println(order4);
        System.out.println("-------------------");

        Set<Order> orders1 = dao.getOrders(1);
        Set<Order> orders2 = dao.getOrders(2);
        Set<Order> orders3 = dao.getOrdersOr(1, 3);
        Set<Order> orders4 = dao.getOrdersOr(1, 2);

        System.out.println(orders1);
        System.out.println(orders2);
        System.out.println(orders3);
        System.out.println(orders4);
        System.out.println("-------------------");

        dao.pushItemToOrder(order1, new Item("item1", 100));
        dao.pushItemToOrder(order1, new Item("item2", 200));
        dao.pushItemToOrder(order2, new Item("item3", 300));
        Set<Item> items1 = dao.getItemsOfOrder(order1);
        Set<Item> items2 = dao.getItemsOfOrder(order2);
        Set<Item> items3 = dao.getItemsOfOrder(order3);

        System.out.println(items1);
        System.out.println(items2);
        System.out.println(items3);
        System.out.println("-------------------");

        dao.removeItemFromOrder(order1, "item1");
        items1 = dao.getItemsOfOrder(order1);
        System.out.println(items1);
        System.out.println("-------------------");

        dao.addOrderTimeToOrder(order1, 12);
        dao.addOrderTimeToOrder(order1, 34);
        dao.addOrderTimeToOrder(order2, 56);
        Set<OrderTime> orderTimes1 = dao.getOrderTimesOfOrder(order1);
        Set<OrderTime> orderTimes2 = dao.getOrderTimesOfOrder(order2);
        OrderTime orderTime1 = dao.getOrderTime(orderTimes1.iterator().next().getOid());

        System.out.println(orderTimes1);
        System.out.println(orderTimes2);
        System.out.println(orderTime1);
        System.out.println("-------------------");

        // Cleanup everything
        dao.removeOrder(order1);
        dao.removeOrder(order2);
        dao.removeOrder(order3);
        System.out.println("testDAO() finished --------------------------");
    }
}
