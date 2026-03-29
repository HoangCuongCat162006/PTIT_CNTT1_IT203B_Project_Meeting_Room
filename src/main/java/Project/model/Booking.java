package Project.model;

public class Booking {
    private int bookingId;
    private int userId;
    private int roomId;
    private String bookingTime;
    private Integer serviceId;
    private Integer equipmentId;
    private String status;
    private String createdAt;
    private String updatedAt;

    public Booking() {
    }

    public Booking(int bookingId, int userId, int roomId, String bookingTime, Integer serviceId, Integer equipmentId, String status, String createdAt, String updatedAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.roomId = roomId;
        this.bookingTime = bookingTime;
        this.serviceId = serviceId;
        this.equipmentId = equipmentId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}