package org.mposolda.mongodb;

import java.util.Set;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public interface DAOApi {

    // return generated ID of added order
    public String addOrder(int custId, int orderId);

    // remove order including items and orderTimes
    public void removeOrder(Order order);

    public Order getOrder(String oid);

    public Order getOrder(int custId, int orderId);

    public Set<Order> getOrders(int custId);

    // get orders, which have given custId OR given orderId
    public Set<Order> getOrdersOr(int custId, int orderId);

    public Set<Item> getItemsOfOrder(Order order);

    public void pushItemToOrder(Order order, Item item);

    public void removeItemFromOrder(Order order, String itemName);

    public Set<OrderTime> getOrderTimesOfOrder(Order order);

    public OrderTime getOrderTime(String oid);

    public void addOrderTimeToOrder(Order order, int time);
}
