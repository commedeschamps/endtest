package factories;

import models.Cart;
import data.interfaceces.IDB;

import java.util.List;

public class CartFactory implements Factory<Cart> {
    private IDB database;

    public CartFactory(IDB database) {
        this.database = database;
    }

    @Override
    public Cart create(Object... args) {
        if (args.length == 1 && args[0] instanceof Integer) {
            return new Cart((int) args[0]); // Matches constructor Cart(int)
        } else if (args.length == 0) {
            return new Cart(); // Matches constructor Cart()
        } else {
            throw new IllegalArgumentException("Invalid arguments for Cart creation");
        }
    }
}
