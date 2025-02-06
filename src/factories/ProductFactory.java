package factories;

import models.Product;

public class ProductFactory implements Factory<Product> {
    @Override
    public Product create(Object... args) {
        if (args.length < 6) {
            throw new IllegalArgumentException("Invalid arguments for Product creation");
        }

        try {
            Integer id = Integer.parseInt(args[0].toString());
            String name = args[1].toString();
            String description = args[2].toString();
            Double price = Double.parseDouble(args[3].toString());
            Integer quantity = Integer.parseInt(args[4].toString());
            String category = args[5].toString();

            return new Product(id, name, description, price, quantity, category);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting arguments for Product creation: " + e.getMessage());
        }
    }
}
