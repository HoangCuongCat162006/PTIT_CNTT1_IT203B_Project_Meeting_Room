package Project.service;

import Project.dao.impl.BookingDAO;
import Project.dao.impl.BookingDetailDAO;
import Project.dao.impl.EquipmentDAO;
import Project.dao.impl.RoomDAO;
import Project.dao.impl.ServiceDAO;
import Project.dao.impl.UserDAO;
import Project.model.Booking;
import Project.model.Equipment;
import Project.model.Room;
import Project.model.Service;
import Project.model.User;
import Project.util.DatabaseConnection;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class BookingService {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public static final String PREPARATION_NOT_ASSIGNED = "NOT_ASSIGNED";
    public static final String PREPARATION_PREPARING = "PREPARING";
    public static final String PREPARATION_READY = "READY";
    public static final String PREPARATION_MISSING_EQUIPMENT = "MISSING_EQUIPMENT";

    private final BookingDAO bookingDAO;
    private final RoomDAO roomDAO;
    private final EquipmentDAO equipmentDAO;
    private final ServiceDAO serviceDAO;
    private final BookingDetailDAO bookingDetailDAO;
    private final UserDAO userDAO;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.roomDAO = new RoomDAO();
        this.equipmentDAO = new EquipmentDAO();
        this.serviceDAO = new ServiceDAO();
        this.bookingDetailDAO = new BookingDetailDAO();
        this.userDAO = new UserDAO();
    }

    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    public Room getRoomById(int roomId) {
        if (roomId <= 0) {
            return null;
        }
        return roomDAO.getRoomById(roomId);
    }

    public boolean addRoom(String roomName, int capacity, String location, String fixedEquipment) {
        if (isBlank(roomName)) {
            System.out.println("Ten phong khong duoc de trong.");
            return false;
        }

        if (capacity <= 0) {
            System.out.println("Suc chua phong phai lon hon 0.");
            return false;
        }

        Room room = new Room();
        room.setRoomName(roomName.trim());
        room.setCapacity(capacity);
        room.setLocation(isBlank(location) ? null : location.trim());
        room.setFixedEquipment(isBlank(fixedEquipment) ? null : fixedEquipment.trim());

        return roomDAO.addRoom(room);
    }

    public boolean updateRoom(int roomId, String roomName, int capacity, String location, String fixedEquipment) {
        if (roomId <= 0) {
            System.out.println("roomId khong hop le.");
            return false;
        }

        if (isBlank(roomName)) {
            System.out.println("Ten phong khong duoc de trong.");
            return false;
        }

        if (capacity <= 0) {
            System.out.println("Suc chua phong phai lon hon 0.");
            return false;
        }

        Room existingRoom = roomDAO.getRoomById(roomId);
        if (existingRoom == null) {
            System.out.println("Khong tim thay phong.");
            return false;
        }

        existingRoom.setRoomName(roomName.trim());
        existingRoom.setCapacity(capacity);
        existingRoom.setLocation(isBlank(location) ? null : location.trim());
        existingRoom.setFixedEquipment(isBlank(fixedEquipment) ? null : fixedEquipment.trim());

        return roomDAO.updateRoom(existingRoom);
    }

    public boolean deleteRoom(int roomId) {
        if (roomId <= 0) {
            System.out.println("roomId khong hop le.");
            return false;
        }

        Room room = roomDAO.getRoomById(roomId);
        if (room == null) {
            System.out.println("Khong tim thay phong.");
            return false;
        }

        return roomDAO.deleteRoom(roomId);
    }

    public List<Room> getAvailableRooms(String startTime, String endTime) {
        if (!isValidDateTime(startTime) || !isValidDateTime(endTime)) {
            System.out.println("Sai dinh dang thoi gian. Dung dinh dang: yyyy-MM-dd HH:mm:ss");
            return new ArrayList<Room>();
        }

        if (!isStartBeforeEnd(startTime, endTime)) {
            System.out.println("Thoi gian bat dau phai nho hon thoi gian ket thuc.");
            return new ArrayList<Room>();
        }

        if (!isStartTimeInFuture(startTime)) {
            System.out.println("Thoi gian bat dau khong duoc o trong qua khu.");
            return new ArrayList<Room>();
        }

        return roomDAO.getAvailableRooms(startTime, endTime);
    }

    public List<Equipment> getAllEquipments() {
        return equipmentDAO.getAllEquipments();
    }

    public Equipment getEquipmentById(int equipmentId) {
        if (equipmentId <= 0) {
            return null;
        }
        return equipmentDAO.getEquipmentById(equipmentId);
    }

    public boolean addEquipment(String equipmentName, int totalQuantity, int availableQuantity) {
        if (isBlank(equipmentName)) {
            System.out.println("Ten thiet bi khong duoc de trong.");
            return false;
        }

        if (equipmentDAO.equipmentNameExists(equipmentName.trim())) {
            System.out.println("Ten thiet bi da ton tai.");
            return false;
        }

        if (totalQuantity < 0 || availableQuantity < 0) {
            System.out.println("So luong khong duoc am.");
            return false;
        }

        if (availableQuantity > totalQuantity) {
            System.out.println("So luong kha dung khong duoc lon hon tong so luong.");
            return false;
        }

        Equipment equipment = new Equipment();
        equipment.setEquipmentName(equipmentName.trim());
        equipment.setTotalQuantity(totalQuantity);
        equipment.setAvailableQuantity(availableQuantity);
        equipment.setStatus(availableQuantity > 0 ? "Available" : "Unavailable");

        return equipmentDAO.addEquipment(equipment);
    }

    public boolean updateEquipment(int equipmentId, String equipmentName, int totalQuantity, int availableQuantity) {
        if (equipmentId <= 0) {
            System.out.println("equipmentId khong hop le.");
            return false;
        }

        Equipment oldEquipment = equipmentDAO.getEquipmentById(equipmentId);
        if (oldEquipment == null) {
            System.out.println("Khong tim thay thiet bi.");
            return false;
        }

        if (isBlank(equipmentName)) {
            System.out.println("Ten thiet bi khong duoc de trong.");
            return false;
        }

        if (equipmentDAO.equipmentNameExistsExceptId(equipmentName.trim(), equipmentId)) {
            System.out.println("Ten thiet bi bi trung voi thiet bi khac.");
            return false;
        }

        if (totalQuantity < 0 || availableQuantity < 0) {
            System.out.println("So luong khong duoc am.");
            return false;
        }

        if (availableQuantity > totalQuantity) {
            System.out.println("So luong kha dung khong duoc lon hon tong so luong.");
            return false;
        }

        oldEquipment.setEquipmentName(equipmentName.trim());
        oldEquipment.setTotalQuantity(totalQuantity);
        oldEquipment.setAvailableQuantity(availableQuantity);
        oldEquipment.setStatus(availableQuantity > 0 ? "Available" : "Unavailable");

        return equipmentDAO.updateEquipment(oldEquipment);
    }

    public boolean deleteEquipment(int equipmentId) {
        if (equipmentId <= 0) {
            System.out.println("equipmentId khong hop le.");
            return false;
        }

        Equipment equipment = equipmentDAO.getEquipmentById(equipmentId);
        if (equipment == null) {
            System.out.println("Khong tim thay thiet bi.");
            return false;
        }

        if (equipmentDAO.hasBookingReference(equipmentId)) {
            System.out.println("Khong the xoa thiet bi vi da phat sinh rang buoc booking.");
            return false;
        }

        return equipmentDAO.deleteEquipment(equipmentId);
    }

    public boolean updateEquipmentQuantity(int equipmentId, int availableQuantity) {
        if (equipmentId <= 0) {
            System.out.println("equipmentId khong hop le.");
            return false;
        }

        if (availableQuantity < 0) {
            System.out.println("So luong kha dung khong duoc am.");
            return false;
        }

        Equipment equipment = equipmentDAO.getEquipmentById(equipmentId);
        if (equipment == null) {
            System.out.println("Khong tim thay thiet bi.");
            return false;
        }

        if (availableQuantity > equipment.getTotalQuantity()) {
            System.out.println("So luong kha dung khong duoc lon hon tong so luong.");
            return false;
        }

        return equipmentDAO.updateAvailableQuantity(equipmentId, availableQuantity);
    }

    public List<Service> getAllServices() {
        return serviceDAO.getAllServices();
    }

    public Service getServiceById(int serviceId) {
        if (serviceId <= 0) {
            return null;
        }
        return serviceDAO.getServiceById(serviceId);
    }

    public boolean addService(String serviceName, String description, double price) {
        if (isBlank(serviceName)) {
            System.out.println("Ten dich vu khong duoc de trong.");
            return false;
        }

        if (serviceDAO.serviceNameExists(serviceName.trim())) {
            System.out.println("Ten dich vu da ton tai.");
            return false;
        }

        if (price < 0) {
            System.out.println("Gia dich vu khong duoc am.");
            return false;
        }

        Service service = new Service();
        service.setServiceName(serviceName.trim());
        service.setDescription(isBlank(description) ? null : description.trim());
        service.setPrice(price);

        return serviceDAO.addService(service);
    }

    public boolean updateService(int serviceId, String serviceName, String description, double price) {
        if (serviceId <= 0) {
            System.out.println("serviceId khong hop le.");
            return false;
        }

        Service oldService = serviceDAO.getServiceById(serviceId);
        if (oldService == null) {
            System.out.println("Khong tim thay dich vu.");
            return false;
        }

        if (isBlank(serviceName)) {
            System.out.println("Ten dich vu khong duoc de trong.");
            return false;
        }

        if (serviceDAO.serviceNameExistsExceptId(serviceName.trim(), serviceId)) {
            System.out.println("Ten dich vu bi trung voi dich vu khac.");
            return false;
        }

        if (price < 0) {
            System.out.println("Gia dich vu khong duoc am.");
            return false;
        }

        oldService.setServiceName(serviceName.trim());
        oldService.setDescription(isBlank(description) ? null : description.trim());
        oldService.setPrice(price);

        return serviceDAO.updateService(oldService);
    }

    public boolean deleteService(int serviceId) {
        if (serviceId <= 0) {
            System.out.println("serviceId khong hop le.");
            return false;
        }

        Service service = serviceDAO.getServiceById(serviceId);
        if (service == null) {
            System.out.println("Khong tim thay dich vu.");
            return false;
        }

        if (serviceDAO.hasBookingReference(serviceId)) {
            System.out.println("Khong the xoa dich vu vi da phat sinh rang buoc booking.");
            return false;
        }

        return serviceDAO.deleteService(serviceId);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }

    public List<Booking> getPendingBookings() {
        return bookingDAO.getBookingsByStatus(STATUS_PENDING);
    }

    public List<Booking> getConfirmedBookingsWithoutSupportStaff() {
        List<Booking> allConfirmed = bookingDAO.getBookingsByStatus(STATUS_CONFIRMED);
        List<Booking> result = new ArrayList<Booking>();

        for (Booking booking : allConfirmed) {
            if (booking.getSupportStaffId() == null) {
                result.add(booking);
            }
        }

        return result;
    }

    public Booking getBookingById(int bookingId) {
        if (bookingId <= 0) {
            return null;
        }
        return bookingDAO.getBookingById(bookingId);
    }

    public List<Booking> getBookingsByUserId(int userId) {
        if (userId <= 0) {
            return new ArrayList<Booking>();
        }
        return bookingDAO.getBookingsByUserId(userId);
    }

    public List<Booking> getBookingsBySupportStaffId(int supportStaffId) {
        if (supportStaffId <= 0) {
            return new ArrayList<Booking>();
        }
        return bookingDAO.getBookingsBySupportStaffId(supportStaffId);
    }

    public List<Booking> getAssignedBookingsByDateNotCompleted(int supportStaffId, String date) {
        List<Booking> result = new ArrayList<Booking>();

        if (supportStaffId <= 0) {
            return result;
        }

        if (!isValidDate(date)) {
            System.out.println("Ngay khong hop le. Dung dinh dang: yyyy-MM-dd");
            return result;
        }

        List<Booking> bookings = bookingDAO.getBookingsBySupportStaffId(supportStaffId);

        for (Booking booking : bookings) {
            if (booking.getStartTime() == null) {
                continue;
            }

            boolean sameDate = booking.getStartTime().startsWith(date);
            boolean notCompleted = !PREPARATION_READY.equalsIgnoreCase(booking.getPreparationStatus());

            if (sameDate && notCompleted) {
                result.add(booking);
            }
        }

        return result;
    }

    public boolean createBooking(int userId, int roomId, String startTime, String endTime, List<Integer> equipmentIds) {
        return createBooking(userId, roomId, startTime, endTime, 1, null, equipmentIds);
    }

    public boolean createBooking(int userId, int roomId, String startTime, String endTime,
                                 int participantCount, List<Integer> equipmentIds) {
        return createBooking(userId, roomId, startTime, endTime, participantCount, null, equipmentIds);
    }

    public boolean createBooking(int userId, int roomId, String startTime, String endTime,
                                 int participantCount, Integer serviceId, List<Integer> equipmentIds) {
        if (userId <= 0 || roomId <= 0) {
            System.out.println("userId va roomId phai lon hon 0.");
            return false;
        }

        if (!userDAO.userIdExists(userId)) {
            System.out.println("userId khong ton tai.");
            return false;
        }

        if (!isValidDateTime(startTime) || !isValidDateTime(endTime)) {
            System.out.println("Sai dinh dang thoi gian. Dung dinh dang: yyyy-MM-dd HH:mm:ss");
            return false;
        }

        if (!isStartBeforeEnd(startTime, endTime)) {
            System.out.println("Thoi gian bat dau phai nho hon thoi gian ket thuc.");
            return false;
        }

        if (!isStartTimeInFuture(startTime)) {
            System.out.println("Ngay gio dat phong khong duoc nam trong qua khu.");
            return false;
        }

        if (participantCount <= 0) {
            System.out.println("So nguoi tham gia phai lon hon 0.");
            return false;
        }

        Room room = roomDAO.getRoomById(roomId);
        if (room == null) {
            System.out.println("Phong khong ton tai.");
            return false;
        }

        if (room.getCapacity() < participantCount) {
            System.out.println("Suc chua phong phai lon hon hoac bang so nguoi tham gia.");
            return false;
        }

        if (bookingDAO.isTimeConflict(roomId, startTime, endTime)) {
            System.out.println("Xung dot thoi gian: phong nay da co booking trong khoang nay.");
            return false;
        }

        if (serviceId != null) {
            Service service = serviceDAO.getServiceById(serviceId);
            if (service == null) {
                System.out.println("Dich vu da chon khong ton tai.");
                return false;
            }
        }

        List<Integer> normalizedEquipmentIds = normalizeEquipmentIds(equipmentIds);

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setRoomId(roomId);
            booking.setParticipantCount(participantCount);
            booking.setBookingTime(LocalDateTime.now().format(FORMATTER));
            booking.setStartTime(startTime);
            booking.setEndTime(endTime);
            booking.setServiceId(serviceId);
            booking.setEquipmentId(normalizedEquipmentIds.isEmpty() ? null : normalizedEquipmentIds.get(0));
            booking.setStatus(STATUS_PENDING);
            booking.setSupportStaffId(null);
            booking.setPreparationStatus(PREPARATION_NOT_ASSIGNED);

            int bookingId = bookingDAO.addBooking(booking, conn);
            if (bookingId <= 0) {
                conn.rollback();
                System.out.println("Khong the tao booking.");
                return false;
            }

            for (Integer equipmentId : normalizedEquipmentIds) {
                Equipment equipment = equipmentDAO.getEquipmentById(equipmentId, conn);

                if (equipment == null) {
                    conn.rollback();
                    System.out.println("Thiet bi id = " + equipmentId + " khong ton tai.");
                    return false;
                }

                if (equipment.getAvailableQuantity() < 1) {
                    conn.rollback();
                    System.out.println("Thiet bi " + equipment.getEquipmentName() + " dang het.");
                    return false;
                }

                boolean added = bookingDetailDAO.addEquipmentToBooking(bookingId, equipmentId, conn);
                boolean updated = equipmentDAO.updateAvailableQuantity(
                        equipmentId,
                        equipment.getAvailableQuantity() - 1,
                        conn
                );

                if (!added || !updated) {
                    conn.rollback();
                    System.out.println("Khong the luu thiet bi muon kem.");
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            rollbackQuietly(conn);
            System.out.println("Loi nghiep vu booking: " + e.getMessage());
            return false;

        } finally {
            closeConnectionQuietly(conn);
        }
    }

    public boolean approveBooking(int bookingId) {
        if (bookingId <= 0) {
            System.out.println("bookingId phai lon hon 0.");
            return false;
        }

        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) {
            System.out.println("Khong tim thay booking.");
            return false;
        }

        if (!STATUS_PENDING.equalsIgnoreCase(booking.getStatus())) {
            System.out.println("Chi booking dang PENDING moi duoc duyet.");
            return false;
        }

        if (!isStartTimeInFuture(booking.getStartTime())) {
            System.out.println("Khong the duyet booking da o qua khu.");
            return false;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Booking bookingForUpdate = bookingDAO.getBookingByIdForUpdate(bookingId, conn);
            if (bookingForUpdate == null) {
                conn.rollback();
                System.out.println("Booking khong ton tai khi duyet.");
                return false;
            }

            if (!STATUS_PENDING.equalsIgnoreCase(bookingForUpdate.getStatus())) {
                conn.rollback();
                System.out.println("Booking nay da duoc xu ly truoc do.");
                return false;
            }

            boolean updated = bookingDAO.approveBooking(bookingId, conn);
            if (!updated) {
                conn.rollback();
                System.out.println("Duyet booking that bai.");
                return false;
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            rollbackQuietly(conn);
            System.out.println("Loi khi duyet booking: " + e.getMessage());
            return false;

        } finally {
            closeConnectionQuietly(conn);
        }
    }

    public boolean assignSupportStaff(int bookingId, int supportStaffId) {
        if (bookingId <= 0 || supportStaffId <= 0) {
            System.out.println("bookingId va supportStaffId phai lon hon 0.");
            return false;
        }

        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) {
            System.out.println("Khong tim thay booking.");
            return false;
        }

        if (!STATUS_CONFIRMED.equalsIgnoreCase(booking.getStatus())) {
            System.out.println("Chi booking da duyet moi duoc phan cong support staff.");
            return false;
        }

        User supportStaff = userDAO.getUserById(supportStaffId);
        if (supportStaff == null || !"Support Staff".equalsIgnoreCase(supportStaff.getRole())) {
            System.out.println("Nguoi duoc phan cong khong phai Support Staff hop le.");
            return false;
        }

        return bookingDAO.assignSupportStaff(bookingId, supportStaffId);
    }

    public boolean rejectBooking(int bookingId) {
        if (bookingId <= 0) {
            System.out.println("bookingId phai lon hon 0.");
            return false;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Booking booking = bookingDAO.getBookingByIdForUpdate(bookingId, conn);
            if (booking == null) {
                conn.rollback();
                System.out.println("Khong tim thay booking.");
                return false;
            }

            if (!STATUS_PENDING.equalsIgnoreCase(booking.getStatus())) {
                conn.rollback();
                System.out.println("Chi booking dang PENDING moi duoc tu choi.");
                return false;
            }

            List<Integer> equipmentIds = bookingDetailDAO.getEquipmentIdsByBookingId(bookingId, conn);
            for (Integer equipmentId : equipmentIds) {
                Equipment equipment = equipmentDAO.getEquipmentById(equipmentId, conn);
                if (equipment == null) {
                    conn.rollback();
                    System.out.println("Khong tim thay thiet bi de hoan tra so luong.");
                    return false;
                }

                boolean updated = equipmentDAO.updateAvailableQuantity(
                        equipmentId,
                        equipment.getAvailableQuantity() + 1,
                        conn
                );

                if (!updated) {
                    conn.rollback();
                    System.out.println("Khong the hoan tra so luong thiet bi khi tu choi booking.");
                    return false;
                }
            }

            boolean rejected = bookingDAO.rejectBooking(bookingId, conn);
            if (!rejected) {
                conn.rollback();
                System.out.println("Tu choi booking that bai.");
                return false;
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            rollbackQuietly(conn);
            System.out.println("Loi khi tu choi booking: " + e.getMessage());
            return false;

        } finally {
            closeConnectionQuietly(conn);
        }
    }

    public boolean cancelPendingBooking(int bookingId, int userId) {
        if (bookingId <= 0 || userId <= 0) {
            System.out.println("bookingId va userId phai lon hon 0.");
            return false;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Booking booking = bookingDAO.getBookingByIdForUpdate(bookingId, conn);
            if (booking == null) {
                conn.rollback();
                System.out.println("Khong tim thay booking.");
                return false;
            }

            if (booking.getUserId() != userId) {
                conn.rollback();
                System.out.println("Ban chi duoc huy booking cua chinh minh.");
                return false;
            }

            if (!STATUS_PENDING.equalsIgnoreCase(booking.getStatus())) {
                conn.rollback();
                System.out.println("Chi booking dang PENDING moi duoc huy.");
                return false;
            }

            List<Integer> equipmentIds = bookingDetailDAO.getEquipmentIdsByBookingId(bookingId, conn);
            for (Integer equipmentId : equipmentIds) {
                Equipment equipment = equipmentDAO.getEquipmentById(equipmentId, conn);
                if (equipment == null) {
                    conn.rollback();
                    System.out.println("Khong tim thay thiet bi de hoan tra so luong.");
                    return false;
                }

                boolean updated = equipmentDAO.updateAvailableQuantity(
                        equipmentId,
                        equipment.getAvailableQuantity() + 1,
                        conn
                );

                if (!updated) {
                    conn.rollback();
                    System.out.println("Khong the hoan tra so luong thiet bi khi huy booking.");
                    return false;
                }
            }

            boolean cancelled = bookingDAO.cancelPendingBooking(bookingId, userId, conn);
            if (!cancelled) {
                conn.rollback();
                System.out.println("Huy booking that bai.");
                return false;
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            rollbackQuietly(conn);
            System.out.println("Loi khi huy booking: " + e.getMessage());
            return false;

        } finally {
            closeConnectionQuietly(conn);
        }
    }

    public boolean updatePreparationStatus(int bookingId, int supportStaffId, String preparationStatus) {
        if (bookingId <= 0 || supportStaffId <= 0) {
            System.out.println("bookingId va supportStaffId phai lon hon 0.");
            return false;
        }

        if (!isValidPreparationStatus(preparationStatus)) {
            System.out.println("Trang thai chuan bi khong hop le.");
            return false;
        }

        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) {
            System.out.println("Khong tim thay booking.");
            return false;
        }

        if (!STATUS_CONFIRMED.equalsIgnoreCase(booking.getStatus())) {
            System.out.println("Chi booking da duoc duyet moi cap nhat trang thai chuan bi.");
            return false;
        }

        if (booking.getSupportStaffId() == null || booking.getSupportStaffId().intValue() != supportStaffId) {
            System.out.println("Ban khong duoc phan cong cho booking nay.");
            return false;
        }

        return bookingDAO.updatePreparationStatus(bookingId, supportStaffId, preparationStatus);
    }

    public boolean isValidPreparationStatus(String preparationStatus) {
        return PREPARATION_PREPARING.equalsIgnoreCase(preparationStatus)
                || PREPARATION_READY.equalsIgnoreCase(preparationStatus)
                || PREPARATION_MISSING_EQUIPMENT.equalsIgnoreCase(preparationStatus);
    }

    private List<Integer> normalizeEquipmentIds(List<Integer> equipmentIds) {
        List<Integer> result = new ArrayList<Integer>();

        if (equipmentIds == null || equipmentIds.isEmpty()) {
            return result;
        }

        Set<Integer> uniqueIds = new LinkedHashSet<Integer>();
        for (Integer equipmentId : equipmentIds) {
            if (equipmentId != null && equipmentId > 0) {
                uniqueIds.add(equipmentId);
            }
        }

        result.addAll(uniqueIds);
        return result;
    }

    private boolean isValidDateTime(String dateTime) {
        try {
            LocalDateTime.parse(dateTime, FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isStartBeforeEnd(String startTime, String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime, FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endTime, FORMATTER);
        return start.isBefore(end);
    }

    private boolean isStartTimeInFuture(String startTime) {
        LocalDateTime start = LocalDateTime.parse(startTime, FORMATTER);
        return start.isAfter(LocalDateTime.now());
    }

    private void rollbackQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (Exception e) {
            System.out.println("Loi rollback: " + e.getMessage());
        }
    }

    private void closeConnectionQuietly(Connection conn) {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (Exception e) {
            System.out.println("Loi dong ket noi: " + e.getMessage());
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}