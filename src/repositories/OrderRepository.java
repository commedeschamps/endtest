package repositories;

import data.interfaceces.IDB;
import models.Order;
import models.OrderItem;
import repositories.interfaces.IOrderRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository implements IOrderRepository {
    private final IDB db;

    public OrderRepository(IDB db) {
        this.db = db;
    }

    @Override
    public boolean createOrder(Order order) {
        String sql = "INSERT INTO orders(user_id, order_date, total_amount, status, delivery_method, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = db.getConnection();
             PreparedStatement st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setInt(1, order.getUserId());
            st.setTimestamp(2, new Timestamp(order.getOrderDate().getTime()));
            st.setDouble(3, order.getTotalAmount());
            st.setString(4, order.getStatus());
            st.setString(5, order.getDeliveryMethod());
            st.setString(6, order.getPaymentMethod());

            int affectedRows = st.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        int orderId = rs.getInt(1);
                        for (OrderItem item : order.getOrderItems()) {
                            addOrderItem(orderId, item);
                        }

                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL error while creating order: " + e.getMessage());
        }
        return false;
    }

    private void addOrderItem(int orderId, OrderItem item) {
        String sql = "INSERT INTO order_items(order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection connection = db.getConnection();
             PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, orderId);
            st.setInt(2, item.getProductId());
            st.setInt(3, item.getQuantity());
            st.setDouble(4, item.getPrice());
            st.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL error while adding order item: " + e.getMessage());
        }
    }


    @Override
    public Order getOrderById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection connection = db.getConnection();
             PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    java.sql.Date sqlDate = rs.getDate("order_date");
                    java.util.Date orderDate = new java.util.Date(sqlDate.getTime());

                    return new Order(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            orderDate,
                            rs.getDouble("total_amount")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL error while retrieving order by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM orders";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = db.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                java.sql.Date sqlDate = rs.getDate("order_date");
                java.util.Date orderDate = new java.util.Date(sqlDate.getTime());

                orders.add(new Order(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        orderDate,
                        rs.getDouble("total_amount")
                ));
            }
            return orders;
        } catch (SQLException e) {
            System.out.println("SQL error while retrieving all orders: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = db.getConnection();
             PreparedStatement st = connection.prepareStatement(sql)) {

            st.setInt(1, userId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date sqlDate = rs.getDate("order_date");
                    java.util.Date orderDate = new java.util.Date(sqlDate.getTime());

                    orders.add(new Order(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            orderDate,
                            rs.getDouble("total_amount")
                    ));
                }
            }
            return orders;
        } catch (SQLException e) {
            System.out.println("SQL error while retrieving orders by user ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String getOrderDetailsById(int orderId) {
        String sql = """
        SELECT
            o.id AS order_id,
            o.order_date,
            o.status,
            o.delivery_method,
            o.payment_method,
            o.total_amount,
            u.name AS user_name,
            u.email AS user_email,
            oi.product_id,
            p.name AS product_name,
            p.price AS product_price,
            oi.quantity,
            oi.price AS item_price
        FROM orders o
        JOIN users u ON o.user_id = u.id
        JOIN order_items oi ON o.id = oi.order_id
        JOIN products p ON oi.product_id = p.id
        WHERE o.id = ?;
        """;

        StringBuilder result = new StringBuilder();
        try (Connection connection = db.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            boolean orderFound = false;

            while (rs.next()) {
                if (!orderFound) {
                    result.append("\nOrder Details:\n")
                            .append("Order ID: ").append(rs.getInt("order_id")).append("\n")
                            .append("Order Date: ").append(rs.getTimestamp("order_date")).append("\n")
                            .append("Status: ").append(rs.getString("status")).append("\n")
                            .append("Delivery Method: ").append(rs.getString("delivery_method")).append("\n")
                            .append("Payment Method: ").append(rs.getString("payment_method")).append("\n")
                            .append("Total Amount: ").append(rs.getDouble("total_amount")).append("\n")
                            .append("Customer: ").append(rs.getString("user_name")).append(" (").append(rs.getString("user_email")).append(")\n")
                            .append("Products:\n");
                    orderFound = true;
                }
                result.append("- Product ID: ").append(rs.getInt("product_id"))
                        .append(", Name: ").append(rs.getString("product_name"))
                        .append(", Price: ").append(rs.getDouble("product_price"))
                        .append(", Quantity: ").append(rs.getInt("quantity"))
                        .append(", Total Item Price: ").append(rs.getDouble("item_price"))
                        .append("\n");
            }

            if (!orderFound) {
                return "Order with ID " + orderId + " not found.";
            }

            return result.toString();
        } catch (SQLException e) {
            return "Error retrieving order details: " + e.getMessage();
        }
    }

}
