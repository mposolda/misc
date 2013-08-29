package org.mposolda.mongodb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class DAO implements DAOApi {

    private final DBCollection orders;
    private final DBCollection orderTimes;

    public DAO(DBCollection collection, DBCollection orderTimes) {
        this.orders = collection;
        this.orderTimes = orderTimes;
    }

    @Override
    public String addOrder(int custId, int orderId) {
        BasicDBObject dbOrder = new BasicDBObject("cust_id", custId).append("order_id", orderId);
        WriteResult wr = orders.insert(dbOrder);
        return dbOrder.get("_id").toString();
    }

    @Override
    public void removeOrder(Order order) {
        // Need to remove orderTimes first
        BasicDBObject orderTimeQuery = new BasicDBObject("order.$id", new ObjectId(order.getOid()));
        orderTimes.remove(orderTimeQuery);

        BasicDBObject orderQuery = new BasicDBObject("_id", new ObjectId(order.getOid()));
        orders.remove(orderQuery);
    }


    public Order getOrder(String oid) {
        DBObject dbObject = orders.findOne(new BasicDBObject("_id", new ObjectId(oid)));
        return dbObject == null ? null : convertOrder(dbObject);
    }

    public Order getOrder(int custId, int orderId) {
        BasicDBObject query1 = new BasicDBObject("cust_id", custId);
        query1.append("order_id", orderId);
        DBObject dbObject = orders.findOne(query1);
        return dbObject == null ? null : convertOrder(dbObject);
    }

    public Set<Order> getOrders(int custId) {
        BasicDBObject query1 = new BasicDBObject("cust_id", custId);
        DBCursor cursor = orders.find(query1);
        return convertOrders(cursor);
    }

    // get orders, which have given custId OR given orderId
    public Set<Order> getOrdersOr(int custId, int orderId) {
        BasicDBObject condition1 = new BasicDBObject("cust_id", custId);
        BasicDBObject condition2 = new BasicDBObject("order_id", orderId);
        BasicDBObject query = new BasicDBObject("$or", new BasicDBObject[] { condition1, condition2 });
        DBCursor cursor = orders.find(query);
        return convertOrders(cursor);
    }

    public Set<Item> getItemsOfOrder(Order order) {
        DBObject dbOrder = orders.findOne(new BasicDBObject("_id", new ObjectId(order.getOid())));
        if (dbOrder == null) {
            throw new IllegalStateException("Order with id " + order.getOid() + " not found");
        }

        List<DBObject> itemList = (List<DBObject>)dbOrder.get("items");
        Set<Item> result = new HashSet<Item>();

        if (itemList == null) {
            return result;
        }

        for (DBObject dbItem : itemList) {
            result.add(convertItem(dbItem));
        }
        return result;
    }

    public void pushItemToOrder(Order order, Item item) {
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(order.getOid()));
        BasicDBObject itemToPush = new BasicDBObject("item_name", item.getItemName()).append("cost", item.getCost());

        BasicDBObject pushCommand = new BasicDBObject("$push", new BasicDBObject("items", itemToPush));

        orders.update(query, pushCommand);
    }

    public void removeItemFromOrder(Order order, String itemName) {
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(order.getOid()));
        DBObject dbOrder = orders.findOne(query);
        if (dbOrder == null) {
            throw new IllegalStateException("Order with id " + order.getOid() + " not found");
        }

        List<DBObject> itemList = (List<DBObject>)dbOrder.get("items");
        List<DBObject> tmp = new ArrayList<DBObject>(itemList);
        for (DBObject dbItem : tmp) {
            if (itemName.equals(dbItem.get("item_name"))) {
                itemList.remove(dbItem);
            }
        }

        BasicDBObject setCommand = new BasicDBObject("$set", new BasicDBObject("items", itemList));

        orders.update(query, setCommand);
    }

    public Set<OrderTime> getOrderTimesOfOrder(Order order) {
        BasicDBObject query = new BasicDBObject("order.$id", new ObjectId(order.getOid()));
        DBCursor cursor = orderTimes.find(query);
        return convertOrderTimes(cursor, order);
    }

    @Override
    public OrderTime getOrderTime(String oid) {
        DBObject dbOrderTimeObject = orderTimes.findOne(new BasicDBObject("_id", new ObjectId(oid)));
        if (dbOrderTimeObject == null) {
            return null;
        }

        // Obtain and fetch the reference
        DBRef dbOrderRef = (DBRef)dbOrderTimeObject.get("order");
        DBObject dbOrderObj = dbOrderRef.fetch();

        Order order = convertOrder(dbOrderObj);

        return convertOrderTime(dbOrderTimeObject, order);
    }

    @Override
    public void addOrderTimeToOrder(Order order, int time) {
        BasicDBObject dbObject = new BasicDBObject("time", time);
        dbObject.append("order", new DBRef(orderTimes.getDB(), "orders", new ObjectId(order.getOid())));

        orderTimes.insert(dbObject);
    }

    private Order convertOrder(DBObject dbObject) {
        String id = dbObject.get("_id").toString();
        Integer customerId = ((Number)dbObject.get("cust_id")).intValue();
        Integer orderId = ((Number)dbObject.get("order_id")).intValue();
        return new Order(id, customerId, orderId);
    }

    private Item convertItem(DBObject dbObject) {
        String itemName = (String)dbObject.get("item_name");
        int cost = ((Number)dbObject.get("cost")).intValue();
        return new Item(itemName, cost);
    }

    private OrderTime convertOrderTime(DBObject dbObject, Order order) {
        String id = dbObject.get("_id").toString();
        Integer time = ((Number)dbObject.get("time")).intValue();
        return new OrderTime(id, time, order);
    }

    private Set<Order> convertOrders(DBCursor cursor) {
        Set<Order> orders = new HashSet<Order>();

        try {
            while (cursor.hasNext()) {
                orders.add(convertOrder(cursor.next()));
            }
        } finally {
            cursor.close();
        }

        return orders;
    }

    private Set<OrderTime> convertOrderTimes(DBCursor cursor, Order order) {
        Set<OrderTime> orderTimes = new HashSet<OrderTime>();

        try {
            while (cursor.hasNext()) {
                orderTimes.add(convertOrderTime(cursor.next(), order));
            }
        } finally {
            cursor.close();
        }

        return orderTimes;
    }
}
