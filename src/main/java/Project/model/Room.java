package Project.model;

public class Room {
    private int roomId;
    private String roomName;
    private int capacity;
    private String location;
    private String fixedEquipment;
    private String createdAt;
    private String updatedAt;

    public Room() {
    }

    public Room(int roomId, String roomName, int capacity, String location, String fixedEquipment, String createdAt, String updatedAt) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
        this.location = location;
        this.fixedEquipment = fixedEquipment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFixedEquipment() {
        return fixedEquipment;
    }

    public void setFixedEquipment(String fixedEquipment) {
        this.fixedEquipment = fixedEquipment;
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
        return "Room{" +
                "roomId=" + roomId +
                ", roomName='" + roomName + '\'' +
                ", capacity=" + capacity +
                ", location='" + location + '\'' +
                ", fixedEquipment='" + fixedEquipment + '\'' +
                '}';
    }
}