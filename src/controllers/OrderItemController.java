package controllers;

import controllers.interfaces.IOrderItemController;
import models.OrderItem;
import repositories.interfaces.IOrderItemRepository;

import java.util.List;

public class OrderItemController implements IOrderItemController {
    private final IOrderItemRepository repo;

    public OrderItemController(IOrderItemRepository repo) {
        this.repo = repo;
    }

    @Override
    public String createOrderItem(int orderId, int productId, int quantity, double price) {
        OrderItem orderItem = new OrderItem(orderId, productId, quantity, price);
        boolean created = repo.createOrderItem(orderItem);
        return (created) ? "OrderItem was created" : "OrderItem creation failed";
    }

    @Override
    public String getOrderItemById(int id) {
        OrderItem orderItem = repo.getOrderItemById(id);
        return (orderItem == null) ? "OrderItem not found" : orderItem.toString();
    }

    @Override
    public String getAllOrderItems() {
        List<OrderItem> orderItems = repo.getAllOrderItems();
        StringBuilder response = new StringBuilder();
        for (OrderItem item : orderItems) {
            response.append(item.toString()).append("\n");
        }
        return response.toString();
    }

    @Override
    public double calculateOrderItemTotalPrice(int quantity, double price) {
        return quantity * price;
    }

}
