package factories;

import models.User;

public class UserFactory implements Factory<User> {
    @Override
    public User create(Object... args) {
        if (args.length < 7) {
            throw new IllegalArgumentException("Invalid arguments for User creation");
        }

        Integer id = (Integer) args[0];
        String name = (String) args[1];
        String surname = (String) args[2];
        Boolean gender = (Boolean) args[3];
        String email = (String) args[4];
        String password = (String) args[5];
        Double balance = (Double) args[6];

        return new User(id, name, surname, gender, email, password, balance);
    }
}
