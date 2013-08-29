package org.mposolda.mongodb;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OrderTime {

    private final String oid;
    private final int time;
    private final Order order;

    public OrderTime(String oid, int time, Order order) {
        this.oid = oid;
        this.time = time;
        this.order = order;
    }

    public String getOid() {
        return oid;
    }

    public int getTime() {
        return time;
    }

    public Order getOrder() {
        return order;
    }

    public String toString() {
        return "OrderTime [oid=" + oid + ", time=" + time + ", orderId=" + order.getOid();
    }
}
