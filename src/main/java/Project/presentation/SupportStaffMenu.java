package Project.presentation;

import Project.model.Booking;
import Project.model.User;
import Project.service.BookingService;

import java.util.List;
import java.util.Scanner;

public class SupportStaffMenu {
    private static final Scanner scanner = new Scanner(System.in);
    private static final BookingService bookingService = new BookingService();

    private SupportStaffMenu() {
    }

    public static void showSupportStaffMenu(User currentUser) {
        while (true) {
            System.out.println("\n================= SUPPORT STAFF MENU =================");
            System.out.println("Xin chao Support Staff: " + currentUser.getUsername());
            System.out.println("1. Xem booking duoc phan cong theo ngay (chua hoan tat)");
            System.out.println("2. Cap nhat trang thai chuan bi");
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
                    viewAssignedBookingsByDate(currentUser);
                    break;
                case 2:
                    updatePreparationStatus(currentUser);
                    break;
                case 0:
                    System.out.println("Dang xuat Support Staff.");
                    return;
                default:
                    System.out.println("Lua chon khong hop le.");
            }
        }
    }

    private static void viewAssignedBookingsByDate(User currentUser) {
        System.out.println("\n===== BOOKING DUOC PHAN CONG THEO NGAY =====");
        System.out.print("Nhap ngay can xem (yyyy-MM-dd): ");
        String date = scanner.nextLine().trim();

        List<Booking> bookings = bookingService.getAssignedBookingsByDateNotCompleted(currentUser.getUserId(), date);
        printAssignedBookingTable(bookings);
    }

    private static void updatePreparationStatus(User currentUser) {
        System.out.println("\n===== CAP NHAT TRANG THAI CHUAN BI =====");
        System.out.print("Nhap ngay can xem booking (yyyy-MM-dd): ");
        String date = scanner.nextLine().trim();

        List<Booking> bookings = bookingService.getAssignedBookingsByDateNotCompleted(currentUser.getUserId(), date);
        if (bookings.isEmpty()) {
            System.out.println("Khong co booking nao duoc phan cong trong ngay nay hoac tat ca da READY.");
            return;
        }

        printAssignedBookingTable(bookings);

        int bookingId = readInt("Nhap bookingId can cap nhat: ");
        if (bookingId <= 0) {
            return;
        }

        System.out.println("1. PREPARING");
        System.out.println("2. READY");
        System.out.println("3. MISSING_EQUIPMENT");
        System.out.print("Chon trang thai moi: ");
        String statusChoice = scanner.nextLine().trim();

        String preparationStatus = mapPreparationStatus(statusChoice);
        if (preparationStatus == null) {
            System.out.println("Trang thai khong hop le.");
            return;
        }

        boolean result = bookingService.updatePreparationStatus(
                bookingId,
                currentUser.getUserId(),
                preparationStatus
        );

        if (result) {
            System.out.println("Cap nhat trang thai chuan bi thanh cong. Da thong bao tren he thong.");
        } else {
            System.out.println("Cap nhat trang thai chuan bi that bai.");
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

    private static String mapPreparationStatus(String choice) {
        switch (choice) {
            case "1":
                return BookingService.PREPARATION_PREPARING;
            case "2":
                return BookingService.PREPARATION_READY;
            case "3":
                return BookingService.PREPARATION_MISSING_EQUIPMENT;
            default:
                return null;
        }
    }

    private static void printAssignedBookingTable(List<Booking> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            System.out.println("Khong co booking nao phu hop.");
            return;
        }

        System.out.printf("%-6s %-7s %-8s %-20s %-20s %-12s %-16s%n",
                "ID", "Room", "People", "Start Time", "End Time", "Status", "Preparation");
        System.out.println("------------------------------------------------------------------------------------------------");

        for (Booking booking : bookings) {
            System.out.printf("%-6d %-7d %-8d %-20s %-20s %-12s %-16s%n",
                    booking.getBookingId(),
                    booking.getRoomId(),
                    booking.getParticipantCount(),
                    safeText(booking.getStartTime(), 20),
                    safeText(booking.getEndTime(), 20),
                    safeText(booking.getStatus(), 12),
                    safeText(booking.getPreparationStatus(), 16));
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