

package hotel;

/**
 *
 * @author Darren
 */
public class Person {
    private String firstName;
    private String lastName;
    private int roomNumber;
    private int numOfNights;
    private String reservationDate;
    
    public Person(String firstName, String lastName, int roomNumber, int numOfNights, String reservationDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.roomNumber = roomNumber;
        this.numOfNights = numOfNights;
        this.reservationDate = reservationDate;
    }
    
    @Override
    public boolean equals(Object obj) {
        Person p = (Person)obj;
        return this.firstName.equals(p.getFirstName()) && this.lastName.equals(p.getLastName()) && this.roomNumber == p.getRoomNumber() && this.numOfNights == p.getNumOfNights() && this.reservationDate.equals(p.getReservationDate());
    }
    
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getRoomNumber() { return roomNumber; }
    public int getNumOfNights() { return numOfNights; }
    public String getReservationDate() { return reservationDate; }
    
    public void setFirstName(String fName) { firstName = fName; }
    public void setLastName(String lName) { lastName = lName; }
    public void setRoomNumber(int rNum) { roomNumber = rNum; }
    public void setNumOfNights(int num) { numOfNights = num; }
    public void setReservationDate(String rDate) { reservationDate = rDate; }
    
}
