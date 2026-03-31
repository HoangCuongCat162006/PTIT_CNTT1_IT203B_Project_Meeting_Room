package Project.service;

import Project.dao.impl.UserDAO;
import Project.model.User;
import Project.util.PasswordHash;
import Project.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;

public class UserService implements UserServiceInterface {
    private final UserDAO userDAO = new UserDAO();

    @Override
    public boolean registerEmployee(String username, String password, String email, String phone) {
        try {
            return createUserByRole(username, password, email, phone, "Employee");
        } catch (Exception e) {
            System.out.println("Loi dang ky Employee: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User login(String username, String password) {
        try {
            if (ValidationUtil.isBlank(username) || ValidationUtil.isBlank(password)) {
                System.out.println("Username va password khong duoc de trong.");
                return null;
            }

            User user = userDAO.getUserByUsername(username.trim());
            if (user == null) {
                System.out.println("Khong tim thay tai khoan.");
                return null;
            }

            if (PasswordHash.checkPassword(password, user.getPassword())) {
                System.out.println("Dang nhap thanh cong.");
                return user;
            }

            System.out.println("Sai mat khau.");
            return null;

        } catch (Exception e) {
            System.out.println("Loi dang nhap: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean createSupportStaff(String username, String password, String email, String phone) {
        try {
            return createUserByRole(username, password, email, phone, "Support Staff");
        } catch (Exception e) {
            System.out.println("Loi tao Support Staff: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean createAdmin(String username, String password, String email, String phone) {
        try {
            return createUserByRole(username, password, email, phone, "Admin");
        } catch (Exception e) {
            System.out.println("Loi tao Admin: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void ensureDefaultAdmin() {
        try {
            if (userDAO.usernameExists("admin")) {
                return;
            }

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(PasswordHash.hashPassword("123456"));
            admin.setEmail("admin@gmail.com");
            admin.setPhone("0123456789");
            admin.setRole("Admin");

            boolean result = userDAO.addUser(admin);

            if (result) {
                System.out.println("Da tao tai khoan admin mac dinh.");
            } else {
                System.out.println("Khong the tao admin mac dinh.");
            }
        } catch (Exception e) {
            System.out.println("Loi tao admin mac dinh: " + e.getMessage());
        }
    }

    @Override
    public List<User> getAllUsers() {
        try {
            return userDAO.getAllUsers();
        } catch (Exception e) {
            System.out.println("Loi lay danh sach user: " + e.getMessage());
            return new ArrayList<User>();
        }
    }

    @Override
    public List<User> getUsersByRole(String role) {
        try {
            if (ValidationUtil.isBlank(role)) {
                return new ArrayList<User>();
            }
            return userDAO.getUsersByRole(role.trim());
        } catch (Exception e) {
            System.out.println("Loi lay user theo role: " + e.getMessage());
            return new ArrayList<User>();
        }
    }

    @Override
    public User getUserById(int userId) {
        try {
            if (userId <= 0) {
                return null;
            }
            return userDAO.getUserById(userId);
        } catch (Exception e) {
            System.out.println("Loi lay user theo id: " + e.getMessage());
            return null;
        }
    }

    public boolean updateProfile(int userId, String newPassword, String email, String phone) {
        try {
            if (userId <= 0) {
                System.out.println("userId khong hop le.");
                return false;
            }

            User oldUser = userDAO.getUserById(userId);
            if (oldUser == null) {
                System.out.println("Khong tim thay user.");
                return false;
            }

            String finalPassword = oldUser.getPassword();
            if (ValidationUtil.isNotBlank(newPassword)) {
                if (newPassword.length() < 6) {
                    System.out.println("Password moi phai co it nhat 6 ky tu.");
                    return false;
                }
                finalPassword = PasswordHash.hashPassword(newPassword);
            }

            String finalEmail = ValidationUtil.normalize(email);
            String finalPhone = ValidationUtil.normalize(phone);

            if (ValidationUtil.isNotBlank(finalEmail) && !ValidationUtil.isValidEmail(finalEmail)) {
                System.out.println("Email khong hop le.");
                return false;
            }

            if (ValidationUtil.isNotBlank(finalPhone) && !ValidationUtil.isValidPhone(finalPhone)) {
                System.out.println("Phone chi duoc chua so va co do dai tu 9 den 11 ky tu.");
                return false;
            }

            User user = new User();
            user.setUserId(userId);
            user.setPassword(finalPassword);
            user.setEmail(finalEmail);
            user.setPhone(finalPhone);

            return userDAO.updateUserProfile(user);

        } catch (Exception e) {
            System.out.println("Loi cap nhat profile: " + e.getMessage());
            return false;
        }
    }

    private boolean createUserByRole(String username, String password, String email, String phone, String role) {
        if (!isValidAccountInput(username, password, email, phone)) {
            return false;
        }

        if (userDAO.usernameExists(username.trim())) {
            System.out.println("Username da ton tai.");
            return false;
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(PasswordHash.hashPassword(password));
        user.setEmail(ValidationUtil.normalize(email));
        user.setPhone(ValidationUtil.normalize(phone));
        user.setRole(role);

        boolean result = userDAO.addUser(user);

        if (result) {
            System.out.println("Tao tai khoan " + role + " thanh cong.");
        } else {
            System.out.println("Tao tai khoan " + role + " that bai.");
        }

        return result;
    }

    private boolean isValidAccountInput(String username, String password, String email, String phone) {
        if (ValidationUtil.isBlank(username) || ValidationUtil.isBlank(password)) {
            System.out.println("Username va password khong duoc de trong.");
            return false;
        }

        if (username.trim().length() < 3) {
            System.out.println("Username phai co it nhat 3 ky tu.");
            return false;
        }

        if (password.length() < 6) {
            System.out.println("Password phai co it nhat 6 ky tu.");
            return false;
        }

        if (ValidationUtil.isNotBlank(email) && !ValidationUtil.isValidEmail(email.trim())) {
            System.out.println("Email khong hop le.");
            return false;
        }

        if (ValidationUtil.isNotBlank(phone) && !ValidationUtil.isValidPhone(phone.trim())) {
            System.out.println("Phone chi duoc chua so va co do dai tu 9 den 11 ky tu.");
            return false;
        }

        return true;
    }
}