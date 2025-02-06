package repositories.interfaces;

import models.Product;

import java.util.List;

public interface IProductRepository {
    boolean createProduct(Product product);
    Product getProductById(int id);
    List<Product> getAllProducts();
    boolean updateProduct(Product product);
    boolean deleteProduct(int id);
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByPriceRange(double minPrice, double maxPrice);
    List<Product> sortProductsByPriceAscending();
    List<Product> sortProductsByPriceDescending();
}
