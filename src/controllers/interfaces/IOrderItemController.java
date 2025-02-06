package controllers.interfaces;

public interface IOrderItemController {
    String createOrderItem(int orderId, int productId, int quantity, double price);
    String getOrderItemById(int id);
    String getAllOrderItems();
    double calculateOrderItemTotalPrice(int quantity, double price);
}
