package model;

import java.util.ArrayList;
import exception.InvalidQuantityException;
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
    public void addItem(Product p, int qty) throws OutOfStockException, InvalidQuantityException {
        if (qty <= 0) {
            throw new InvalidQuantityException("Quantity must be at least 1.");
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

    /* Sets an exact quantity for the item at this row. Checks stock. */
    public void setQuantity(int index, int qty) throws OutOfStockException, InvalidQuantityException {
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("No item selected.");
        }
        if (qty <= 0) {
            throw new InvalidQuantityException("Quantity must be at least 1. Use REMOVE ITEM to delete it.");
        }
        OrderItem item = items.get(index);
        if (qty > item.getProduct().getStock()) {
            throw new OutOfStockException("Only " + item.getProduct().getStock()
                    + " left for " + item.getProduct().getName());
        }
        item.setQuantity(qty);
    }

    /* Re-checks every line against current stock. Call right before payment. */
    public void validateStock() throws OutOfStockException {
        for (OrderItem item : items) {
            Product p = item.getProduct();
            if (item.getQuantity() > p.getStock()) {
                throw new OutOfStockException("Only " + p.getStock() + " left for " + p.getName());
            }
        }
    }

    public void removeItem(int index) {
        if (index < 0 || index >= items.size()) return;
        items.remove(index);
        if (items.isEmpty()) discountApplied = false;   // empty order = no leftover discount
    }

    public void clear() {
        items.clear();
        discountApplied = false;
    }

    public boolean isDiscountApplied() { return discountApplied; }

    public void setDiscountApplied(boolean applied) {
        this.discountApplied = applied && !items.isEmpty();
    }

    // ---------- Money (every figure is rounded to centavos, so the lines always add up) ----------

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public double getSubtotal() {
        double sum = 0;
        for (OrderItem item : items) sum += item.getLineTotal();
        return round2(sum);
    }

    public double getDiscount() {
        return discountApplied ? round2(getSubtotal() * DISCOUNT_RATE) : 0;
    }

    public double getTax() {
        return round2((getSubtotal() - getDiscount()) * TAX_RATE);
    }

    public double getTotal() {
        return round2(getSubtotal() - getDiscount() + getTax());
    }
}