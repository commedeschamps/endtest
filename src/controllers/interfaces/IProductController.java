package controllers.interfaces;

public interface IProductController {

    String createProduct(String name, String description, double price, int quantity, String category);
    String getProductById(int id);
    String getAllProducts();
    String getProductsByCategory(String category);
    String getProductsByPriceRange(double minPrice, double maxPrice);
    String sortProductsByPriceAscending();
    String sortProductsByPriceDescending();
}
