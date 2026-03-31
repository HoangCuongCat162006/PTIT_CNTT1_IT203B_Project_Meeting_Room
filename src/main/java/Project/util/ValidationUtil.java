package Project.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidationUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static boolean isValidEmail(String email) {
        if (isBlank(email)) {
            return false;
        }
        String value = email.trim();
        return value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        if (isBlank(phone)) {
            return false;
        }
        return phone.trim().matches("\\d{9,11}");
    }

    public static boolean isValidDateTime(String dateTime) {
        if (isBlank(dateTime)) {
            return false;
        }
        try {
            LocalDateTime.parse(dateTime.trim(), DATE_TIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidDate(String date) {
        if (isBlank(date)) {
            return false;
        }
        try {
            LocalDate.parse(date.trim(), DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isPositiveInt(int number) {
        return number > 0;
    }

    public static boolean isNonNegativeInt(int number) {
        return number >= 0;
    }

    public static boolean isNonNegativeDouble(double number) {
        return number >= 0;
    }

    public static String normalize(String value) {
        return isBlank(value) ? null : value.trim();
    }
}