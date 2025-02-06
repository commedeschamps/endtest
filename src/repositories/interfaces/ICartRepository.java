package repositories.interfaces;

import models.Cart;
import models.OrderItem;

public interface ICartRepository {
    boolean addItemToCart(int userId, int productId, int quantity, double price);
    Cart getCartByUserId(int userId);
    boolean clearCart(int userId);
    boolean removeItemFromCart(int userId, int productId);
    boolean updateCartItemQuantity(int userId, int productId, int newQuantity);
}
