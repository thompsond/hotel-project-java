

package hotel;

/**
 *
 * @author Darren
 */
public class HotelRoom {
    private final int roomNumber;
    private final double price;
    private final int numberOfBeds;
    private String roomStatus;
    private final RoomType roomType;
    
    public HotelRoom(int roomNumber, double price, int numberOfBeds, String roomStatus, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.price = price;
        this.numberOfBeds = numberOfBeds;
        this.roomStatus = roomStatus;
        this.roomType = roomType;
    }
    
    public enum RoomType {
        ECONO,
        SUITE
    }
    
    public int getRoomNumber() { return roomNumber; }
    public double getRoomPrice() { return price; }
    public int getNumberOfBeds() { return numberOfBeds; }
    public String getRoomStatus() { return roomStatus; }
    public RoomType getRoomType() { return roomType; }
    

}
