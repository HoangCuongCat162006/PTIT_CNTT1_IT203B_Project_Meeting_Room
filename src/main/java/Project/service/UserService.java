package Project.service;

import Project.dao.UserDAO;
import Project.model.User;
import Project.util.PasswordHash;

import java.util.List;

public class UserService implements UserServiceInterface {
    private final UserDAO userDAO = new UserDAO();

    @Override
    public boolean registerEmployee(String username, String password, String email, String phone) {
        if (isBlank(username) || isBlank(password)) {
            return false;
        }

        if (userDAO.usernameExists(username)) {
            return false;
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(PasswordHash.hashPassword(password));
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole("Employee");

        return userDAO.addUser(user);
    }

    @Override
    public User login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return null;
        }

        User user = userDAO.getUserByUsername(username.trim());
        if (user == null) {
            return null;
        }

        if (PasswordHash.checkPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public boolean createSupportStaff(String username, String password, String email, String phone) {
        if (isBlank(username) || isBlank(password)) {
            return false;
        }

        if (userDAO.usernameExists(username)) {
            return false;
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(PasswordHash.hashPassword(password));
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole("Support Staff");

        return userDAO.addUser(user);
    }

    @Override
    public void ensureDefaultAdmin() {
        if (userDAO.usernameExists("admin")) {
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(PasswordHash.hashPassword("123456"));
        admin.setEmail("admin@gmail.com");
        admin.setPhone("0123456789");
        admin.setRole("Admin");

        userDAO.addUser(admin);
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}