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
     * Adds an item and takes it out of the product's stock right away.
     * If the product is already in the order, the quantities are combined.
     * p.getStock() is what is still free to add, so the check is simply qty <= stock.
     */
    public void addItem(Product p, int qty) throws OutOfStockException, InvalidQuantityException {
        if (qty <= 0) {
            throw new InvalidQuantityException("Quantity must be at least 1.");
        }
        if (qty > p.getStock()) {
            throw new OutOfStockException("Only " + p.getStock() + " left for " + p.getName());
        }
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(p.getId())) {
                item.setQuantity(item.getQuantity() + qty);
                p.setStock(p.getStock() - qty);
                return;
            }
        }
        items.add(new OrderItem(p, qty));
        p.setStock(p.getStock() - qty);
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
        Product p = item.getProduct();
        int extra = qty - item.getQuantity();      // more (+) or fewer (-) than before
        if (extra > p.getStock()) {
            throw new OutOfStockException("Only " + (p.getStock() + item.getQuantity())
                    + " available for " + p.getName());
        }
        p.setStock(p.getStock() - extra);          // fewer in the order gives stock back
        item.setQuantity(qty);
    }

    /*
     * Stock is already taken out when an item is added, so every line is covered.
     * This only guards against a stock figure that went below zero.
     */
    public void validateStock() throws OutOfStockException {
        for (OrderItem item : items) {
            Product p = item.getProduct();
            if (p.getStock() < 0) {
                throw new OutOfStockException("Not enough stock for " + p.getName());
            }
        }
    }

    /* Removes one line and gives its quantity back to the stock. */
    public void removeItem(int index) {
        if (index < 0 || index >= items.size()) return;
        OrderItem item = items.remove(index);
        Product p = item.getProduct();
        p.setStock(p.getStock() + item.getQuantity());
        if (items.isEmpty()) discountApplied = false;   // empty order = no leftover discount
    }

    /* Empties the order WITHOUT giving stock back. Use only after a completed sale. */
    public void clear() {
        items.clear();
        discountApplied = false;
    }

    /* Cancels the order: gives every item's quantity back to the stock, then empties it. */
    public void releaseAll() {
        for (OrderItem item : items) {
            Product p = item.getProduct();
            p.setStock(p.getStock() + item.getQuantity());
        }
        clear();
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