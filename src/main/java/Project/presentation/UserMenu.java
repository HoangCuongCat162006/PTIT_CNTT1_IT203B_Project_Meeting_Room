package Project.presentation;

import Project.model.Booking;
import Project.model.Equipment;
import Project.model.Room;
import Project.model.Service;
import Project.model.User;
import Project.service.BookingService;
import Project.service.UserService;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private static final BookingService bookingService = new BookingService();
    private static final UserService userService = new UserService();

    private UserMenu() {
    }

    public static void showUserMenu(User currentUser) {
        while (true) {
            currentUser = userService.getUserById(currentUser.getUserId());

            System.out.println("\n=================== EMPLOYEE MENU ===================");
            System.out.println("Xin chao: " + currentUser.getUsername());
            System.out.println("1. Xem phong trong theo thoi gian");
            System.out.println("2. Dat phong va yeu cau dich vu");
            System.out.println("3. Xem lich hop cua toi");
            System.out.println("4. Huy booking PENDING");
            System.out.println("5. Xem danh sach thiet bi");
            System.out.println("6. Xem danh sach dich vu");
            System.out.println("7. Xem ho so ca nhan");
            System.out.println("8. Cap nhat ho so ca nhan");
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
                    viewAvailableRooms();
                    break;
                case 2:
                    bookRoom(currentUser);
                    break;
                case 3:
                    viewMyBookings(currentUser);
                    break;
                case 4:
                    cancelPendingBooking(currentUser);
                    break;
                case 5:
                    viewEquipments();
                    break;
                case 6:
                    viewServices();
                    break;
                case 7:
                    viewMyProfile(currentUser);
                    break;
                case 8:
                    updateMyProfile(currentUser);
                    currentUser = userService.getUserById(currentUser.getUserId());
                    break;
                case 0:
                    System.out.println("Dang xuat Employee.");
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private static void viewAvailableRooms() {
        System.out.println("\n===== XEM PHONG TRONG =====");

        System.out.print("Nhap start time (yyyy-MM-dd HH:mm:ss): ");
        String startTime = scanner.nextLine().trim();

        System.out.print("Nhap end time (yyyy-MM-dd HH:mm:ss): ");
        String endTime = scanner.nextLine().trim();

        int participantCount = readInt("Nhap so nguoi tham gia: ");
        if (participantCount <= 0) {
            return;
        }

        List<Room> rooms = bookingService.getAvailableRooms(startTime, endTime);
        List<Room> matchedRooms = filterRoomsByCapacity(rooms, participantCount);

        if (matchedRooms.isEmpty()) {
            System.out.println("Khong co phong trong phu hop, du lieu thoi gian khong hop le, hoac suc chua khong du.");
            return;
        }

        printRoomTable(matchedRooms);
    }

    private static void bookRoom(User currentUser) {
        System.out.println("\n===== DAT PHONG & YEU CAU DICH VU =====");

        System.out.print("Nhap start time (yyyy-MM-dd HH:mm:ss): ");
        String startTime = scanner.nextLine().trim();

        System.out.print("Nhap end time (yyyy-MM-dd HH:mm:ss): ");
        String endTime = scanner.nextLine().trim();

        int participantCount = readInt("Nhap so nguoi tham gia: ");
        if (participantCount <= 0) {
            return;
        }

        List<Room> availableRooms = bookingService.getAvailableRooms(startTime, endTime);
        List<Room> matchedRooms = filterRoomsByCapacity(availableRooms, participantCount);

        if (matchedRooms.isEmpty()) {
            System.out.println("Khong co phong trong phu hop hoac thoi gian khong hop le.");
            return;
        }

        System.out.println("\nPhong trong hien co:");
        printRoomTable(matchedRooms);

        int roomId = readInt("Nhap roomId muon dat: ");
        if (roomId <= 0) {
            return;
        }

        if (!isRoomInAvailableList(roomId, matchedRooms)) {
            System.out.println("roomId khong nam trong danh sach phong trong phu hop.");
            return;
        }

        Integer serviceId = chooseOptionalService();
        if (serviceId == Integer.MIN_VALUE) {
            return;
        }

        List<Integer> equipmentIds = chooseOptionalEquipments();
        if (equipmentIds == null) {
            return;
        }

        boolean result = bookingService.createBooking(
                currentUser.getUserId(),
                roomId,
                startTime,
                endTime,
                participantCount,
                serviceId,
                equipmentIds
        );

        if (result) {
            System.out.println("Dat phong thanh cong. Booking da duoc luu voi trang thai PENDING.");
        } else {
            System.out.println("Dat phong that bai.");
        }
    }

    private static void viewMyBookings(User currentUser) {
        System.out.println("\n===== LICH HOP CUA TOI =====");

        List<Booking> bookings = bookingService.getBookingsByUserId(currentUser.getUserId());

        if (bookings.isEmpty()) {
            System.out.println("Ban chua co booking nao.");
            return;
        }

        System.out.printf("%-6s %-7s %-8s %-20s %-20s %-12s %-16s %-22s%n",
                "ID", "Room", "People", "Start Time", "End Time", "Status", "Preparation", "Meeting Status");
        System.out.println("------------------------------------------------------------------------------------------------------------------");

        for (Booking booking : bookings) {
            System.out.printf("%-6d %-7d %-8d %-20s %-20s %-12s %-16s %-22s%n",
                    booking.getBookingId(),
                    booking.getRoomId(),
                    booking.getParticipantCount(),
                    safeText(booking.getStartTime(), 20),
                    safeText(booking.getEndTime(), 20),
                    safeText(booking.getStatus(), 12),
                    safeText(booking.getPreparationStatus(), 16),
                    safeText(getMeetingReadableStatus(booking), 22));
        }
    }

    private static void cancelPendingBooking(User currentUser) {
        System.out.println("\n===== HUY BOOKING PENDING =====");
        List<Booking> bookings = bookingService.getBookingsByUserId(currentUser.getUserId());

        List<Booking> pendingBookings = new ArrayList<Booking>();
        for (Booking booking : bookings) {
            if (BookingService.STATUS_PENDING.equalsIgnoreCase(booking.getStatus())) {
                pendingBookings.add(booking);
            }
        }

        if (pendingBookings.isEmpty()) {
            System.out.println("Ban khong co booking nao dang PENDING de huy.");
            return;
        }

        System.out.printf("%-6s %-7s %-8s %-20s %-20s %-12s%n",
                "ID", "Room", "People", "Start Time", "End Time", "Status");
        System.out.println("-------------------------------------------------------------------------------------");
        for (Booking booking : pendingBookings) {
            System.out.printf("%-6d %-7d %-8d %-20s %-20s %-12s%n",
                    booking.getBookingId(),
                    booking.getRoomId(),
                    booking.getParticipantCount(),
                    safeText(booking.getStartTime(), 20),
                    safeText(booking.getEndTime(), 20),
                    safeText(booking.getStatus(), 12));
        }

        int bookingId = readInt("Nhap bookingId can huy: ");
        if (bookingId <= 0) {
            return;
        }

        System.out.print("Ban co chac chan muon huy booking nay? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (!"Y".equalsIgnoreCase(confirm)) {
            System.out.println("Da huy thao tac.");
            return;
        }

        boolean result = bookingService.cancelPendingBooking(bookingId, currentUser.getUserId());
        if (result) {
            System.out.println("Huy booking thanh cong.");
        } else {
            System.out.println("Huy booking that bai.");
        }
    }

    private static void viewEquipments() {
        System.out.println("\n===== DANH SACH THIET BI =====");

        List<Equipment> equipments = bookingService.getAllEquipments();

        if (equipments.isEmpty()) {
            System.out.println("Chua co du lieu thiet bi.");
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

    private static void viewServices() {
        System.out.println("\n===== DANH SACH DICH VU =====");
        List<Service> services = bookingService.getAllServices();

        if (services.isEmpty()) {
            System.out.println("Chua co du lieu dich vu.");
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

    private static Integer chooseOptionalService() {
        List<Service> services = bookingService.getAllServices();
        if (services.isEmpty()) {
            System.out.println("\nHe thong chua co dich vu nao. Bo qua buoc chon dich vu.");
            return null;
        }

        System.out.println("\nDanh sach dich vu:");
        viewServices();
        System.out.print("Nhap serviceId muon chon (bo trong neu khong can): ");
        String serviceInput = scanner.nextLine().trim();

        if (serviceInput.isEmpty()) {
            return null;
        }

        int serviceId;
        try {
            serviceId = Integer.parseInt(serviceInput);
        } catch (Exception e) {
            System.out.println("serviceId khong hop le.");
            return Integer.MIN_VALUE;
        }

        Service service = bookingService.getServiceById(serviceId);
        if (service == null) {
            System.out.println("Khong tim thay dich vu vua chon.");
            return Integer.MIN_VALUE;
        }

        return serviceId;
    }

    private static List<Integer> chooseOptionalEquipments() {
        List<Equipment> equipments = bookingService.getAllEquipments();
        if (equipments.isEmpty()) {
            System.out.println("\nHe thong chua co thiet bi nao. Bo qua buoc chon thiet bi.");
            return new ArrayList<Integer>();
        }

        System.out.println("\nDanh sach thiet bi:");
        viewEquipments();
        System.out.print("Nhap danh sach equipmentId muon muon them, cach nhau boi dau phay. Bo trong neu khong muon: ");
        String equipmentInput = scanner.nextLine().trim();
        return parseEquipmentIds(equipmentInput);
    }

    private static void viewMyProfile(User currentUser) {
        User freshUser = userService.getUserById(currentUser.getUserId());
        if (freshUser == null) {
            System.out.println("Khong tim thay ho so ca nhan.");
            return;
        }

        System.out.println("\n===== HO SO CA NHAN =====");
        System.out.println("User ID  : " + freshUser.getUserId());
        System.out.println("Username : " + safeText(freshUser.getUsername(), 50));
        System.out.println("Role     : " + safeText(freshUser.getRole(), 50));
        System.out.println("Email    : " + safeText(freshUser.getEmail(), 50));
        System.out.println("Phone    : " + safeText(freshUser.getPhone(), 50));
    }

    private static void updateMyProfile(User currentUser) {
        User freshUser = userService.getUserById(currentUser.getUserId());
        if (freshUser == null) {
            System.out.println("Khong tim thay user.");
            return;
        }

        System.out.println("\n===== CAP NHAT HO SO CA NHAN =====");
        System.out.println("De trong neu muon giu nguyen gia tri cu.");

        System.out.println("Email hien tai: " + safeText(freshUser.getEmail(), 50));
        System.out.print("Nhap email moi: ");
        String email = scanner.nextLine().trim();

        System.out.println("Phone hien tai: " + safeText(freshUser.getPhone(), 50));
        System.out.print("Nhap phone moi: ");
        String phone = scanner.nextLine().trim();

        String newPassword = readPassword("Nhap password moi (bo trong neu khong doi): ");

        boolean result = userService.updateProfile(
                freshUser.getUserId(),
                newPassword,
                email.isEmpty() ? freshUser.getEmail() : email,
                phone.isEmpty() ? freshUser.getPhone() : phone
        );

        if (result) {
            System.out.println("Cap nhat ho so thanh cong.");
        } else {
            System.out.println("Cap nhat ho so that bai.");
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

    private static List<Room> filterRoomsByCapacity(List<Room> rooms, int participantCount) {
        List<Room> matchedRooms = new ArrayList<Room>();
        for (Room room : rooms) {
            if (room.getCapacity() >= participantCount) {
                matchedRooms.add(room);
            }
        }
        return matchedRooms;
    }

    private static void printRoomTable(List<Room> rooms) {
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

    private static String getMeetingReadableStatus(Booking booking) {
        if (BookingService.STATUS_PENDING.equalsIgnoreCase(booking.getStatus())) {
            return "Dang cho duyet";
        }

        if (BookingService.STATUS_CANCELLED.equalsIgnoreCase(booking.getStatus())) {
            return "Da huy / bi tu choi";
        }

        if (BookingService.STATUS_CONFIRMED.equalsIgnoreCase(booking.getStatus())
                && BookingService.PREPARATION_READY.equalsIgnoreCase(booking.getPreparationStatus())) {
            return "Phong da san sang";
        }

        if (BookingService.STATUS_CONFIRMED.equalsIgnoreCase(booking.getStatus())
                && BookingService.PREPARATION_MISSING_EQUIPMENT.equalsIgnoreCase(booking.getPreparationStatus())) {
            return "Dang thieu thiet bi";
        }

        if (BookingService.STATUS_CONFIRMED.equalsIgnoreCase(booking.getStatus())
                && BookingService.PREPARATION_PREPARING.equalsIgnoreCase(booking.getPreparationStatus())) {
            return "Dang chuan bi";
        }

        if (BookingService.STATUS_CONFIRMED.equalsIgnoreCase(booking.getStatus())
                && BookingService.PREPARATION_NOT_ASSIGNED.equalsIgnoreCase(booking.getPreparationStatus())) {
            return "Cho ho tro";
        }

        return "Khong xac dinh";
    }

    private static List<Integer> parseEquipmentIds(String input) {
        List<Integer> equipmentIds = new ArrayList<Integer>();

        if (input == null || input.trim().isEmpty()) {
            return equipmentIds;
        }

        String[] parts = input.split(",");
        for (String part : parts) {
            try {
                equipmentIds.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException e) {
                System.out.println("Bo qua equipmentId khong hop le: " + part);
            }
        }

        return equipmentIds;
    }

    private static boolean isRoomInAvailableList(int roomId, List<Room> availableRooms) {
        for (Room room : availableRooms) {
            if (room.getRoomId() == roomId) {
                return true;
            }
        }
        return false;
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

    private static String readPassword(String prompt) {
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword(prompt);
            return passwordChars == null ? "" : new String(passwordChars);
        }

        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static void showMainMenu() {
    }
}