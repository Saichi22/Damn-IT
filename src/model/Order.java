package model;

import java.util.ArrayList;
import exception.OutOfStockException;

public class Order {
	private ArrayList<OrderItem> items = new ArrayList<>();
    private static final double TAX_RATE = 0.12;        // 12% VAT
    private static final double DISCOUNT_RATE = 0.10;   // 10% discount
    private boolean discountApplied = false;

    public ArrayList<OrderItem> getItems() { return items; }

    /*
     * Adds an item. If the product is already in the order,
     * the quantities are combined. Checks stock before adding.
     */
    public void addItem(Product p, int qty) throws OutOfStockException {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(p.getId())) {
                int newQty = item.getQuantity() + qty;
                if (newQty > p.getStock()) {
                    throw new OutOfStockException("Only " + p.getStock() + " left for " + p.getName());
                }
                item.setQuantity(newQty);
                return;
            }
        }
        if (qty > p.getStock()) {
            throw new OutOfStockException("Only " + p.getStock() + " left for " + p.getName());
        }
        items.add(new OrderItem(p, qty));
    }

    public void removeItem(int index) { items.remove(index); }
    public void clear() { items.clear(); discountApplied = false; }

    public void setDiscountApplied(boolean applied) { this.discountApplied = applied; }

    public double getSubtotal() {
        double sum = 0;
        for (OrderItem item : items) sum += item.getLineTotal();
        return sum;
    }

    public double getDiscount() {
        return discountApplied ? getSubtotal() * DISCOUNT_RATE : 0;
    }

    public double getTax() {
        return (getSubtotal() - getDiscount()) * TAX_RATE;
    }

    public double getTotal() {
        return getSubtotal() - getDiscount() + getTax();
    }
}
