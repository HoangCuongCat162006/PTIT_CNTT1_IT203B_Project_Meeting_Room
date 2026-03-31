package Project.presentation;

import Project.model.User;
import Project.service.UserService;
import Project.util.ValidationUtil;

import java.io.Console;
import java.util.Scanner;

public class AuthMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();

    private AuthMenu() {
    }

    public static void showMainMenu() {
        while (true) {
            try {
                System.out.println("\n============== MEETING ROOM MANAGEMENT ==============");
                System.out.println("1. Dang ky Employee");
                System.out.println("2. Dang nhap");
                System.out.println("3. Thoat");
                System.out.print("Chon chuc nang (1-3): ");

                int choice;
                try {
                    choice = Integer.parseInt(scanner.nextLine().trim());
                } catch (Exception e) {
                    System.out.println("Vui long nhap so hop le.");
                    continue;
                }

                switch (choice) {
                    case 1:
                        registerEmployee();
                        break;
                    case 2:
                        login();
                        break;
                    case 3:
                        System.out.println("Tam biet.");
                        return;
                    default:
                        System.out.println("Lua chon khong hop le.");
                }
            } catch (Exception e) {
                System.out.println("Da xay ra loi trong menu chinh: " + e.getMessage());
            }
        }
    }

    private static void registerEmployee() {
        try {
            System.out.println("\n===== DANG KY EMPLOYEE =====");

            System.out.print("Nhap username: ");
            String username = scanner.nextLine().trim();

            String password = readPassword("Nhap password: ");

            System.out.print("Nhap email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Nhap phone: ");
            String phone = scanner.nextLine().trim();

            if (ValidationUtil.isBlank(username) || ValidationUtil.isBlank(password)) {
                System.out.println("Username va password la thong tin bat buoc.");
                return;
            }

            boolean result = userService.registerEmployee(username, password, email, phone);

            if (result) {
                System.out.println("Dang ky thanh cong.");
            } else {
                System.out.println("Dang ky that bai.");
            }
        } catch (Exception e) {
            System.out.println("Loi dang ky: " + e.getMessage());
        }
    }

    private static void login() {
        try {
            System.out.println("\n===== DANG NHAP =====");

            System.out.print("Nhap username: ");
            String username = scanner.nextLine().trim();

            String password = readPassword("Nhap password: ");

            if (ValidationUtil.isBlank(username) || ValidationUtil.isBlank(password)) {
                System.out.println("Username va password khong duoc de trong.");
                return;
            }

            User user = userService.login(username, password);

            if (user == null) {
                System.out.println("Dang nhap that bai.");
                return;
            }

            System.out.println("Xin chao, " + user.getUsername() + " | Role: " + user.getRole());

            routeByRole(user);
        } catch (Exception e) {
            System.out.println("Loi dang nhap: " + e.getMessage());
        }
    }

    private static void routeByRole(User user) {
        try {
            if (user == null || user.getRole() == null) {
                System.out.println("Khong xac dinh duoc role.");
                return;
            }

            switch (user.getRole()) {
                case "Employee":
                    UserMenu.showUserMenu(user);
                    break;
                case "Admin":
                    AdminMenu.showAdminMenu(user);
                    break;
                case "Support Staff":
                    SupportStaffMenu.showSupportStaffMenu(user);
                    break;
                default:
                    System.out.println("Role khong hop le.");
            }
        } catch (Exception e) {
            System.out.println("Loi dieu huong role: " + e.getMessage());
        }
    }

    private static String readPassword(String prompt) {
        try {
            Console console = System.console();
            if (console != null) {
                char[] passwordChars = console.readPassword(prompt);
                return passwordChars == null ? "" : new String(passwordChars);
            }

            System.out.print(prompt);
            return scanner.nextLine().trim();
        } catch (Exception e) {
            System.out.println("Loi nhap password: " + e.getMessage());
            return "";
        }
    }
}