import java.io.*;
import java.net.*;
import java.sql.*;

public class DeliveryServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(6000)) {
            System.out.println("Server running... Waiting for clients.");
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            while (true) {
                String command = (String) in.readObject();
                if (Protocol.ADD_ORDER.equals(command)) {
                    Order order = (Order) in.readObject();
                    addOrderToDB(order);
                    out.writeObject("Order added successfully!");
                } else if (Protocol.VIEW_ORDERS.equals(command)) {
                    out.writeObject(viewOrders());
                } else if (Protocol.UPDATE_STATUS.equals(command)) {
                    int orderId = (int) in.readObject();
                    String status = (String) in.readObject();
                    updateOrderStatus(orderId, status);
                    out.writeObject("Order status updated!");
                } else if (Protocol.EXIT.equals(command)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addOrderToDB(Order order) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO orders VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, order.getOrderId());
            ps.setString(2, order.getCustomerName());
            ps.setString(3, order.getProductName());
            ps.setInt(4, order.getQuantity());
            ps.setDouble(5, order.getPrice());
            ps.setString(6, order.getStatus());
            ps.executeUpdate();
        }
    }

    private String viewOrders() throws SQLException {
        StringBuilder sb = new StringBuilder();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM orders")) {
            while (rs.next()) {
                sb.append(String.format("OrderID: %d | Customer: %s | Product: %s | Qty: %d | Price: %.2f | Status: %s\n",
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getInt(4), rs.getDouble(5), rs.getString(6)));
            }
        }
        return sb.length() == 0 ? "No orders found!" : sb.toString();
    }

    private void updateOrderStatus(int orderId, String status) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE orders SET status = ? WHERE orderId = ?")) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }
}
