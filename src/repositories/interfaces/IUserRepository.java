package repositories.interfaces;

import models.User;
import java.util.List;

public interface IUserRepository {
    boolean createUser(User user);
    User getUserById(int id);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    boolean updateUserBalance(int userId, double newBalance);
    boolean verifyUserCredentials(String email, String password);
}
