import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class DeliveryClient extends JFrame {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JTextArea outputArea;

    public DeliveryClient() {
        setTitle("Delivery Order Tracking System");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JButton addButton = new JButton("Add Order");
        JButton viewButton = new JButton("View Orders");
        JButton updateButton = new JButton("Update Status");

        JPanel panel = new JPanel();
        panel.add(addButton);
        panel.add(viewButton);
        panel.add(updateButton);
        add(panel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        connectToServer();

        addButton.addActionListener(e -> addOrder());
        viewButton.addActionListener(e -> viewOrders());
        updateButton.addActionListener(e -> updateStatus());
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 6000);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Server not available!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addOrder() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField productField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField statusField = new JTextField("Pending");

        Object[] message = {
            "Order ID:", idField,
            "Customer Name:", nameField,
            "Product Name:", productField,
            "Quantity:", qtyField,
            "Price:", priceField,
            "Status:", statusField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Order", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Order order = new Order(
                    Integer.parseInt(idField.getText()),
                    nameField.getText(),
                    productField.getText(),
                    Integer.parseInt(qtyField.getText()),
                    Double.parseDouble(priceField.getText()),
                    statusField.getText()
                );
                out.writeObject(Protocol.ADD_ORDER);
                out.writeObject(order);
                out.flush();
                outputArea.setText((String) in.readObject());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding order!");
            }
        }
    }

    private void viewOrders() {
        try {
            out.writeObject(Protocol.VIEW_ORDERS);
            out.flush();
            outputArea.setText((String) in.readObject());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error viewing orders!");
        }
    }

    private void updateStatus() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Order ID:"));
            String status = JOptionPane.showInputDialog(this, "Enter New Status:");
            out.writeObject(Protocol.UPDATE_STATUS);
            out.writeObject(id);
            out.writeObject(status);
            out.flush();
            outputArea.setText((String) in.readObject());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating status!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DeliveryClient().setVisible(true));
    }
}
