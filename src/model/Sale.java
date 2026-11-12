package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * A completed sale, kept in memory for the receipt and the daily sales report.
 * It stores its own copy of every line (name and price at the time of sale),
 * so later changes to a product never alter an old receipt.
 */
public class Sale {

    /* One line of the receipt. */
    public static class Line {
        private final String productId;
        private final String name;
        private final double unitPrice;
        private final int quantity;

        public Line(String productId, String name, double unitPrice, int quantity) {
            this.productId = productId;
            this.name = name;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        public String getProductId() { return productId; }
        public String getName() { return name; }
        public double getUnitPrice() { return unitPrice; }
        public int getQuantity() { return quantity; }
        public double getLineTotal() { return round2(unitPrice * quantity); }
    }

    private final String receiptNo;
    private final LocalDateTime dateTime;
    private final String cashierName;
    private final List<Line> lines = new ArrayList<>();
    private final boolean discountApplied;
    private final double subtotal;
    private final double discount;
    private final double tax;
    private final double total;
    private final double tendered;
    private final double change;

    public Sale(String receiptNo, String cashierName, Order order, double tendered) {
        this.receiptNo = receiptNo;
        this.dateTime = LocalDateTime.now();
        this.cashierName = cashierName;
        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();
            lines.add(new Line(p.getId(), p.getName(), p.getPrice(), item.getQuantity()));
        }
        this.discountApplied = order.isDiscountApplied();
        this.subtotal = order.getSubtotal();
        this.discount = order.getDiscount();
        this.tax = order.getTax();
        this.total = order.getTotal();
        this.tendered = round2(tendered);
        this.change = round2(this.tendered - this.total);
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public String getReceiptNo() { return receiptNo; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getCashierName() { return cashierName; }
    public List<Line> getLines() { return Collections.unmodifiableList(lines); }
    public boolean isDiscountApplied() { return discountApplied; }
    public double getSubtotal() { return subtotal; }
    public double getDiscount() { return discount; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    public double getTendered() { return tendered; }
    public double getChange() { return change; }

    public int getItemCount() {
        int n = 0;
        for (Line l : lines) n += l.getQuantity();
        return n;
    }
}
