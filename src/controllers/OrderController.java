package controllers;

import controllers.interfaces.IOrderController;
import models.Order;
import repositories.interfaces.IOrderRepository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderController implements IOrderController {
    private final IOrderRepository repo;

    public OrderController(IOrderRepository repo) {
        this.repo = repo;
    }

    @Override
    public String createOrder(int userId, double totalAmount) {
        LocalDateTime orderDateTime = LocalDateTime.now();
        Date orderDate = Date.valueOf(orderDateTime.toLocalDate());

        String shippingAddress = "123 Shipping St.";
        String billingAddress = "123 Billing St.";

        // Создание заказа
        Order newOrder = new Order(userId, totalAmount, orderDate, shippingAddress, billingAddress);
        boolean created = repo.createOrder(newOrder);
        return created ? "Order was created" : "Order creation failed";
    }

    @Override
    public String getOrderById(int id) {
        Order order = repo.getOrderById(id);
        return order == null ? "Order not found" : order.toString();
    }

    @Override
    public String getAllOrders() {
        // Получаем все заказы
        List<Order> orders = repo.getAllOrders();
        if (orders == null || orders.isEmpty()) {
            return "No orders available.";
        }
        return orders.stream()
                .map(Order::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getUserOrders(int userId) {
        // Получаем заказы по userId
        List<Order> orders = repo.getOrdersByUserId(userId);
        if (orders.isEmpty()) {
            return "No orders found for this user.";
        }

        return orders.stream()
                .map(Order::toString)
                .collect(Collectors.joining("\n"));
    }
    @Override
    public String getOrderDetailsById(int orderId) {
        return repo.getOrderDetailsById(orderId);
    }

}
