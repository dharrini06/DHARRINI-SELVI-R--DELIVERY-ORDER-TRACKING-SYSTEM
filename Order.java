public class Order implements java.io.Serializable {
    private int orderId;
    private String customerName;
    private String productName;
    private int quantity;
    private double price;
    private String status;

    public Order(int orderId, String customerName, String productName, int quantity, double price, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }

    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("OrderID: %d | Customer: %s | Product: %s | Qty: %d | Price: %.2f | Status: %s",
                orderId, customerName, productName, quantity, price, status);
    }
}
