package models;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import data.interfaceces.IDB;


public class Cart {
    private int userId;
    private List<OrderItem> items;
    private IDB db;

    public Cart(IDB db) {
        this.db = db;
    }

    public Cart() {
        items = new ArrayList<>();
    }

    public Cart(int userId) {
        this();
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        if (userId > 0) {
            this.userId = userId;
        } else {
            throw new IllegalArgumentException("User ID must be positive.");
        }
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        if (items != null) {
            this.items = items;
        } else {
            throw new IllegalArgumentException("Items list cannot be null.");
        }
    }

    public void addItem(OrderItem item) {
        if (item != null) {
            items.add(item);
        } else {
            throw new IllegalArgumentException("Cannot add null item.");
        }
    }

    public void removeItem(OrderItem item) {
        if (item != null && items.contains(item)) {
            items.remove(item);
        } else {
            throw new IllegalArgumentException("Item does not exist in the cart.");
        }
    }

    public void clear() {
        items.clear();
    }

    public double getTotalPrice() {
        double totalPrice = 0.0;
        try (Connection connection = db.getConnection()) {  
            for (OrderItem item : items) {  
                double productPrice = getProductPriceById(item.getProductId(), connection);
                totalPrice += productPrice * item.getQuantity();
            }
        } catch (SQLException e) {
            System.out.println("Error calculating total price: " + e.getMessage());
        }
        return totalPrice;
    }

    private double getProductPriceById(int productId, Connection connection) {
        double productPrice = 0.0;
        String query = "SELECT price FROM products WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    productPrice = rs.getDouble("price");
                } else {
                    System.out.println("Product not found with ID: " + productId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving product price: " + e.getMessage());
        }
        return productPrice;
    }


    @Override
    public String toString() {
        return "Cart{" +
                "userId=" + userId +
                ", items=" + items +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}
