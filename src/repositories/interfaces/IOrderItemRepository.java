package repositories.interfaces;

import models.OrderItem;
import java.util.List;

public interface IOrderItemRepository {
    boolean createOrderItem(OrderItem orderItem);
    List<OrderItem> getItemsByOrderId(int orderId);
    OrderItem getOrderItemById(int id);
    List<OrderItem> getAllOrderItems();
    boolean removeOrderItem(int orderId, int productId);

    boolean deleteOrderItem(int id);
}
