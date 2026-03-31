package Project.presentation;

import Project.model.Booking;
import Project.model.Equipment;
import Project.model.Room;
import Project.model.Service;
import Project.model.User;
import Project.service.BookingService;
import Project.service.UserService;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private static final BookingService bookingService = new BookingService();
    private static final UserService userService = new UserService();

    private AdminMenu() {
    }

    public static void showAdminMenu(User currentUser) {
        while (true) {
            System.out.println("\n==================== ADMIN MENU ====================");
            System.out.println("Xin chao Admin: " + currentUser.getUsername());

            System.out.println("1. Xem tat ca booking");
            System.out.println("2. Xem booking dang cho duyet (PENDING)");
            System.out.println("3. Duyet booking");
            System.out.println("4. Tu choi booking");
            System.out.println("5. Phan cong Support Staff cho booking da duyet");

            System.out.println("6. Xem danh sach phong");
            System.out.println("7. Them phong");
            System.out.println("8. Cap nhat phong");
            System.out.println("9. Xoa phong");

            System.out.println("10. Xem danh sach thiet bi");
            System.out.println("11. Them thiet bi");
            System.out.println("12. Cap nhat thiet bi");
            System.out.println("13. Xoa thiet bi");

            System.out.println("14. Xem danh sach dich vu");
            System.out.println("15. Them dich vu");
            System.out.println("16. Cap nhat dich vu");
            System.out.println("17. Xoa dich vu");

            System.out.println("18. Xem danh sach nguoi dung");
            System.out.println("19. Tao tai khoan Support Staff / Admin");

            System.out.println("0. Dang xuat");
            System.out.print("Chon chuc nang: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Vui long nhap so hop le.");
                continue;
            }

            switch (choice) {
                case 1:
                    viewAllBookings();
                    break;
                case 2:
                    viewPendingBookings();
                    break;
                case 3:
                    approveBooking();
                    break;
                case 4:
                    rejectBooking();
                    break;
                case 5:
                    assignSupportStaff();
                    break;
                case 6:
                    viewRooms();
                    break;
                case 7:
                    addRoom();
                    break;
                case 8:
                    updateRoom();
                    break;
                case 9:
                    deleteRoom();
                    break;
                case 10:
                    viewEquipments();
                    break;
                case 11:
                    addEquipment();
                    break;
                case 12:
                    updateEquipment();
                    break;
                case 13:
                    deleteEquipment();
                    break;
                case 14:
                    viewServices();
                    break;
                case 15:
                    addService();
                    break;
                case 16:
                    updateService();
                    break;
                case 17:
                    deleteService();
                    break;
                case 18:
                    viewUsers();
                    break;
                case 19:
                    createAdminOrSupportStaff();
                    break;
                case 0:
                    System.out.println("Dang xuat Admin.");
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private static void viewAllBookings() {
        System.out.println("\n===== DANH SACH TAT CA BOOKING =====");
        List<Booking> bookings = bookingService.getAllBookings();
        printBookingTable(bookings);
    }

    private static void viewPendingBookings() {
        System.out.println("\n===== DANH SACH BOOKING PENDING =====");
        List<Booking> bookings = bookingService.getPendingBookings();
        printBookingTable(bookings);
    }

    private static void approveBooking() {
        System.out.println("\n===== DUYET BOOKING =====");
        List<Booking> pendingBookings = bookingService.getPendingBookings();
        if (pendingBookings.isEmpty()) {
            System.out.println("Khong co booking nao dang cho duyet.");
            return;
        }

        printBookingTable(pendingBookings);

        int bookingId = readInt("Nhap bookingId can duyet: ");
        if (bookingId <= 0) {
            return;
        }

        boolean result = bookingService.approveBooking(bookingId);
        if (result) {
            System.out.println("Duyet booking thanh cong. Booking da chuyen sang CONFIRMED.");
        } else {
            System.out.println("Duyet booking that bai.");
        }
    }

    private static void rejectBooking() {
        System.out.println("\n===== TU CHOI BOOKING =====");
        List<Booking> pendingBookings = bookingService.getPendingBookings();
        if (pendingBookings.isEmpty()) {
            System.out.println("Khong co booking nao dang cho duyet.");
            return;
        }

        printBookingTable(pendingBookings);

        int bookingId = readInt("Nhap bookingId can tu choi: ");
        if (bookingId <= 0) {
            return;
        }

        boolean result = bookingService.rejectBooking(bookingId);
        if (result) {
            System.out.println("Tu choi booking thanh cong. Da thong bao tren he thong.");
        } else {
            System.out.println("Tu choi booking that bai.");
        }
    }

    private static void assignSupportStaff() {
        System.out.println("\n===== PHAN CONG SUPPORT STAFF =====");
        List<Booking> confirmedBookings = bookingService.getConfirmedBookingsWithoutSupportStaff();
        if (confirmedBookings.isEmpty()) {
            System.out.println("Khong co booking CONFIRMED nao chua duoc phan cong.");
            return;
        }

        System.out.println("Danh sach booking da duyet va chua duoc phan cong:");
        printBookingTable(confirmedBookings);

        List<User> supportStaffs = userService.getUsersByRole("Support Staff");
        if (supportStaffs.isEmpty()) {
            System.out.println("Chua co Support Staff nao. Hay tao tai khoan Support Staff truoc.");
            return;
        }

        System.out.println("Danh sach Support Staff:");
        printUserTable(supportStaffs);

        int bookingId = readInt("Nhap bookingId can phan cong: ");
        if (bookingId <= 0) {
            return;
        }

        int supportStaffId = readInt("Nhap supportStaffId duoc phan cong: ");
        if (supportStaffId <= 0) {
            return;
        }

        boolean result = bookingService.assignSupportStaff(bookingId, supportStaffId);
        if (result) {
            System.out.println("Phan cong Support Staff thanh cong. Da thong bao tren he thong.");
        } else {
            System.out.println("Phan cong Support Staff that bai.");
        }
    }

    private static void viewRooms() {
        System.out.println("\n===== DANH SACH PHONG =====");
        List<Room> rooms = bookingService.getAllRooms();
        printRoomTable(rooms);
    }

    private static void addRoom() {
        System.out.println("\n===== THEM PHONG =====");

        System.out.print("Nhap ten phong: ");
        String roomName = scanner.nextLine().trim();

        int capacity = readInt("Nhap suc chua: ");
        if (capacity <= 0) {
            return;
        }

        System.out.print("Nhap vi tri: ");
        String location = scanner.nextLine().trim();

        System.out.print("Nhap thiet bi co dinh: ");
        String fixedEquipment = scanner.nextLine().trim();

        boolean result = bookingService.addRoom(roomName, capacity, location, fixedEquipment);
        if (result) {
            System.out.println("Them phong thanh cong.");
        } else {
            System.out.println("Them phong that bai.");
        }
    }

    private static void updateRoom() {
        System.out.println("\n===== CAP NHAT PHONG =====");
        List<Room> rooms = bookingService.getAllRooms();
        printRoomTable(rooms);

        int roomId = readInt("Nhap roomId can cap nhat: ");
        if (roomId <= 0) {
            return;
        }

        System.out.print("Nhap ten phong moi: ");
        String roomName = scanner.nextLine().trim();

        int capacity = readInt("Nhap suc chua moi: ");
        if (capacity <= 0) {
            return;
        }

        System.out.print("Nhap vi tri moi: ");
        String location = scanner.nextLine().trim();

        System.out.print("Nhap thiet bi co dinh moi: ");
        String fixedEquipment = scanner.nextLine().trim();

        boolean result = bookingService.updateRoom(roomId, roomName, capacity, location, fixedEquipment);
        if (result) {
            System.out.println("Cap nhat phong thanh cong.");
        } else {
            System.out.println("Cap nhat phong that bai.");
        }
    }

    private static void deleteRoom() {
        System.out.println("\n===== XOA PHONG =====");
        List<Room> rooms = bookingService.getAllRooms();
        printRoomTable(rooms);

        int roomId = readInt("Nhap roomId can xoa: ");
        if (roomId <= 0) {
            return;
        }

        boolean result = bookingService.deleteRoom(roomId);
        if (result) {
            System.out.println("Xoa phong thanh cong.");
        } else {
            System.out.println("Xoa phong that bai. Neu phong da co booking, database co the dang chan xoa.");
        }
    }

    private static void viewEquipments() {
        System.out.println("\n===== DANH SACH THIET BI =====");
        List<Equipment> equipments = bookingService.getAllEquipments();
        printEquipmentTable(equipments);
    }

    private static void addEquipment() {
        System.out.println("\n===== THEM THIET BI =====");

        System.out.print("Nhap ten thiet bi: ");
        String equipmentName = scanner.nextLine().trim();

        int totalQuantity = readInt("Nhap tong so luong: ");
        if (totalQuantity < 0) {
            return;
        }

        int availableQuantity = readInt("Nhap so luong kha dung: ");
        if (availableQuantity < 0) {
            return;
        }

        boolean result = bookingService.addEquipment(equipmentName, totalQuantity, availableQuantity);
        if (result) {
            System.out.println("Them thiet bi thanh cong.");
        } else {
            System.out.println("Them thiet bi that bai.");
        }
    }

    private static void updateEquipment() {
        System.out.println("\n===== CAP NHAT THIET BI =====");
        List<Equipment> equipments = bookingService.getAllEquipments();
        printEquipmentTable(equipments);

        int equipmentId = readInt("Nhap equipmentId can cap nhat: ");
        if (equipmentId <= 0) {
            return;
        }

        Equipment oldEquipment = bookingService.getEquipmentById(equipmentId);
        if (oldEquipment == null) {
            System.out.println("Khong tim thay thiet bi.");
            return;
        }

        System.out.println("Thong tin cu:");
        System.out.println("Ten thiet bi      : " + safeText(oldEquipment.getEquipmentName(), 30));
        System.out.println("Tong so luong     : " + oldEquipment.getTotalQuantity());
        System.out.println("So luong kha dung : " + oldEquipment.getAvailableQuantity());
        System.out.println("Trang thai        : " + safeText(oldEquipment.getStatus(), 20));

        System.out.print("Nhap ten thiet bi moi: ");
        String equipmentName = scanner.nextLine().trim();

        int totalQuantity = readInt("Nhap tong so luong moi: ");
        if (totalQuantity < 0) {
            return;
        }

        int availableQuantity = readInt("Nhap so luong kha dung moi: ");
        if (availableQuantity < 0) {
            return;
        }

        boolean result = bookingService.updateEquipment(equipmentId, equipmentName, totalQuantity, availableQuantity);
        if (result) {
            System.out.println("Cap nhat thiet bi thanh cong.");
        } else {
            System.out.println("Cap nhat thiet bi that bai.");
        }
    }

    private static void deleteEquipment() {
        System.out.println("\n===== XOA THIET BI =====");
        List<Equipment> equipments = bookingService.getAllEquipments();
        printEquipmentTable(equipments);

        int equipmentId = readInt("Nhap equipmentId can xoa: ");
        if (equipmentId <= 0) {
            return;
        }

        Equipment equipment = bookingService.getEquipmentById(equipmentId);
        if (equipment == null) {
            System.out.println("Khong tim thay thiet bi.");
            return;
        }

        System.out.print("Ban co chac chan muon xoa thiet bi nay? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (!"Y".equalsIgnoreCase(confirm)) {
            System.out.println("Da huy thao tac xoa.");
            return;
        }

        boolean result = bookingService.deleteEquipment(equipmentId);
        if (result) {
            System.out.println("Xoa thiet bi thanh cong.");
        } else {
            System.out.println("Xoa thiet bi that bai.");
        }
    }

    private static void viewServices() {
        System.out.println("\n===== DANH SACH DICH VU =====");
        List<Service> services = bookingService.getAllServices();
        printServiceTable(services);
    }

    private static void addService() {
        System.out.println("\n===== THEM DICH VU =====");

        System.out.print("Nhap ten dich vu: ");
        String serviceName = scanner.nextLine().trim();

        System.out.print("Nhap mo ta: ");
        String description = scanner.nextLine().trim();

        double price = readDouble("Nhap gia dich vu: ");
        if (price < 0) {
            return;
        }

        boolean result = bookingService.addService(serviceName, description, price);
        if (result) {
            System.out.println("Them dich vu thanh cong.");
        } else {
            System.out.println("Them dich vu that bai.");
        }
    }

    private static void updateService() {
        System.out.println("\n===== CAP NHAT DICH VU =====");
        List<Service> services = bookingService.getAllServices();
        printServiceTable(services);

        int serviceId = readInt("Nhap serviceId can cap nhat: ");
        if (serviceId <= 0) {
            return;
        }

        Service oldService = bookingService.getServiceById(serviceId);
        if (oldService == null) {
            System.out.println("Khong tim thay dich vu.");
            return;
        }

        System.out.println("Thong tin cu:");
        System.out.println("Ten dich vu : " + safeText(oldService.getServiceName(), 30));
        System.out.println("Mo ta       : " + safeText(oldService.getDescription(), 50));
        System.out.println("Gia         : " + oldService.getPrice());

        System.out.print("Nhap ten dich vu moi: ");
        String serviceName = scanner.nextLine().trim();

        System.out.print("Nhap mo ta moi: ");
        String description = scanner.nextLine().trim();

        double price = readDouble("Nhap gia moi: ");
        if (price < 0) {
            return;
        }

        boolean result = bookingService.updateService(serviceId, serviceName, description, price);
        if (result) {
            System.out.println("Cap nhat dich vu thanh cong.");
        } else {
            System.out.println("Cap nhat dich vu that bai.");
        }
    }

    private static void deleteService() {
        System.out.println("\n===== XOA DICH VU =====");
        List<Service> services = bookingService.getAllServices();
        printServiceTable(services);

        int serviceId = readInt("Nhap serviceId can xoa: ");
        if (serviceId <= 0) {
            return;
        }

        Service service = bookingService.getServiceById(serviceId);
        if (service == null) {
            System.out.println("Khong tim thay dich vu.");
            return;
        }

        System.out.print("Ban co chac chan muon xoa dich vu nay? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (!"Y".equalsIgnoreCase(confirm)) {
            System.out.println("Da huy thao tac xoa.");
            return;
        }

        boolean result = bookingService.deleteService(serviceId);
        if (result) {
            System.out.println("Xoa dich vu thanh cong.");
        } else {
            System.out.println("Xoa dich vu that bai.");
        }
    }

    private static void viewUsers() {
        System.out.println("\n===== DANH SACH NGUOI DUNG =====");
        List<User> users = userService.getAllUsers();
        printUserTable(users);
    }

    private static void createAdminOrSupportStaff() {
        System.out.println("\n===== TAO TAI KHOAN =====");
        System.out.println("1. Support Staff");
        System.out.println("2. Admin");
        System.out.print("Chon role can tao: ");
        String roleChoice = scanner.nextLine().trim();

        System.out.print("Nhap username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Nhap password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Nhap email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Nhap phone: ");
        String phone = scanner.nextLine().trim();

        boolean result;
        if ("1".equals(roleChoice)) {
            result = userService.createSupportStaff(username, password, email, phone);
        } else if ("2".equals(roleChoice)) {
            result = userService.createAdmin(username, password, email, phone);
        } else {
            System.out.println("Role khong hop le.");
            return;
        }

        if (result) {
            System.out.println("Tao tai khoan thanh cong.");
        } else {
            System.out.println("Tao tai khoan that bai.");
        }
    }

    private static int readInt(String message) {
        System.out.print(message);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Vui long nhap so hop le.");
            return -1;
        }
    }

    private static double readDouble(String message) {
        System.out.print(message);
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Vui long nhap so hop le.");
            return -1;
        }
    }

    private static void printBookingTable(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            System.out.println("Khong co du lieu booking.");
            return;
        }

        System.out.printf("%-6s %-7s %-7s %-8s %-20s %-20s %-12s %-10s %-16s%n",
                "ID", "User", "Room", "People", "Start Time", "End Time", "Status", "Staff", "Preparation");
        System.out.println("----------------------------------------------------------------------------------------------------------------");

        for (Booking booking : bookings) {
            System.out.printf("%-6d %-7d %-7d %-8d %-20s %-20s %-12s %-10s %-16s%n",
                    booking.getBookingId(),
                    booking.getUserId(),
                    booking.getRoomId(),
                    booking.getParticipantCount(),
                    safeText(booking.getStartTime(), 20),
                    safeText(booking.getEndTime(), 20),
                    safeText(booking.getStatus(), 12),
                    booking.getSupportStaffId() == null ? "-" : String.valueOf(booking.getSupportStaffId()),
                    safeText(booking.getPreparationStatus(), 16));
        }
    }

    private static void printRoomTable(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            System.out.println("Khong co du lieu phong.");
            return;
        }

        System.out.printf("%-8s %-20s %-10s %-20s %-25s%n",
                "Room ID", "Room Name", "Capacity", "Location", "Fixed Equipment");
        System.out.println("------------------------------------------------------------------------------------------");

        for (Room room : rooms) {
            System.out.printf("%-8d %-20s %-10d %-20s %-25s%n",
                    room.getRoomId(),
                    safeText(room.getRoomName(), 20),
                    room.getCapacity(),
                    safeText(room.getLocation(), 20),
                    safeText(room.getFixedEquipment(), 25));
        }
    }

    private static void printEquipmentTable(List<Equipment> equipments) {
        if (equipments == null || equipments.isEmpty()) {
            System.out.println("Khong co du lieu thiet bi.");
            return;
        }

        System.out.printf("%-8s %-25s %-12s %-12s %-12s%n",
                "ID", "Equipment Name", "Total", "Available", "Status");
        System.out.println("--------------------------------------------------------------------------");

        for (Equipment equipment : equipments) {
            System.out.printf("%-8d %-25s %-12d %-12d %-12s%n",
                    equipment.getEquipmentId(),
                    safeText(equipment.getEquipmentName(), 25),
                    equipment.getTotalQuantity(),
                    equipment.getAvailableQuantity(),
                    safeText(equipment.getStatus(), 12));
        }
    }

    private static void printServiceTable(List<Service> services) {
        if (services == null || services.isEmpty()) {
            System.out.println("Khong co du lieu dich vu.");
            return;
        }

        System.out.printf("%-8s %-25s %-35s %-12s%n",
                "ID", "Service Name", "Description", "Price");
        System.out.println("--------------------------------------------------------------------------------");

        for (Service service : services) {
            System.out.printf("%-8d %-25s %-35s %-12.2f%n",
                    service.getServiceId(),
                    safeText(service.getServiceName(), 25),
                    safeText(service.getDescription(), 35),
                    service.getPrice());
        }
    }

    private static void printUserTable(List<User> users) {
        if (users == null || users.isEmpty()) {
            System.out.println("Khong co du lieu nguoi dung.");
            return;
        }

        System.out.printf("%-8s %-20s %-18s %-25s %-15s%n",
                "User ID", "Username", "Role", "Email", "Phone");
        System.out.println("------------------------------------------------------------------------------------------");

        for (User user : users) {
            System.out.printf("%-8d %-20s %-18s %-25s %-15s%n",
                    user.getUserId(),
                    safeText(user.getUsername(), 20),
                    safeText(user.getRole(), 18),
                    safeText(user.getEmail(), 25),
                    safeText(user.getPhone(), 15));
        }
    }

    private static String safeText(String value, int maxLength) {
        if (value == null || value.trim().isEmpty()) {
            return "-";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}