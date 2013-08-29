package org.mposolda.mongodb;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Order {

    private final String oid;
    private final int custId;
    private final int orderId;

    public Order(String oid, int custId, int orderId) {
        this.oid = oid;
        this.custId = custId;
        this.orderId = orderId;
    }

    public String getOid() {
        return oid;
    }

    public int getCustId() {
        return custId;
    }

    public int getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "Order [oid=" + oid + ", custId=" + custId + ", orderId=" + orderId + "]";
    }
}
