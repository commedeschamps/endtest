package repositories;

import data.interfaceces.IDB;
import models.Cart;
import models.OrderItem;
import repositories.interfaces.ICartRepository;

import java.sql.*;

public class CartRepository implements ICartRepository {
    private final IDB db;

    public CartRepository(IDB db) {
        this.db = db;
    }

    @Override
    public boolean addItemToCart(int userId, int productId, int quantity, double price) {
        Connection connection = null;
        try {
            connection = db.getConnection();
            String checkSql = "SELECT * FROM cart WHERE user_id = ? AND product_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, productId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String updateSql = "UPDATE cart SET quantity = quantity + ?, price = ? WHERE user_id = ? AND product_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setInt(1, quantity);
                updateStmt.setDouble(2, price);
                updateStmt.setInt(3, userId);
                updateStmt.setInt(4, productId);
                updateStmt.executeUpdate();
            } else {
                String insertSql = "INSERT INTO cart(user_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, productId);
                insertStmt.setInt(3, quantity);
                insertStmt.setDouble(4, price);
                insertStmt.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Cart getCartByUserId(int userId) {
        Connection connection = null;
        try {
            connection = db.getConnection();
            String sql = "SELECT * FROM cart WHERE user_id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, userId);

            ResultSet rs = st.executeQuery();
            Cart cart = new Cart(userId);
            while (rs.next()) {
                OrderItem item = new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                );
                cart.addItem(item);
            }
            return cart;
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean clearCart(int userId) {
        Connection connection = null;
        try {
            connection = db.getConnection();
            String sql = "DELETE FROM cart WHERE user_id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            st.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateCartItemQuantity(int userId, int productId, int quantity) {
        Connection connection = null;
        try {
            connection = db.getConnection();
            String checkSql = "SELECT * FROM cart WHERE user_id = ? AND product_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, productId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String updateSql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setInt(1, quantity);
                updateStmt.setInt(2, userId);
                updateStmt.setInt(3, productId);
                int affectedRows = updateStmt.executeUpdate();
                return affectedRows > 0; 
            } else {
                System.out.println("Item not found in the cart.");
            }
        } catch (SQLException e) {
            System.out.println("SQL error while updating cart item: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean removeItemFromCart(int userId, int productId) {
        Connection connection = null;
        try {
            connection = db.getConnection();
            String checkSql = "SELECT * FROM cart WHERE user_id = ? AND product_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, productId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String deleteSql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
                PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
                deleteStmt.setInt(1, userId);
                deleteStmt.setInt(2, productId);
                int affectedRows = deleteStmt.executeUpdate();
                return affectedRows > 0;
            } else {
                System.out.println("Item not found in the cart.");
            }
        } catch (SQLException e) {
            System.out.println("SQL error while removing cart item: " + e.getMessage());
        }
        return false;
    }
}
