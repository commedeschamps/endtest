import controllers.*;
import controllers.interfaces.*;
import data.PostgresDB;
import data.interfaceces.IDB;
import repositories.*;
import repositories.interfaces.*;
import services.OrderService;
import strategy.DiscountPricing;
import strategy.StandardPricing;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        IDB db = PostgresDB.getInstance();
        IUserRepository userRepository = new UserRepository(db);
        IProductRepository productRepository = new ProductRepository(db);
        IOrderRepository orderRepository = new OrderRepository(db);
        ICartRepository cartRepository = new CartRepository(db);

        IUserController userController = new UserController(userRepository);
        IProductController productController = new ProductController(productRepository);
        IOrderController orderController = new OrderController(orderRepository);
        ICartController cartController = new CartController(cartRepository);

        OrderService orderService = new OrderService(new StandardPricing());

        orderService.setPricingStrategy(new DiscountPricing());
        ShopApplication app = new ShopApplication(
                userController,
                productController,
                orderController,
                cartController
        );

        app.start();

        db.close();
    }
}
