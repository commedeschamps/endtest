import controllers.interfaces.*;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class ShopApplication {
    private final IUserController userController;
    private final IProductController productController;
    private final IOrderController orderController;
    private final ICartController cartController;
    private final Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
    private final Connection connection;

    public ShopApplication(IUserController userController, IProductController productController,
                           IOrderController orderController, ICartController cartController) throws SQLException {
        this.userController = userController;
        this.productController = productController;
        this.orderController = orderController;
        this.cartController = cartController;
        this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/electronics_shop", "postgres", "2903");
    }

    public void start() {
        while (loggedInUserRole == null) {
            System.out.println("\n--- Welcome to the Electronics Store ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine();
                switch (option) {
                    case 1 -> createUser();
                    case 2 -> loginUser();
                    case 0 -> exitApplication();
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }

        while (true) {
            mainMenu();
            try {
                int option = scanner.nextInt();
                scanner.nextLine();
                if ("admin".equals(loggedInUserRole)) {
                    switch (option) {
                        case 1 -> addProduct();
                        case 2 -> deleteProduct();
                        case 3 -> updateProduct();
                        case 4 -> viewOrderDetails(); // New method
                        case 0 -> exitApplication();
                        default -> System.out.println("Invalid option. Please try again.");
                    }
                } else {
                    //  для пользователя
                    switch (option) {
                        case 1 -> viewAllProducts();
                        case 2 -> viewProductDetails();
                        case 3 -> createOrder();
                        case 4 -> viewCart();
                        case 5 -> addToCart();
                        case 6 -> removeFromCart();
                        case 7 -> checkBalance();
                        case 8 -> filterByCategory();
                        case 9 -> filterByPriceRange();
                        case 10 -> sortProductsAscending();
                        case 11 -> sortProductsDescending();
                        case 12 -> topUpBalance();
                        case 0 -> exitApplication();
                        default -> System.out.println("Invalid option. Please try again.");
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
            System.out.println("----------------------------------------");
        }
    }

    private void updateProduct() {
        int productId = getIntInput("Enter Product ID to update: ");
        String name = getStringInput("Enter new product name (leave blank to skip): ");
        String description = getStringInput("Enter new product description (leave blank to skip): ");
        String priceInput = getStringInput("Enter new product price (leave blank to skip): ");
        String quantityInput = getStringInput("Enter new product quantity (leave blank to skip): ");
        String category = getStringInput("Enter new product category (leave blank to skip): ");

        try {
            StringBuilder query = new StringBuilder("UPDATE products SET ");
            boolean hasUpdates = false;

            if (!name.isEmpty()) {
                query.append("name = ?, ");
                hasUpdates = true;
            }
            if (!description.isEmpty()) {
                query.append("description = ?, ");
                hasUpdates = true;
            }
            if (!priceInput.isEmpty()) {
                query.append("price = ?, ");
                hasUpdates = true;
            }
            if (!quantityInput.isEmpty()) {
                query.append("quantity = ?, ");
                hasUpdates = true;
            }
            if (!category.isEmpty()) {
                query.append("category = ?, ");
                hasUpdates = true;
            }

            if (!hasUpdates) {
                System.out.println("No updates provided.");
                return;
            }

            // Убираем последнюю запятую и пробел
            query.delete(query.length() - 2, query.length());
            query.append(" WHERE id = ?");

            PreparedStatement stmt = connection.prepareStatement(query.toString());
            int paramIndex = 1;

            if (!name.isEmpty()) {
                stmt.setString(paramIndex++, name);
            }
            if (!description.isEmpty()) {
                stmt.setString(paramIndex++, description);
            }
            if (!priceInput.isEmpty()) {
                stmt.setDouble(paramIndex++, Double.parseDouble(priceInput));
            }
            if (!quantityInput.isEmpty()) {
                stmt.setInt(paramIndex++, Integer.parseInt(quantityInput));
            }
            if (!category.isEmpty()) {
                stmt.setString(paramIndex++, category);
            }

            stmt.setInt(paramIndex, productId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product updated successfully.");
            } else {
                System.out.println("Product not found or no changes made.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for price or quantity.");
        }
    }


    private void mainMenu() {
        System.out.println("\n--- Welcome to the Electronics Store ---");
        if ("admin".equals(loggedInUserRole)) {
            System.out.println("1. Add New Product");
            System.out.println("2. Delete Product");
            System.out.println("3. Update Product");
            System.out.println("4. View Full Order Details by Order ID"); // New option
            System.out.println("0. Exit");
        } else {
            // Меню для пользователя
            System.out.println("1. View All Products");
            System.out.println("2. View Product Details");
            System.out.println("3. Create Order");
            System.out.println("4. View Cart");
            System.out.println("5. Add to Cart");
            System.out.println("6. Remove from Cart");
            System.out.println("7. Check Balance");
            System.out.println("8. Filter Products by Category");
            System.out.println("9. Filter Products by Price Range");
            System.out.println("10. Sort Products by Price (Ascending)");
            System.out.println("11. Sort Products by Price (Descending)");
            System.out.println("12. Top Up Balance");
            System.out.println("0. Exit");
        }
        System.out.print("Select an option: ");
    }

    private void viewAllProducts() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM products");
            System.out.println("All Products:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Price: " + rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
    }

    private void viewProductDetails() {
        int productId = getIntInput("Enter Product ID to view details: ");
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM products WHERE id = ?")) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Product ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Price: " + rs.getDouble("price"));
                System.out.println("Category: " + rs.getString("category"));
            } else {
                System.out.println("Product not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching product details: " + e.getMessage());
        }
    }

    private void createUser() {
        String name = getStringInput("Enter name: ");
        String email = getStringInput("Enter email: ");
        String password = getStringInput("Enter password: ");
        double balance = getDoubleInput("Enter balance: ");
        String role = getStringInput("Enter role (admin/user): ");

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO users (name, email, password, balance, role) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setDouble(4, balance);
            stmt.setString(5, role);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User created successfully.");
                loggedInUserRole = role;
            } else {
                System.out.println("Error creating user.");
            }
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    private String loggedInUserRole = null;

    private void loginUser() {
        String email = getStringInput("Enter email: ");
        String password = getStringInput("Enter password: ");
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?")) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                loggedInUserRole = rs.getString("role");
                System.out.println("Login successful. Welcome, " + rs.getString("name") + " (" + loggedInUserRole + ")");
            } else {
                System.out.println("Invalid email or password.");
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private void createOrder() {
        int productId = getIntInput("Enter Product ID: ");
        int quantity = getIntInput("Enter Quantity: ");
        int userId = getIntInput("Enter User ID: ");

        double productPrice = 0;
        try (PreparedStatement priceStmt = connection.prepareStatement("SELECT price FROM products WHERE id = ?")) {
            priceStmt.setInt(1, productId);
            ResultSet rs = priceStmt.executeQuery();
            if (rs.next()) {
                productPrice = rs.getDouble("price");
            } else {
                System.out.println("Product with ID " + productId + " not found.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching product price: " + e.getMessage());
            return;
        }

        double totalPrice = productPrice * quantity;

        Timestamp orderDate = new Timestamp(System.currentTimeMillis());

        String deliveryMethod = getStringInput("Enter delivery method (Pickup, Intercity delivery, delivery in the city): ");
        String paymentMethod = getStringInput("Enter payment method (Cash, Credit Card, PayPal): ");

        String status = "Pending";

        String issueOrder = getStringInput("Issue it? (Yes/No): ");

        if (issueOrder.equalsIgnoreCase("Yes")) {
            double userBalance = 0;
            try (PreparedStatement balanceStmt = connection.prepareStatement("SELECT balance FROM users WHERE id = ?")) {
                balanceStmt.setInt(1, userId);
                ResultSet rs = balanceStmt.executeQuery();
                if (rs.next()) {
                    userBalance = rs.getDouble("balance");
                } else {
                    System.out.println("User not found.");
                    return;
                }
            } catch (SQLException e) {
                System.out.println("Error checking balance: " + e.getMessage());
                return;
            }

            if (userBalance >= totalPrice) {
                try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO orders (user_id, product_id, quantity, total_amount, order_date, status, delivery_method, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    stmt.setInt(1, userId);
                    stmt.setInt(2, productId);
                    stmt.setInt(3, quantity);
                    stmt.setDouble(4, totalPrice);
                    stmt.setTimestamp(5, orderDate);
                    stmt.setString(6, status);
                    stmt.setString(7, deliveryMethod);
                    stmt.setString(8, paymentMethod);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Order created successfully. Total Price: " + totalPrice);

                        try (PreparedStatement updateBalanceStmt = connection.prepareStatement("UPDATE users SET balance = balance - ? WHERE id = ?")) {
                            updateBalanceStmt.setDouble(1, totalPrice);
                            updateBalanceStmt.setInt(2, userId);
                            int balanceUpdated = updateBalanceStmt.executeUpdate();
                            if (balanceUpdated > 0) {
                                System.out.println("Amount successfully deducted from balance.");
                            } else {
                                System.out.println("Error updating balance.");
                            }
                        } catch (SQLException e) {
                            System.out.println("Error updating balance: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Error creating order.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error creating order: " + e.getMessage());
                }
            } else {
                System.out.println("Insufficient balance. Order cannot be created.");
            }
        } else {
            System.out.println("Order creation canceled.");
        }
    }






    private void viewCart() {
        int userId = getIntInput("Enter User ID to view cart: ");
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM cart WHERE user_id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            double totalCost = 0;

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                int quantity = rs.getInt("quantity");

                try (PreparedStatement priceStmt = connection.prepareStatement("SELECT price FROM products WHERE id = ?")) {
                    priceStmt.setInt(1, productId);
                    ResultSet productRs = priceStmt.executeQuery();
                    if (productRs.next()) {
                        double price = productRs.getDouble("price");
                        double itemCost = price * quantity;
                        System.out.println("Product ID: " + productId + ", Quantity: " + quantity + ", Price: " + price + ", Total: " + itemCost);
                        totalCost += itemCost;
                    } else {
                        System.out.println("Product with ID " + productId + " not found in products table.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error fetching product price: " + e.getMessage());
                }
            }

            System.out.println("Total Cart Value: " + totalCost);
        } catch (SQLException e) {
            System.out.println("Error fetching cart: " + e.getMessage());
        }
    }


    private void addToCart() {
        int userId = getIntInput("Enter User ID: ");
        int productId = getIntInput("Enter Product ID: ");
        int quantity = getIntInput("Enter Quantity: ");
        double price = getDoubleInput("Enter Price: ");
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO cart (user_id, product_id, quantity, price) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Item added to cart.");
            } else {
                System.out.println("Error adding item to cart.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding item to cart: " + e.getMessage());
        }
    }

    private void removeFromCart() {
        int userId = getIntInput("Enter User ID: ");
        int productId = getIntInput("Enter Product ID to remove: ");
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM cart WHERE user_id = ? AND product_id = ?")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Item removed from cart.");
            } else {
                System.out.println("Error removing item from cart.");
            }
        } catch (SQLException e) {
            System.out.println("Error removing item from cart: " + e.getMessage());
        }
    }

    private void checkBalance() {
        int userId = getIntInput("Enter User ID to check balance: ");
        try (PreparedStatement stmt = connection.prepareStatement("SELECT balance FROM users WHERE id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("User balance: " + rs.getDouble("balance"));
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error checking balance: " + e.getMessage());
        }
    }

    private void addProduct() {
        String name = getStringInput("Enter product name: ");
        String description = getStringInput("Enter product description: ");
        double price = getDoubleInput("Enter product price: ");
        int quantity = getIntInput("Enter product quantity: ");
        String category = getStringInput("Enter product category: ");
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO products (name, description, price, quantity, category) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.setString(5, category);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product added successfully.");
            } else {
                System.out.println("Error adding product.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding product: " + e.getMessage());
        }
    }

    private void filterByCategory() {
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM products WHERE category = ?")) {
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Price: " + rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("Error filtering products by category: " + e.getMessage());
        }
    }

    private void filterByPriceRange() {
        double minPrice = getDoubleInput("Enter minimum price: ");
        double maxPrice = getDoubleInput("Enter maximum price: ");
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM products WHERE price BETWEEN ? AND ?")) {
            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Price: " + rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("Error filtering products by price range: " + e.getMessage());
        }
    }

    private void sortProductsAscending() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM products ORDER BY price ASC");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Price: " + rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("Error sorting products ascending: " + e.getMessage());
        }
    }

    private void manageUsers() {
        if (!"admin".equals(loggedInUserRole)) {
            System.out.println("Access denied. Only admin can manage users.");
            return;
        }

        System.out.println("1. View All Users");
        System.out.println("2. Delete User");
        int option = getIntInput("Select an option: ");

        switch (option) {
            case 1 -> viewAllUsers();
            case 2 -> deleteUser();
            default -> System.out.println("Invalid option.");
        }
    }

    private void viewAllUsers() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            System.out.println("All Users:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Email: " + rs.getString("email") + ", Role: " + rs.getString("role"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
    }

    private void deleteUser() {
        int userId = getIntInput("Enter User ID to delete: ");
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("Error deleting user.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    private void manageProducts() {
        if (!"admin".equals(loggedInUserRole)) {
            System.out.println("Access denied. Only admin can manage products.");
            return;
        }

        System.out.println("1. View All Products");
        System.out.println("2. Delete Product");
        int option = getIntInput("Select an option: ");

        switch (option) {
            case 1 -> viewAllProducts();
            case 2 -> deleteProduct();
            default -> System.out.println("Invalid option.");
        }
    }

    private void deleteProduct() {
        int productId = getIntInput("Enter Product ID to delete: ");
        try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM products WHERE id = ?")) {
            stmt.setInt(1, productId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Product deleted successfully.");
            } else {
                System.out.println("Error deleting product.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }
    }

    private void sortProductsDescending() {
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM products ORDER BY price DESC");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Price: " + rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("Error sorting products descending: " + e.getMessage());
        }
    }

    private void topUpBalance() {
        int userId = getIntInput("Enter User ID to top up balance: ");
        double amount = getDoubleInput("Enter amount to add: ");
        try (PreparedStatement stmt = connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE id = ?")) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, userId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Balance updated successfully.");
            } else {
                System.out.println("Error updating balance.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
            System.out.print(prompt);
        }
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next();
            System.out.print(prompt);
        }
        double input = scanner.nextDouble();
        scanner.nextLine();
        return input;
    }

    private void exitApplication() {
        System.out.println("Exiting application...");
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
        System.exit(0);
    }
    private void viewOrderDetails() {
        int orderId = getIntInput("Enter Order ID to view full details: ");
        String result = orderController.getOrderDetailsById(orderId);
        System.out.println(result);
    }

}
