package controllers;

import controllers.interfaces.IUserController;
import models.User;
import repositories.interfaces.IUserRepository;

import java.util.List;

public class UserController implements IUserController {
    private final IUserRepository repo;

    public UserController(IUserRepository repo) {
        this.repo = repo;
    }

    @Override
    public String createUser(String name, String surname, String gender) {
        if (name == null || name.isEmpty() || surname == null || surname.isEmpty() || gender == null || gender.isEmpty()) {
            return "Invalid input. Please provide valid name, surname, and gender.";
        }

        boolean male = gender.equalsIgnoreCase("male");
        User user = new User(name, surname, male);
        boolean created = repo.createUser(user);

        return created ? "User was created successfully." : "User creation failed. Please try again.";
    }

    @Override
    public String getUserById(int id) {
        if (id <= 0) {
            return "Invalid user ID.";
        }

        User user = repo.getUserById(id);
        return user == null ? "User not found." : user.toString();
    }

    @Override
    public String getAllUsers() {
        List<User> users = repo.getAllUsers();
        if (users == null || users.isEmpty()) {
            return "No users available.";
        }

        StringBuilder response = new StringBuilder();
        for (User user : users) {
            response.append(user.toString()).append("\n");
        }
        return response.toString();
    }
}
