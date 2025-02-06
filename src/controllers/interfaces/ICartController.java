package controllers.interfaces;

public interface ICartController {
    String addItemToCart(int userId, int productId, int quantity, double price);
    String removeItemFromCart(int userId, int productId);
    String getCartByUserId(int userId);
    String clearCart(int userId);
    String updateItemQuantity(int userId, int productId, int newQuantity);
    double calculateTotalPrice(int userId);
}
