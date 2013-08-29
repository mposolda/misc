package org.mposolda.mongodb;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class Item {

    private final String itemName;
    private final int cost;

    public Item(String itemName, int cost) {
        this.itemName = itemName;
        this.cost = cost;
    }

    public String getItemName() {
        return itemName;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Item [itemName=" + itemName + ", cost=" + cost + "]";
    }
}
