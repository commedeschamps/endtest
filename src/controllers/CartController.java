package controllers;

import controllers.interfaces.ICartController;
import models.Cart;
import repositories.interfaces.ICartRepository;  // Assuming you have a repository for Cart

public class CartController implements ICartController {
    private final ICartRepository cartRepository;

    public CartController(ICartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public String addItemToCart(int userId, int productId, int quantity, double price) {
        return "Item added to cart";
    }

    @Override
    public String removeItemFromCart(int userId, int productId) {
        return "Item removed from cart";
    }

    @Override
    public String getCartByUserId(int userId) {
        return "Cart details";
    }

    @Override
    public String clearCart(int userId) {
        return "Cart cleared";
    }

    @Override
    public String updateItemQuantity(int userId, int productId, int newQuantity) {
        return "Item quantity updated";
    }

    @Override
    public double calculateTotalPrice(int userId) {
        Cart cart = cartRepository.getCartByUserId(userId);

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for user: " + userId);
        }

        return cart.getTotalPrice();
    }
}
