package hotel;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Font;

/**
 *
 * @author Darren
 */
public class Hotel extends Application {
    private final TableView<HotelRoom> roomTable = new TableView<>();
    private final TableView<Person> guestsTable = new TableView<>();
    private ObservableList<HotelRoom> roomObservableList;
    private ObservableList<Person> guestsObservableList;
    private static final String URL = "jdbc:mysql://localhost:3306/hotel?useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Sodacan5100";
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        roomObservableList = makeSQLQuery("SELECT * FROM rooms", true);
        guestsObservableList = makeGuestSQLQuery("SELECT * FROM guests", true);
        double columnWidth = 80.0;
        double columnHeight = 40.0;
        String textPosition = "-fx-alignment: CENTER;";
        
        // Room Label
        Label roomTableTitle = new Label();
        roomTableTitle.setText("Rooms");
        roomTableTitle.setFont(new Font("Arial", 18));
        roomTableTitle.setTranslateX(10.0);
        roomTableTitle.setPadding(new Insets(10.0, 0, 10.0, 0));
        
        /***** Room Table Definition - Start *****/
        
        // Room Number Column
        TableColumn roomNumberCol = new TableColumn("Room Number");
        roomNumberCol.setMinWidth(columnWidth);
        roomNumberCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomNumberCol.setStyle(textPosition);
        
        // Price Column
        TableColumn priceCol = new TableColumn("Price");
        priceCol.setMinWidth(columnWidth);
        priceCol.setCellValueFactory(new PropertyValueFactory<>("roomPrice"));
        priceCol.setStyle(textPosition);
        
        // Number of Beds Column
        TableColumn numOfBedsCol = new TableColumn("Number of Beds");
        numOfBedsCol.setMinWidth(columnWidth);
        numOfBedsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfBeds"));
        numOfBedsCol.setStyle(textPosition);
        
        // Room Status Column
        TableColumn roomStatusCol = new TableColumn("Room Status");
        roomStatusCol.setMinWidth(columnWidth);
        roomStatusCol.setCellValueFactory(new PropertyValueFactory<>("roomStatus"));
        roomStatusCol.setStyle(textPosition);
        
        // Room Type Column
        TableColumn roomTypeCol = new TableColumn("Room Type");
        roomTypeCol.setMinWidth(columnWidth);
        roomTypeCol.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        roomTypeCol.setStyle(textPosition);
        
        
        roomTable.setEditable(false);
        roomTable.setItems(roomObservableList);
        roomTable.getColumns().addAll(roomNumberCol, priceCol, numOfBedsCol, roomStatusCol, roomTypeCol);
        roomTable.fixedCellSizeProperty().set(columnHeight);
        roomTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        roomTable.getSelectionModel().clearSelection();
        
        /***** Room Table Definition - End *****/
        
        
        // Button to add new guests
        Button addGuestBtn = new Button("Add Guest");
        //        addGuestBtn.setPadding(new Insets(5.0, 0.0, 5.0, 0.0));
        addGuestBtn.setOnAction((ActionEvent event) -> {
            GuestDialog window = new GuestDialog("Add Guest", this, false, null);
            window.display();
        });
        
        
        // Button to edit an existing guest's information
        Button editGuestBtn = new Button("Edit Guest");
        editGuestBtn.setTranslateX(10.0);
        editGuestBtn.setOnAction((ActionEvent event) -> {
            if (!guestsTable.getSelectionModel().isEmpty()) {
                int index = guestsTable.getSelectionModel().getSelectedIndex();
                Person currentGuest = guestsTable.getSelectionModel().getTableView().getItems().get(index);
                GuestDialog window = new GuestDialog("Edit Guest", this, true, currentGuest);
                window.display();
            }
            else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("No Guest Selected");
                alert.setContentText("Please select a guest");
                alert.show();
            }
        });

        
        // Guest Label
        Label guestTableTitle = new Label();
        guestTableTitle.setText("Guests");
        guestTableTitle.setFont(new Font("Arial", 18));
        guestTableTitle.setPadding(new Insets(0.0, 20.0, 0.0, 0.0));
        
        
        
        /***** Guest Table Definition - Start *****/
        TableColumn firstNameCol = new TableColumn("First Name");
        firstNameCol.setMinWidth(columnWidth);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameCol.setStyle(textPosition);
        
        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setMinWidth(columnWidth);
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        lastNameCol.setStyle(textPosition);
        
        TableColumn roomCol = new TableColumn("Room Number");
        roomCol.setMinWidth(columnWidth);
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomCol.setStyle(textPosition);
        
        TableColumn nightsCol = new TableColumn("Number of Nights");
        nightsCol.setMinWidth(columnWidth);
        nightsCol.setCellValueFactory(new PropertyValueFactory<>("numOfNights"));
        nightsCol.setStyle(textPosition);
        
        TableColumn resDateCol = new TableColumn("Reservation Date");
        resDateCol.setMinWidth(columnWidth);
        resDateCol.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        resDateCol.setStyle(textPosition);
        
        // Checkout Column
        TableColumn checkoutCol = new TableColumn("");
        checkoutCol.setMinWidth(columnWidth);
        checkoutCol.setStyle(textPosition);
        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory = 
                (final TableColumn<Person, String> param) -> {
                    final TableCell<Person, String> cell = new TableCell<Person, String>() {
                        final Button btn = new Button("Checkout");
                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if(empty) {
                                setGraphic(null);
                                setText(null);
                            }
                            else {
                                setGraphic(btn);
                                setText(null); 
                            }
                            btn.setOnAction((ActionEvent event) -> {
                                // Get the current person in the list
                                Person person = getTableView().getItems().get(getIndex());
                                int roomNumber = person.getRoomNumber();
                                guestsTable.getSelectionModel().select(getIndex());
                                String query = "UPDATE rooms SET room_status='Available' WHERE room_number='" +
                                        person.getRoomNumber() + "'";
                                makeSQLQuery(query, false);
                                query = "DELETE FROM guests WHERE room_number='" + roomNumber + "'";
                                makeGuestSQLQuery(query, false);
                                roomObservableList = makeSQLQuery("SELECT * FROM rooms", true);
                                guestsObservableList = makeGuestSQLQuery("SELECT * FROM guests", true);
                                updateTables();
                            });
                        }
                    };
                    return cell;
        };
        checkoutCol.setCellFactory(cellFactory);

        guestsTable.setEditable(false);
        guestsTable.setItems(guestsObservableList);
        guestsTable.getColumns().addAll(firstNameCol, lastNameCol, roomCol, nightsCol, resDateCol, checkoutCol);
        guestsTable.fixedCellSizeProperty().set(columnHeight);
        guestsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        guestsTable.getSelectionModel().clearSelection();
        
        /***** Guest Table Definition - End *****/
        
        // Content Container
        StackPane root = new StackPane();
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10.0, 0.0, 10.0, 10.0));
        hbox.getChildren().addAll(guestTableTitle,addGuestBtn, editGuestBtn);
        vbox.getChildren().addAll(roomTableTitle, roomTable, hbox, guestsTable);
        root.getChildren().add(vbox);
        
        // Main Container
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Hotel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Makes a SQL query to the hotel rooms table
     * @param query
     * The SQL query to be used
     * @param isSelect
     * Determines whether the SQL query is a select query or another type
     * @return Returns an ObservableList of the hotel room after the query
     */
    public ObservableList<HotelRoom> makeSQLQuery(String query, boolean isSelect) {
        ObservableList<HotelRoom> rooms = null;
        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Statement st = connection.createStatement();
            ResultSet rs;
            if(isSelect) { rs = st.executeQuery(query); }
            else { st.executeUpdate(query); return rooms; }
            rooms = FXCollections.observableArrayList();
            while(rs.next()) {
                int roomNumber = rs.getInt("room_number");
                double price = rs.getDouble("price");
                int numOfBeds = rs.getInt("number_of_beds");
                String roomStatus = rs.getString("room_status");
                String roomType = rs.getString("room_type");
                HotelRoom room = new HotelRoom(roomNumber, price, numOfBeds, roomStatus, HotelRoom.RoomType.valueOf(roomType));
                rooms.add(room);
            }
        }
        catch(SQLException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Cannot connect to database");
            alert.show();
        }
        return rooms;
    }
    
    /**
     * Makes a SQL query to the hotel guests table
     * @param query
     * The SQL query to be used
     * @param isSelect
     * Determines whether the SQL query is a select query or another type
     * @return Returns an ObservableList of the hotel guests after the query
     */
    public ObservableList<Person> makeGuestSQLQuery(String query, boolean isSelect) {
        ObservableList<Person> guests = null;
        try(Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            Statement st = connection.createStatement();
            ResultSet rs;
            if(isSelect) { rs = st.executeQuery(query); }
            else { st.executeUpdate(query); return guests; }
            guests = FXCollections.observableArrayList();
            while(rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int roomNumber = rs.getInt("room_number");
                int numberOfNights = rs.getInt("number_of_nights");
                String reservationDate = rs.getString("reservation_date");
                Person person = new Person(firstName, lastName, roomNumber, numberOfNights, reservationDate);
                guests.add(person);
            }
        }
        catch(SQLException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Connection Error");
            alert.setContentText("Cannot connect to database");
        }
        return guests;
    }
    
    
    public void updateTables() {
        roomTable.setItems(roomObservableList);
        roomTable.refresh();
        guestsTable.setItems(guestsObservableList);
        guestsTable.refresh();
    }
    
    public void addGuest(Person p) {
        // Add the guest to the database
        String query = String.format("INSERT INTO guests (first_name, last_name, room_number, number_of_nights, reservation_date) VALUES (\"%s\", \"%s\", %s, %s, \"%s\")", p.getFirstName(), p.getLastName(), String.valueOf(p.getRoomNumber()), String.valueOf(p.getNumOfNights()), p.getReservationDate());
        makeGuestSQLQuery(query, false);
        // Update the room status in the database
        query = "UPDATE rooms SET room_status='Booked' WHERE room_number=" + p.getRoomNumber();
        makeSQLQuery(query, false);
        roomObservableList = makeSQLQuery("SELECT * FROM rooms", true);
        guestsObservableList = makeGuestSQLQuery("SELECT * FROM guests", true);
        updateTables();
    }
    
    public void editGuest(Person currentGuest, Person editedGuest) {
        // Update the guest information in the database
        if (!currentGuest.equals(editedGuest)) {
            String query = String.format("UPDATE guests SET first_name = \"%s\", last_name = \"%s\", room_number = %d, number_of_nights = %d, reservation_date = \"%s\" WHERE room_number = %d", editedGuest.getFirstName(), editedGuest.getLastName(), editedGuest.getRoomNumber(), editedGuest.getNumOfNights(), editedGuest.getReservationDate(), currentGuest.getRoomNumber());
            makeGuestSQLQuery(query, false);
            // if room was changed, make current room available and new room unavailable
            if (currentGuest.getRoomNumber() != editedGuest.getRoomNumber()) {
                query = String.format("UPDATE rooms SET room_status='Available' WHERE room_number = %d", currentGuest.getRoomNumber());
                makeSQLQuery(query, false);
                query = String.format("UPDATE rooms SET room_status='Booked' WHERE room_number = %d", editedGuest.getRoomNumber());
                makeSQLQuery(query, false);
            }
            roomObservableList = makeSQLQuery("SELECT * FROM rooms", true);
            guestsObservableList = makeGuestSQLQuery("SELECT * FROM guests", true);
            updateTables();
        }
        
    }

    public static void main(String[] args) {
        launch(args);
    } 
}