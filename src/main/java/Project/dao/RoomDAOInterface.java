package Project.dao;

import Project.model.Room;

import java.util.List;

public interface RoomDAOInterface {
    boolean addRoom(Room room);

    boolean updateRoom(Room room);

    boolean deleteRoom(int roomId);

    Room getRoomById(int roomId);

    List<Room> getAllRooms();

    List<Room> getAvailableRooms(String startTime, String endTime);

    List<Room> searchRoomsByName(String keyword);

    boolean roomNameExists(String roomName);

    boolean roomNameExistsExceptId(String roomName, int roomId);

    boolean hasBookingReference(int roomId);
}