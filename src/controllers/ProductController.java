package controllers;

import controllers.interfaces.IProductController;
import models.Product;
import repositories.interfaces.IProductRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ProductController implements IProductController {
    private final IProductRepository repo;

    public ProductController(IProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public String createProduct(String name, String description, double price, int quantity, String category) {
        if (name == null || name.isEmpty() || description == null || description.isEmpty() || price <= 0 || quantity < 0 || category == null || category.isEmpty()) {
            return "Invalid input values. Please check the data and try again.";
        }

        Product product = new Product(0, name, description, price, quantity, category); // ✅ Fix: Provide default ID (0)
        boolean created = repo.createProduct(product);

        return created ? "Product was created successfully." : "Product creation failed. Please try again.";
    }


    @Override
    public String getProductById(int id) {
        if (id <= 0) {
            return "Invalid product ID.";
        }

        Product product = repo.getProductById(id);
        return (product == null) ? "Product not found." : product.toString();
    }

    @Override
    public String getAllProducts() {
        List<Product> products = repo.getAllProducts();
        if (products == null || products.isEmpty()) {
            return "No products available.";
        }

        return products.stream()
                .map(Product::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getProductsByCategory(String category) {
        if (category == null || category.isEmpty()) {
            return "Invalid category input.";
        }

        List<Product> products = repo.getAllProducts().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        if (products.isEmpty()) {
            return "No products found in this category.";
        }

        return products.stream()
                .map(Product::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String getProductsByPriceRange(double minPrice, double maxPrice) {
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return "Invalid price range.";
        }

        List<Product> products = repo.getAllProducts().stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());

        if (products.isEmpty()) {
            return "No products found in this price range.";
        }

        return products.stream()
                .map(Product::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String sortProductsByPriceAscending() {
        List<Product> products = repo.getAllProducts().stream()
                .sorted((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()))
                .collect(Collectors.toList());

        return products.isEmpty() ? "No products available." : products.stream()
                .map(Product::toString)
                .collect(Collectors.joining("\n"));
    }

    @Override
    public String sortProductsByPriceDescending() {
        List<Product> products = repo.getAllProducts().stream()
                .sorted((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()))
                .collect(Collectors.toList());

        return products.isEmpty() ? "No products available." : products.stream()
                .map(Product::toString)
                .collect(Collectors.joining("\n"));
    }
}
