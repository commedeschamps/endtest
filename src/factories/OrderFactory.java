package factories;

import models.Order;
import java.util.List;

public class OrderFactory implements Factory<Order> {
    @Override
    public Order create(Object... args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Invalid arguments for Order creation");
        }
        return new Order(
                (int) args[0],       // order id
                (int) args[1],       // user id
                (List<Integer>) args[2] // list of product IDs
        );
    }
}
