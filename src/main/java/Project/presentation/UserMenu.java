package Project.presentation;

import Project.dao.EquipmentDAO;
import Project.dao.RoomDAO;
import Project.model.Equipment;
import Project.model.Room;
import Project.model.User;
import Project.service.UserService;

import java.util.List;
import java.util.Scanner;

public class UserMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final RoomDAO roomDAO = new RoomDAO();
    private static final EquipmentDAO equipmentDAO = new EquipmentDAO();

    public static void showMainMenu() {
        userService.ensureDefaultAdmin();

        while (true) {
            System.out.println("\n He thong quan ly phong hop ");
            System.out.println("1. Dang ky Employee");
            System.out.println("2. Dang nhap");
            System.out.println("3. Thoat");
            System.out.print("Chon chuc nang: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    registerEmployee();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    System.out.println("Thoat chuong trinh.");
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private static void registerEmployee() {
        System.out.println("\n DANG KY EMPLOYEE ");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        boolean success = userService.registerEmployee(username, password, email, phone);
        if (success) {
            System.out.println("Dang ky Employee thanh cong.");
        } else {
            System.out.println("Dang ky that bai. Co the username da ton tai hoac du lieu khong hop le.");
        }
    }

    private static void login() {
        System.out.println("\n DANG NHAP ");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = userService.login(username, password);
        if (user == null) {
            System.out.println("Dang nhap that bai.");
            return;
        }

        System.out.println("Dang nhap thanh cong. Vai tro: " + user.getRole());

        switch (user.getRole()) {
            case "Admin":
                showAdminMenu(user);
                break;
            case "Employee":
                showEmployeeMenu(user);
                break;
            case "Support Staff":
                showSupportStaffMenu(user);
                break;
            default:
                System.out.println("Vai tro khong hop le.");
        }
    }

    private static void showAdminMenu(User admin) {
        while (true) {
            System.out.println("\n MENU ADMIN ");
            System.out.println("Xin chao, " + admin.getUsername());
            System.out.println("1. Xem danh sach phong");
            System.out.println("2. Them phong");
            System.out.println("3. Sua phong");
            System.out.println("4. Xoa phong");
            System.out.println("5. Xem danh sach thiet bi");
            System.out.println("6. Cap nhat so luong kha dung thiet bi");
            System.out.println("7. Tao tai khoan Support Staff");
            System.out.println("8. Xem danh sach nguoi dung");
            System.out.println("9. Dang xuat");
            System.out.print("Chon chuc nang: ");

            int choice = readInt();

            switch (choice) {
                case 1:
                    viewRooms();
                    break;
                case 2:
                    addRoom();
                    break;
                case 3:
                    updateRoom();
                    break;
                case 4:
                    deleteRoom();
                    break;
                case 5:
                    viewEquipments();
                    break;
                case 6:
                    updateEquipmentAvailableQuantity();
                    break;
                case 7:
                    createSupportStaff();
                    break;
                case 8:
                    viewUsers();
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private static void showEmployeeMenu(User employee) {
        System.out.println("\n MENU EMPLOYEE ");
        System.out.println("Xin chao, " + employee.getUsername());
    }

    private static void showSupportStaffMenu(User supportStaff) {
        System.out.println("\n MENU SUPPORT STAFF ");
        System.out.println("Xin chao, " + supportStaff.getUsername());
    }

    private static void viewRooms() {
        List<Room> rooms = roomDAO.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("Chua co phong nao.");
            return;
        }

        for (Room room : rooms) {
            System.out.println(room);
        }
    }

    private static void addRoom() {
        System.out.println("\n THEM PHONG ");
        Room room = new Room();

        System.out.print("Ten phong: ");
        room.setRoomName(scanner.nextLine());

        System.out.print("Suc chua: ");
        room.setCapacity(readInt());

        System.out.print("Vi tri: ");
        room.setLocation(scanner.nextLine());

        System.out.print("Thiet bi co dinh: ");
        room.setFixedEquipment(scanner.nextLine());

        boolean success = roomDAO.addRoom(room);
        if (success) {
            System.out.println("Them phong thanh cong.");
        } else {
            System.out.println("Them phong that bai.");
        }
    }

    private static void updateRoom() {
        System.out.println("\n SUA PHONG ");
        System.out.print("Nhap room_id can sua: ");
        int roomId = readInt();

        Room room = roomDAO.getRoomById(roomId);
        if (room == null) {
            System.out.println("Khong tim thay phong.");
            return;
        }

        System.out.println("De trong neu muon giu nguyen gia tri cu.");

        System.out.print("Ten phong moi (hien tai: " + room.getRoomName() + "): ");
        String roomName = scanner.nextLine();
        if (!roomName.trim().isEmpty()) {
            room.setRoomName(roomName);
        }

        System.out.print("Suc chua moi (hien tai: " + room.getCapacity() + "): ");
        String capacityInput = scanner.nextLine();
        if (!capacityInput.trim().isEmpty()) {
            try {
                room.setCapacity(Integer.parseInt(capacityInput));
            } catch (NumberFormatException e) {
                System.out.println("Suc chua khong hop le. Giu nguyen gia tri cu.");
            }
        }

        System.out.print("Vi tri moi (hien tai: " + room.getLocation() + "): ");
        String location = scanner.nextLine();
        if (!location.trim().isEmpty()) {
            room.setLocation(location);
        }

        System.out.print("Thiet bi co dinh moi (hien tai: " + room.getFixedEquipment() + "): ");
        String fixedEquipment = scanner.nextLine();
        if (!fixedEquipment.trim().isEmpty()) {
            room.setFixedEquipment(fixedEquipment);
        }

        boolean success = roomDAO.updateRoom(room);
        if (success) {
            System.out.println("Cap nhat phong thanh cong.");
        } else {
            System.out.println("Cap nhat phong that bai.");
        }
    }

    private static void deleteRoom() {
        System.out.println("\n XOA PHONG ");
        System.out.print("Nhap room_id can xoa: ");
        int roomId = readInt();

        boolean success = roomDAO.deleteRoom(roomId);
        if (success) {
            System.out.println("Xoa phong thanh cong.");
        } else {
            System.out.println("Xoa phong that bai. Co the phong dang duoc tham chieu trong Bookings hoac khong ton tai.");
        }
    }

    private static void viewEquipments() {
        List<Equipment> equipments = equipmentDAO.getAllEquipments();
        if (equipments.isEmpty()) {
            System.out.println("Chua co thiet bi nao. Hay them du lieu thiet bi truc tiep trong MySQL truoc.");
            return;
        }

        for (Equipment equipment : equipments) {
            System.out.println(equipment);
        }
    }

    private static void updateEquipmentAvailableQuantity() {
        System.out.println("\n CAP NHAT SO LUONG KHA DUNG ");
        System.out.print("Nhap equipment_id: ");
        int equipmentId = readInt();

        Equipment equipment = equipmentDAO.getEquipmentById(equipmentId);
        if (equipment == null) {
            System.out.println("Khong tim thay thiet bi.");
            return;
        }

        System.out.println("Thiet bi hien tai: " + equipment);
        System.out.print("Nhap available_quantity moi: ");
        int availableQuantity = readInt();

        if (availableQuantity < 0 || availableQuantity > equipment.getTotalQuantity()) {
            System.out.println("So luong kha dung phai tu 0 den total_quantity.");
            return;
        }

        boolean success = equipmentDAO.updateAvailableQuantity(equipmentId, availableQuantity);
        if (success) {
            System.out.println("Cap nhat thiet bi thanh cong.");
        } else {
            System.out.println("Cap nhat thiet bi that bai.");
        }
    }

    private static void createSupportStaff() {
        System.out.println("\n TAO TAI KHOAN SUPPORT STAFF ");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        boolean success = userService.createSupportStaff(username, password, email, phone);
        if (success) {
            System.out.println("Tao tai khoan Support Staff thanh cong.");
        } else {
            System.out.println("Tao tai khoan that bai. Co the username da ton tai hoac du lieu khong hop le.");
        }
    }

    private static void viewUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Chua co user nao.");
            return;
        }

        for (User user : users) {
            System.out.println(user);
        }
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Vui long nhap so hop le: ");
            }
        }
    }
}