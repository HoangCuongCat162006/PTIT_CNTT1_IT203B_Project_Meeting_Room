package Project.presentation;

import Project.service.UserService;
import Project.util.DatabaseConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            try (Connection connection = DatabaseConnection.getConnection()) {
                if (connection != null) {
                    System.out.println("Ket noi MySQL thanh cong.");
                } else {
                    System.out.println("Khong the ket noi MySQL.");
                    return;
                }
            }

            UserService userService = new UserService();
            userService.ensureDefaultAdmin();

            AuthMenu.showMainMenu();

        } catch (Exception e) {
            System.out.println("Chuong trinh gap loi va da dung an toan: " + e.getMessage());
        }
    }
}