package repositories;

import data.interfaceces.IDB;
import models.OrderItem;
import repositories.interfaces.IOrderItemRepository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemRepository implements IOrderItemRepository {
    private final IDB db;

    public OrderItemRepository(IDB db) {
        this.db = db;
    }

    @Override
    public boolean createOrderItem(OrderItem orderItem) {
        Connection connection = null;
        try {
            connection = db.getConnection();
            String sql = "INSERT INTO order_items(order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement st = connection.prepareStatement(sql);

            st.setInt(1, orderItem.getOrderId());
            st.setInt(2, orderItem.getProductId());
            st.setInt(3, orderItem.getQuantity());
            st.setDouble(4, orderItem.getPrice());

            st.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return false;
    }

    public List<OrderItem> getItemsByOrderId(int orderId) {
        Connection connection = null;
        try {
            connection = db.getConnection();
            String sql = "SELECT * FROM order_items WHERE order_id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, orderId);

            ResultSet rs = st.executeQuery();
            List<OrderItem> items = new ArrayList<>();
            while (rs.next()) {
                OrderItem item = new OrderItem(rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"));
                items.add(item);
            }
            return items;
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return null;
    }


    @Override
    public OrderItem getOrderItemById(int id) {
        Connection connection = null;
        try {
            connection = db.getConnection();
            String sql = "SELECT * FROM order_items WHERE id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, id);

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return new OrderItem(rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<OrderItem> getAllOrderItems() {
        Connection connection = null;
        try {
            connection = db.getConnection();
            String sql = "SELECT * FROM order_items";
            Statement st = connection.createStatement();

            ResultSet rs = st.executeQuery(sql);
            List<OrderItem> orderItems = new ArrayList<>();
            while (rs.next()) {
                OrderItem item = new OrderItem(rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"));
                orderItems.add(item);
            }
            return orderItems;
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean removeOrderItem(int orderId, int productId) {
        Connection connection = null;
        try {
            connection = db.getConnection();
            String sql = "DELETE FROM order_items WHERE order_id = ? AND product_id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, orderId);
            st.setInt(2, productId);

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteOrderItem(int id) {
        Connection connection = null;
        try {
            connection = db.getConnection();
            String sql = "DELETE FROM order_items WHERE id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, id);

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return false;
    }
}
