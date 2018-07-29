package hotel;

import java.time.LocalDate;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;


/**
 *
 * @author Darren
 */
public class GuestDialog extends Dialog {
    private final Label firstNameLabel = new Label("First Name");
    private final Label lastNameLabel = new Label("Last Name");
    private final Label roomNumberLabel = new Label("Room Number");
    private final Label nightsLabel = new Label("Number of Nights");
    private final Label resDateLabel = new Label("Reservation Date");
    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final TextField roomNumber = new TextField();
    private final TextField nights = new TextField();
    private final DatePicker reservationDate = new DatePicker(LocalDate.now());
    private Stage dialog = new Stage();
    private final double offset = 10.0;
    private boolean isEdit = false;
    private String errorMsg = "";
    private Person currentGuest;
    Hotel hotel;
    
    public GuestDialog(String title, Hotel hotel, boolean isEdit, Person currentGuest) {
        this.hotel = hotel;
        this.isEdit = isEdit;
        this.currentGuest = currentGuest;
        
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);

        // Set padding on labels
        lastNameLabel.setPadding(new Insets(offset, 0.0, 0.0, 0.0));
        roomNumberLabel.setPadding(new Insets(offset, 0.0, 0.0, 0.0));
        nightsLabel.setPadding(new Insets(offset, 0.0, 0.0, 0.0));
        resDateLabel.setPadding(new Insets(offset, 0.0, 0.0, 0.0));
        
        Button primaryButton;
        Button cancel = new Button("Cancel");
        
        if (isEdit) {
            primaryButton = new Button("Save");
            firstName.setText(currentGuest.getFirstName());
            lastName.setText(currentGuest.getLastName());
            roomNumber.setText(String.valueOf(currentGuest.getRoomNumber()));
            nights.setText(String.valueOf(currentGuest.getNumOfNights()));
            reservationDate.setValue(LocalDate.parse(currentGuest.getReservationDate()));
        }
        else {
            primaryButton = new Button("Add");
        }
        
        
        // Click event handler for primary button
        primaryButton.setOnAction((ActionEvent) -> {
            if(isValid()) {
                Person person = new Person(firstName.getText(), lastName.getText(), Integer.parseInt(roomNumber.getText()), Integer.parseInt(nights.getText()), reservationDate.getValue().toString());
                if (isEdit) {
                    hotel.editGuest(currentGuest, person);
                }
                else {
                    hotel.addGuest(person);
                }
                dialog.close();
            }
            else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setContentText(errorMsg);
                alert.show();
            }
            
        });
        
        
        // Click event handler for cancel button
        cancel.setOnAction((ActionEvent) -> {
            dialog.close();
        });
        
        
        VBox formElementsContainer = new VBox();
        formElementsContainer.getChildren().addAll(firstNameLabel, firstName, lastNameLabel, lastName, roomNumberLabel, roomNumber, nightsLabel, nights, resDateLabel, reservationDate);
        
        HBox primaryBtnContainer = new HBox();
        primaryBtnContainer.setPadding(new Insets(0.0, 0.0, 0.0, 5.0));
        primaryBtnContainer.getChildren().add(primaryButton);
        
        HBox buttonsContainer = new HBox();
        buttonsContainer.setPadding(new Insets(10.0, 0.0, 0.0, 0.0));
        buttonsContainer.getChildren().addAll(cancel, primaryBtnContainer);
        buttonsContainer.setAlignment(Pos.BOTTOM_RIGHT);
        
        // Secondary container
        VBox container = new VBox();
        container.setPadding(new Insets(5.0, 10.0, 0.0, 10.0));
        container.getChildren().addAll(formElementsContainer, buttonsContainer);
        
        // Root container
        Scene scene = new Scene(container, 300, 300);
        dialog.setScene(scene);
    }
    
    public void display() { dialog.show(); }
    
    
    private boolean isValid() {
        errorMsg = "";
        // First Name Validation
        if(!firstName.getText().isEmpty()) {
            if(!firstName.getText().chars().allMatch(Character::isLetter)) {
                errorMsg += "First name should only contain letters\n";
            }
        }
        else if(firstName.getText().trim().split(" ").length > 1) {
            errorMsg += "First name should only have one word\n";
        }
        else {
            errorMsg += "First name must have a value\n";
        }
        // Last Name Validation
        if(!lastName.getText().isEmpty()) {
            if(!lastName.getText().chars().allMatch(Character::isLetter)) {
                errorMsg += "Last name should only container letters\n";
            }
        }
        else if(lastName.getText().trim().split(" ").length > 1) {
            errorMsg += "Last name should only have one word\n";
        }
        else {
            errorMsg += "Last name must have a value\n";
        }
        // Room Number Validation
        if(roomNumber.getText().chars().allMatch(Character::isDigit) && (roomNumber.getText().length() == 3)) {
            if(!roomIsAvailable(Integer.parseInt(roomNumber.getText()))) {
                if (isEdit) {
                    if (Integer.parseInt(roomNumber.getText()) != currentGuest.getRoomNumber()) {
                        errorMsg += "The room number entered is not available\n";
                    }
                }
                else {
                    errorMsg += "The room number entered is not available\n";
                }
            }
        }
        else if(roomNumber.getText().trim().split(" ").length > 1) {
            errorMsg += "Only one number should be entered for the room number\n";
        }
        else {
            errorMsg += "Room number must be a 3 digit number\n";
        }
        // Number of Nights Validation
        if(!nights.getText().isEmpty()) {
            if(!nights.getText().chars().allMatch(Character::isDigit)) {
                errorMsg += "The number of nights should only be a number\n";
            }
            else {
                if(Integer.parseInt(nights.getText()) <= 0) { errorMsg += "The number of nights should be at least 1\n"; }
            }
        }
        else if(nights.getText().trim().split(" ").length > 1) {
            errorMsg += "Only one number should be entered for the number of nights\n";
        }
        else {
            errorMsg += "The number of nights must have a value\n";
        }
        // Reservation Date Validation
        if (isEdit) {
            if (reservationDate.getValue().compareTo(LocalDate.now()) < 0) {
                if (!reservationDate.getValue().equals(LocalDate.parse(currentGuest.getReservationDate()))) {
                    errorMsg += "Reservation date must be in the future\n";
                }
            }
        }
        else {
            if (reservationDate.getValue().compareTo(LocalDate.now()) < 0) {
                errorMsg += "Reservation date must be in the future\n";
            }
        }
        return errorMsg.equals("");
    }
    
    private boolean roomIsAvailable(int roomNumber) {
        ObservableList<HotelRoom> list = hotel.makeSQLQuery("SELECT * FROM rooms WHERE room_number=" + roomNumber + " AND room_status='Available'", true);
        return !list.isEmpty();
    }
    
    
}
