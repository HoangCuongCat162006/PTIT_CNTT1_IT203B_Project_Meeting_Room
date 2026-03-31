package Project.model;

public class Booking {
    private int bookingId;
    private int userId;
    private int roomId;
    private int participantCount;
    private String bookingTime;
    private String startTime;
    private String endTime;
    private Integer serviceId;
    private Integer equipmentId;
    private String status;
    private Integer supportStaffId;
    private String preparationStatus;
    private String createdAt;
    private String updatedAt;

    public Booking() {
    }

    public Booking(int bookingId, int userId, int roomId, int participantCount, String bookingTime,
                   String startTime, String endTime, Integer serviceId, Integer equipmentId,
                   String status, Integer supportStaffId, String preparationStatus,
                   String createdAt, String updatedAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.roomId = roomId;
        this.participantCount = participantCount;
        this.bookingTime = bookingTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.serviceId = serviceId;
        this.equipmentId = equipmentId;
        this.status = status;
        this.supportStaffId = supportStaffId;
        this.preparationStatus = preparationStatus;
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

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public Integer getSupportStaffId() {
        return supportStaffId;
    }

    public void setSupportStaffId(Integer supportStaffId) {
        this.supportStaffId = supportStaffId;
    }

    public String getPreparationStatus() {
        return preparationStatus;
    }

    public void setPreparationStatus(String preparationStatus) {
        this.preparationStatus = preparationStatus;
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

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", roomId=" + roomId +
                ", participantCount=" + participantCount +
                ", bookingTime='" + bookingTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", status='" + status + '\'' +
                ", supportStaffId=" + supportStaffId +
                ", preparationStatus='" + preparationStatus + '\'' +
                '}';
    }
}