/**
 * Colossal Airlines Final Project
 * 
 * Notes: This is not the best code I have ever written
 *          There is a lot of repeating code and it is probably 
 *              twice as long as it needs to be, but it works 
 *                  and I can always return and improve it.
 * 
 * @author Thomas Scanlan 
 * @version 8/20/14
 */

import java.io.*;
import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;


public class ColossalAirlines extends Application {

    // the five flight objects contain an array of 10 passenger objects
    Flight flight100 = new Flight("100");
    Flight flight200 = new Flight("200");
    Flight flight300 = new Flight("300");
    Flight flight400 = new Flight("400");
    Flight flight500 = new Flight("500");
    
    public static void main (String [] args) {
            launch (args); 
    }
    
    /**
     * Opens the binary file if it exists, and then reads the contents into
     * my five flight objects, then closes the stream.
     */
    public void readBookingInformation()  {
                    
        File bookings = new File("Bookings.dat");
        if (bookings.exists()) {
            try {
               FileInputStream fisInput;
               fisInput = new FileInputStream(bookings);
               ObjectInputStream oi = new ObjectInputStream(fisInput);
               flight100 = (Flight)oi.readObject();
               flight200 = (Flight)oi.readObject();
               flight300 = (Flight)oi.readObject();
               flight400 = (Flight)oi.readObject();               
               flight500 = (Flight)oi.readObject();
               oi.close();
            }
            catch (ClassNotFoundException badClass) {
                badClass.printStackTrace();
            }
            catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
    }
    
    
    /**
     * readBookingInformation() is actually invoked from the start method
     * @param mainStage 
     */
    @Override
    public void start (Stage mainStage) {
        
        readBookingInformation();
        mainStage.setTitle("Colossus Airlines");
        
        BorderPane rootNode = new BorderPane();
        Scene mainScene = new Scene (rootNode, 900, 600);
        mainStage.setScene(mainScene);
        
        // Create the menu that will provide all available options
        // to the user
        MenuBar myMenuBar = createUserMenu();
        
        ImageView seeImage = new ImageView ( new Image ("Airplane.jpg"));
        rootNode.setTop(myMenuBar);
        myMenuBar.setStyle("-fx-background-color: lightskyblue");
        rootNode.setCenter(seeImage);
        rootNode.setStyle("-fx-base: white");
        
        mainStage.show();
    }
    
    /**
     * Creates the MenuBar and returns it to the start method.
     * @return MenuBar
     */
    MenuBar createUserMenu() {
           
        MenuBar myMenuBar = new MenuBar();
        Menu mainMenu = new Menu("Main Menu");
        mainMenu.setStyle("-fx-border-color: steelblue");
        // create the reservation menu with accompanying event handlers
        Menu reservationMenu = createReservationMenu();
        
        MenuItem cancelRes = new MenuItem("Cancel Reservation");
        cancelRes.setAccelerator(KeyCombination.keyCombination("shortcut+C"));
        cancelRes.setOnAction((ActionEvent cancelBooking) -> {
                cancelReservation();
        });
        
        MenuItem singleSeat = new MenuItem("Display A Seat Assignment");
        singleSeat.setAccelerator(KeyCombination.keyCombination("shortcut+D"));
        singleSeat.setOnAction((ActionEvent getSeat) -> {
                displaySeatAssignmentStage();
        });
        
        MenuItem allSeats = new MenuItem("Display All Seat Assignments");
        allSeats.setAccelerator(KeyCombination.keyCombination("shortcut+A"));
        allSeats.setOnAction((ActionEvent display)-> {
            displayAllSeats();
        });
        // create the boarding pass menu with accompanying event handlers
        Menu boardingPassMenu = createBoardingPassMenu();
        
        MenuItem quitMenu = new MenuItem("Quit");
        quitMenu.setAccelerator(KeyCombination.keyCombination("shortcut+Q"));
        quitMenu.setOnAction((ActionEvent quit) -> {
                quitMenu();
        });
        
        // add the items to my main bar
        mainMenu.getItems().addAll(reservationMenu, cancelRes, singleSeat,
                allSeats, boardingPassMenu, new SeparatorMenuItem(), quitMenu);
       
        // add the mainbar to my menuBar
        myMenuBar.getMenus().add(mainMenu);
        
        return myMenuBar;
    }
    
    /**
     * Pops up a window asking you if you are sure you want to quit
     */
    void quitMenu() {
        
        Stage quitStage = new Stage();
        quitStage.setTitle("Quitting");
        
        BorderPane quitPane = new BorderPane();
        quitPane.setPadding(new Insets(11, 12, 13, 14));
        
        Label quitMessage = new Label("Are you sure you want to quit?");
        quitMessage.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
        quitMessage.setAlignment(Pos.CENTER);
        quitPane.setCenter(quitMessage);
         
        
        ToggleButton yesButton = new ToggleButton("Yes");
        yesButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));
        yesButton.setStyle("-fx-base: lightcoral");
        yesButton.setAlignment(Pos.CENTER);
        
        yesButton.setOnAction((ActionEvent closeProgram) -> {
            Platform.exit();
        });
        
        ToggleButton noButton = new ToggleButton("No");
        noButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));
        noButton.setStyle("-fx-base: palegreen");
        noButton.setAlignment(Pos.CENTER);
        
        noButton.setOnAction((ActionEvent continueProgram) -> {
                quitStage.close();
        });
        
        HBox holdButtonsBox = new HBox(5);
        holdButtonsBox.setAlignment(Pos.TOP_CENTER);
        holdButtonsBox.getChildren().addAll(yesButton, noButton);
        
        quitPane.setBottom(holdButtonsBox);
        
        Scene quitScene = new Scene(quitPane);
        quitStage.setScene(quitScene);
        
        quitStage.show();
    }
    
    /**
     * Pops up a window where can you can search by name or by seat number
     * for a passenger on any of the flights, the event handlers 
     * handle NullPointerExceptions and deal with issues if the user left 
     * fields blank
     */
    void displaySeatAssignmentStage() {
        
        Stage getSeatStage = new Stage();
        getSeatStage.setTitle("Display Seat Assignment");
        
        GridPane getSeatPane = new GridPane();
        getSeatPane.setAlignment(Pos.CENTER);
        getSeatPane.setPadding(new Insets(12.5, 12.5, 13.5, 14.5));
        getSeatPane.setStyle("-fx-base: plum");
        getSeatPane.setHgap(5.5);
        getSeatPane.setVgap(5.5);
        
        ToggleGroup tgGroup = new ToggleGroup();
        RadioButton rbName = new RadioButton("Find by Name");
        rbName.setToggleGroup(tgGroup);
        RadioButton rbSeat = new RadioButton("Find by Seat");
        rbSeat.setToggleGroup(tgGroup);
        getSeatPane.add(rbName, 0, 0);
        getSeatPane.add(rbSeat, 1, 0);
        
        getSeatPane.add(new Label("First Name:"), 0, 1);
        TextField firstName = new TextField();
        firstName.setEditable(false);
        getSeatPane.add(firstName, 1, 1);
        
        getSeatPane.add(new Label("Last Name:"), 0, 2);
        TextField lastName = new TextField();
        lastName.setEditable(false);
        getSeatPane.add(lastName, 1, 2);
                
        getSeatPane.add(new Label("Seat#"), 0, 3);
        ComboBox<String> seatBox = createSeatSelection();
        seatBox.setDisable(true);
        getSeatPane.add(seatBox, 1, 3);
        
        rbName.setOnAction((ActionEvent nameButton) -> {
                seatBox.setDisable(true);
                firstName.setEditable(true);
                lastName.setEditable(true);
        });
        
        rbSeat.setOnAction((ActionEvent seatButton) -> {
                seatBox.setDisable(false);
                firstName.clear();
                firstName.setEditable(false);
                lastName.clear();
                lastName.setEditable(false);
        });

        getSeatPane.add(new Label("Flight#:"), 0, 4);
        ComboBox<String> cbFlight = createFlightComboBox();
        getSeatPane.add(cbFlight, 1, 4);

        ToggleButton submitButton = new ToggleButton("Submit");    
        getSeatPane.add(submitButton, 1, 5);
        getSeatPane.setHalignment(submitButton, HPos.CENTER);
        
        submitButton.setOnAction((ActionEvent e) -> {
           try { 
                String userFlight = cbFlight.getValue();
                Passenger tempPassenger = null;
                switch (userFlight) {
                    case "100":                        
                        if (seatBox.isDisabled()) {
                            String first = firstName.getText();
                            String last = lastName.getText();
                            tempPassenger = flight100.getPassenger(first, last);
                        }
                        else {
                            int userSeat = Integer.parseInt(seatBox.getValue());
                            tempPassenger = flight100.getPassenger(userSeat);
                        }
                        break;
                    case "200":
                        if (seatBox.isDisabled()) {
                            String first = firstName.getText();
                            String last = lastName.getText();
                            tempPassenger = flight200.getPassenger(first, last);
                        }
                        else {
                                int userSeat = Integer.parseInt(seatBox.getValue());
                                tempPassenger = flight200.getPassenger(userSeat);
                        }   
                        break;
                    case "300":
                        if (seatBox.isDisabled()) {
                            String first = firstName.getText();
                            String last = lastName.getText();
                            tempPassenger = flight300.getPassenger(first, last);
                        }
                        else {
                            int userSeat = Integer.parseInt(seatBox.getValue());
                            tempPassenger = flight300.getPassenger(userSeat);
                        }
                        break;
                    case "400":
                        if (seatBox.isDisabled()) {
                            String first = firstName.getText();
                            String last = lastName.getText();
                            tempPassenger = flight400.getPassenger(first, last);
                        }
                        else {
                            int userSeat = Integer.parseInt(seatBox.getValue());
                            tempPassenger = flight400.getPassenger(userSeat);
                        }
                        break;
                    case "500":
                        if (seatBox.isDisabled()) {
                            String first = firstName.getText();
                            String last = lastName.getText();
                            tempPassenger = flight500.getPassenger(first, last);
                        }
                        else {
                            int userSeat = Integer.parseInt(seatBox.getValue());
                            tempPassenger = flight500.getPassenger(userSeat);
                        }
                        break;
                    default : fieldsLeftEmptyMessage();
                }
                displayTheSeat(tempPassenger);
                firstName.clear();
                lastName.clear();
           }
           catch(Exception ex) {
               fieldsLeftEmptyMessage();
           }
        });
        
        Scene getSeatScene = new Scene(getSeatPane, 450, 300);
        getSeatStage.setScene(getSeatScene);
        getSeatStage.show();
    }
    
    /**
     * Displays the seat found by the 
     * @param passenger 
     */
    void displayTheSeat(Passenger passenger) {
        
        Stage displayStage = new Stage();
        BorderPane displayPane = new BorderPane();
        displayPane.setPadding(new Insets(11, 12, 13, 14));
        Label message = new Label();
        if (passenger == null) {
            // error message
            displayStage.setTitle("Display Seat Assignment Error");
            message.setText("   Requested Reservation could"
                    + "\n\t\tnot be found.");
            displayPane.setStyle("-fx-background-color: lightcoral");
        }
        else {
            displayStage.setTitle("Display Seat Assignment");
            Label passengerInfo = new Label("Passenger Information:");
            displayPane.setTop(passengerInfo);
            passengerInfo.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 24));
            
            message.setText("First Name: " + passenger.getFirstName() 
                        +"\nLast Name: " + passenger.getLastName() 
                        + "\nSeat#: " + passenger.getSeatNumber() 
                        + "\nFlight#: " + passenger.getFlightNum());    
            displayPane.setStyle("-fx-background-color: palegreen");
        }
        displayPane.setCenter(message);
        message.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
        message.setAlignment(Pos.CENTER);
        
        
        Scene displayScene = new Scene(displayPane, 400, 275);
        displayStage.setScene(displayScene);
        displayStage.show();
    }
    
    /**
     * Creates and returns the combo box that contains seats 1-10.
     * @return ComboBox 
     */
    ComboBox<String> createSeatSelection() {
        ComboBox<String> seatSelectionBox = new ComboBox<>();
        seatSelectionBox.setPrefWidth(200);
        String [] seatArray = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        ObservableList<String> allSeats;
        allSeats = FXCollections.observableArrayList(seatArray);
        seatSelectionBox.getItems().addAll(allSeats);
        return seatSelectionBox;
    }
    
    /**
     * Although not technically required I decided to add a search by seat
     * when the user wants to cancel reservations. I thought it made testing 
     * easier and was pretty simple to write. This will find a passenger
     * by seat or by name and cancel their reservation.
     */
    void cancelReservation() {
        
        Stage cancelBookingStage = new Stage();
        cancelBookingStage.setTitle("Cancel A Reservation");
                
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(12.5, 12.5, 13.5, 14.5));
        gridPane.setStyle("-fx-base: lightcoral");
        gridPane.setHgap(5.5);
        gridPane.setVgap(5.5);
          
        ToggleGroup tgGroup = new ToggleGroup();
        RadioButton rbName = new RadioButton("Find by Name");
        rbName.setToggleGroup(tgGroup);
        RadioButton rbSeat = new RadioButton("Find by Seat");
        rbSeat.setToggleGroup(tgGroup);
        gridPane.add(rbName, 0, 0);
        gridPane.add(rbSeat, 1, 0);
        
        gridPane.add(new Label("First Name:"), 0, 1);
        TextField firstName = new TextField();
        firstName.setEditable(false);
        gridPane.add(firstName, 1, 1);
        
        gridPane.add(new Label("Last Name:"), 0, 2);
        TextField lastName = new TextField();
        lastName.setEditable(false);
        gridPane.add(lastName, 1, 2);
        
        gridPane.add(new Label("Seat#"), 0, 3);
        ComboBox<String> seatBox = createSeatSelection();
        seatBox.setDisable(true);
        gridPane.add(seatBox, 1, 3);
        
        rbName.setOnAction((ActionEvent nameButton) -> {
                seatBox.setDisable(true);
                firstName.setEditable(true);
                lastName.setEditable(true);
        });
        
        rbSeat.setOnAction((ActionEvent seatButton) -> {
                seatBox.setDisable(false);
                firstName.clear();
                firstName.setEditable(false);
                lastName.clear();
                lastName.setEditable(false);
        });
        
        gridPane.add(new Label("Flight#:"), 0, 4);
        ComboBox<String> cbFlight = createFlightComboBox();
        gridPane.add(cbFlight, 1, 4);

        ToggleButton submitButton = new ToggleButton("Submit");
        submitButton.setOnAction((ActionEvent submit) -> {
           try { 
            String userFlight = cbFlight.getValue();
            
            switch (userFlight) {
                case "100":
                    if (seatBox.isDisabled()) {
                        if (flight100.deletePassenger(firstName.getText().trim(), lastName.getText().trim())) {
                                cancelResSuccessMessage();
                        }
                        else { cancelResFailureMessage(); }
                    }
                    else {
                        if (flight100.deletePassenger(Integer.parseInt(seatBox.getValue()))) {
                            cancelResSuccessMessage();
                        }
                        else { cancelResFailureMessage(); }
                    }  
                    break;
                case "200":
                    if (seatBox.isDisabled()) {
                        if (flight200.deletePassenger(firstName.getText().trim(), lastName.getText().trim())) {
                                cancelResSuccessMessage();
                        }
                        else { cancelResFailureMessage(); }
                    }
                    else {
                        if (flight200.deletePassenger(Integer.parseInt(seatBox.getValue()))) {
                            cancelResSuccessMessage();
                        }
                        else { cancelResFailureMessage(); }
                    }  
                    break;
                case "300":
                    if (seatBox.isDisabled()) {
                        if (flight300.deletePassenger(firstName.getText().trim(), lastName.getText().trim())) {
                                cancelResSuccessMessage();
                        }
                        else { cancelResFailureMessage(); }
                    }
                    else {
                        if (flight300.deletePassenger(Integer.parseInt(seatBox.getValue()))) {
                            cancelResSuccessMessage();
                        }
                        else { cancelResFailureMessage(); }
                    }  
                    break;
                case "400":
                    if (seatBox.isDisabled()) {
                        if (flight400.deletePassenger(firstName.getText().trim(), lastName.getText().trim())) {
                                cancelResSuccessMessage();
                        }
                        else { cancelResFailureMessage(); }
                    }
                    else {
                        if (flight400.deletePassenger(Integer.parseInt(seatBox.getValue()))) {
                            cancelResSuccessMessage();
                        }
                        else { cancelResFailureMessage(); }
                    }  
                    break;
                case "500":
                    if (seatBox.isDisabled()) {
                        if (flight500.deletePassenger(firstName.getText().trim(), lastName.getText().trim())) {
                                cancelResSuccessMessage();
                        }
                        else { cancelResFailureMessage(); }
                    }
                    else {
                        if (flight500.deletePassenger(Integer.parseInt(seatBox.getValue()))) {
                            cancelResSuccessMessage();
                        }
                        else { cancelResFailureMessage(); }
                    }  
                    break;
                default: fieldsLeftEmptyMessage();
            }
            firstName.clear();
            lastName.clear();
           }
           catch (Exception e) {
               fieldsLeftEmptyMessage();
           }
        });
        
        gridPane.add(submitButton, 1, 5);
        gridPane.setHalignment(submitButton, HPos.CENTER);
        
        Scene cancelScene = new Scene(gridPane, 450, 300);
        cancelBookingStage.setScene(cancelScene);
        cancelBookingStage.show();
    }
    
    /**
     * Creates the reservation menu, sets accelerator keys 
     * and sets the event handlers for the non-smoking and smoking
     * menu items.
     * 
     * @return Menu
     */
    Menu createReservationMenu() {
        
        Menu reservationMenu = new Menu("Make Reservation");
        
        MenuItem smokingItem = new MenuItem("Smoking Section"); 
        smokingItem.setAccelerator(KeyCombination.keyCombination("shortcut+S"));
        
        MenuItem nonSmokingItem = new MenuItem("Non-Smoking Section ");
        nonSmokingItem.setAccelerator(KeyCombination.keyCombination("shortcut+N"));
        
        // pop up a window that will allow users to make a smoking reservation
        smokingItem.setOnAction((ActionEvent e) ->{
            smokingReservation();
        });
        
        // pop up a window that will allow users to make a non smoking reservation
        nonSmokingItem.setOnAction((ActionEvent e) -> {
            nonSmokingReservation();
        });
        
        reservationMenu.getItems().addAll(smokingItem, nonSmokingItem);
        return reservationMenu;
    }
    
    /** Pops up a window where the user can make a non smoking reservation */
    void nonSmokingReservation() {
        
        Stage reservationStage = new Stage ();
        reservationStage.setTitle("Non-Smoking Reservation");
        
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
        gridPane.setHgap(5.5);
        gridPane.setVgap(5.5);
        gridPane.setStyle("-fx-base: lightskyblue");
        
        
        gridPane.add(new Label("First Name:"), 0, 0);
        TextField firstName = new TextField();
        firstName.setEditable(true);
        gridPane.add(firstName, 1, 0);
        
        gridPane.add(new Label("Last Name:"), 0, 1);
        TextField lastName = new TextField();
        lastName.setEditable(true);
        gridPane.add(lastName, 1, 1);
        
        gridPane.add(new Label("Street Adress:"), 0, 2);
        TextField street = new TextField();
        street.setEditable(true);
        gridPane.add(street,1,2);
        
        gridPane.add(new Label("City:"), 0, 3);
        TextField city = new TextField();
        city.setEditable(true);
        gridPane.add(city, 1, 3);
                
        gridPane.add(new Label("Zip Code:"), 0, 4);
        TextField zipcode = new TextField();
        zipcode.setEditable(true);
        gridPane.add(zipcode, 1, 4);
        
        gridPane.add(new Label("State:"), 0, 5);
        ComboBox<String> cbState = createStateComboBox();
        gridPane.add(cbState, 1, 5);

        gridPane.add(new Label("Flight#:"), 0, 6);
        ComboBox<String> cbFlight = createFlightComboBox();
        gridPane.add(cbFlight, 1, 6);

        gridPane.add(new Label("Seat#[6-10 non-smoking]:"), 0, 7);
        ComboBox<String> cbNonSmoking = createNonSmokingBox();
        gridPane.add(cbNonSmoking, 1, 7);
        
        ToggleButton submitButton = new ToggleButton("Submit");
        ToggleButton cancelButton = new ToggleButton("Cancel");
        
        submitButton.setOnAction((ActionEvent submit) -> {
            try {
                String userFlight = cbFlight.getValue();
                int userSeat = Integer.parseInt(cbNonSmoking.getValue());
            
                switch(userFlight) {
                    case "100" :
                            if (flight100.isFlightFull()) {
                                    flightIsFullMessage(userFlight);
                            }
                            else if (flight100.isNonSmokingSectionFull()) {
                                    nonSmokingSectionFullMessage(userFlight);
                            }
                            else if (flight100.isSeatTaken(userSeat)) {
                                    seatOccupiedMessage(userFlight, userSeat);
                            }
                            else {
                                    flight100.reserveSeat(firstName.getText().trim(), lastName.getText().trim(), street.getText(), 
                                            city.getText(), zipcode.getText(), cbState.getValue(), userFlight, userSeat);
                                    successfulReservationMessage();
                            }
                            break;
                    case "200" :
                            if (flight200.isFlightFull()) {
                                    flightIsFullMessage(userFlight);
                            }
                            else if (flight200.isNonSmokingSectionFull()) {
                                    nonSmokingSectionFullMessage(userFlight);
                            }
                            else if (flight200.isSeatTaken(userSeat)) {
                                    seatOccupiedMessage(userFlight, userSeat);
                            }
                            else {
                                    flight200.reserveSeat(firstName.getText().trim(), lastName.getText().trim(), street.getText(), 
                                            city.getText(), zipcode.getText(), cbState.getValue(), userFlight, userSeat);
                                    successfulReservationMessage();
                            }
                            break;
                    case "300" :
                            if (flight300.isFlightFull()) {
                                    flightIsFullMessage(userFlight);
                            }
                            else if (flight300.isNonSmokingSectionFull()) {
                                    nonSmokingSectionFullMessage(userFlight);
                            }
                            else if (flight300.isSeatTaken(userSeat)) {
                                    seatOccupiedMessage(userFlight, userSeat);
                            }
                            else {
                                    flight300.reserveSeat(firstName.getText().trim(), lastName.getText().trim(), street.getText(), 
                                            city.getText(), zipcode.getText(), cbState.getValue(), userFlight, userSeat);
                                    successfulReservationMessage();
                            }
                            break;
                    case "400" :
                            if (flight400.isFlightFull()) {
                                    flightIsFullMessage(userFlight);
                            }
                            else if (flight400.isNonSmokingSectionFull()) {
                                    nonSmokingSectionFullMessage(userFlight);
                            }
                            else if (flight400.isSeatTaken(userSeat)) {
                                    seatOccupiedMessage(userFlight, userSeat);
                            }
                            else {
                                    flight400.reserveSeat(firstName.getText().trim(), lastName.getText().trim(), street.getText(), 
                                            city.getText(), zipcode.getText(), cbState.getValue(), userFlight, userSeat);
                                    successfulReservationMessage();
                            }
                            break;
                    case "500" :
                            if (flight500.isFlightFull()) {
                                    flightIsFullMessage(userFlight);
                            }
                            else if (flight500.isNonSmokingSectionFull()) {
                                    nonSmokingSectionFullMessage(userFlight);
                            }
                            else if (flight500.isSeatTaken(userSeat)) {
                                    seatOccupiedMessage(userFlight, userSeat);
                            }
                            else {
                                    flight500.reserveSeat(firstName.getText().trim(), lastName.getText().trim(), street.getText(), 
                                            city.getText(), zipcode.getText(), cbState.getValue(), userFlight, userSeat);
                                    successfulReservationMessage();
                            }
                            break;  
                    default: fieldsLeftEmptyMessage();
                }
                reservationStage.close();
           }
           catch (Exception e) {
                fieldsLeftEmptyMessage();
           }
        });
               
        cancelButton.setOnAction((ActionEvent cancel) -> {
            reservationStage.close();
        });
        
        gridPane.add(submitButton, 0, 8);
        gridPane.setHalignment(submitButton, HPos.CENTER);
        gridPane.add(cancelButton, 1, 8);
        gridPane.setHalignment(cancelButton, HPos.CENTER);
        
        Scene reservationScene = new Scene(gridPane);
        reservationStage.setScene(reservationScene);
        reservationStage.show();           
        
    }
        
    /** 
     * Pops up a window where you can make a smoking reservation
     */
    void smokingReservation() {
        
        Stage reservationStage = new Stage ();
        reservationStage.setTitle("Smoking Reservation");
        
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
        gridPane.setHgap(5.5);
        gridPane.setVgap(5.5);
        gridPane.setStyle("-fx-base: pink");
        
        
        gridPane.add(new Label("First Name:"), 0, 0);
        TextField firstName = new TextField();
        firstName.setEditable(true);
        gridPane.add(firstName, 1, 0);
        
        gridPane.add(new Label("Last Name:"), 0, 1);
        TextField lastName = new TextField();
        lastName.setEditable(true);
        gridPane.add(lastName, 1, 1);
        
        gridPane.add(new Label("Street Adress:"), 0, 2);
        TextField street = new TextField();
        street.setEditable(true);
        gridPane.add(street,1,2);
        
        gridPane.add(new Label("City:"), 0, 3);
        TextField city = new TextField();
        city.setEditable(true);
        gridPane.add(city, 1, 3);
        
        gridPane.add(new Label("Zip Code:"), 0, 4);
        TextField zipcode = new TextField();
        zipcode.setEditable(true);
        gridPane.add(zipcode, 1, 4);
        
        gridPane.add(new Label("State:"), 0, 5);
        ComboBox<String> cbState = createStateComboBox();
        gridPane.add(cbState, 1, 5);
        
        gridPane.add(new Label("Flight#:"), 0, 6);
        ComboBox<String> cbFlight = createFlightComboBox();
        gridPane.add(cbFlight, 1, 6);
        
        gridPane.add(new Label("Seat#[1-5 are smoking]:"), 0, 7);
        ComboBox<String> cbSmokeBox = createSmokingBox();
        gridPane.add(cbSmokeBox, 1, 7);
        
        ToggleButton submitButton = new ToggleButton("Submit");
        ToggleButton cancelButton = new ToggleButton("Cancel");
        
        
        submitButton.setOnAction((ActionEvent submit) -> {
           try {
               String userFlight = cbFlight.getValue();
               int userSeat = Integer.parseInt(cbSmokeBox.getValue());
            
               switch(userFlight) {
                   case "100" :
                            if (flight100.isFlightFull()) {
                                    flightIsFullMessage(userFlight);
                            }
                            else if (flight100.isSmokingSectionFull()) {
                                    smokingSectionFullMessage(userFlight);
                            }
                            else if (flight100.isSeatTaken(userSeat)) {
                                    seatOccupiedMessage(userFlight, userSeat);
                            }
                            else {
                                    flight100.reserveSeat(firstName.getText().trim(), lastName.getText().trim(), street.getText(), 
                                            city.getText(), zipcode.getText(), cbState.getValue(), userFlight, userSeat);
                                    successfulReservationMessage();
                            }
                            break;
                    case "200" :
                            if (flight200.isFlightFull()) {
                                    flightIsFullMessage(userFlight);
                            }
                            else if (flight200.isSmokingSectionFull()) {
                                    smokingSectionFullMessage(userFlight);
                            }
                            else if (flight200.isSeatTaken(userSeat)) {
                                    seatOccupiedMessage(userFlight, userSeat);
                            }
                            else {
                                    flight200.reserveSeat(firstName.getText().trim(), lastName.getText().trim(), street.getText(), 
                                            city.getText(), zipcode.getText(), cbState.getValue(), userFlight, userSeat);
                                    successfulReservationMessage();
                            }
                            break;
                    case "300" :
                            if (flight300.isFlightFull()) {
                                    flightIsFullMessage(userFlight);
                            }
                            else if (flight300.isSmokingSectionFull()) { 
                                    smokingSectionFullMessage(userFlight);
                            }
                            else if (flight300.isSeatTaken(userSeat)) {
                                    seatOccupiedMessage(userFlight, userSeat);
                            }
                            else {
                                    flight300.reserveSeat(firstName.getText().trim(), lastName.getText().trim(), street.getText(), 
                                           city.getText(), zipcode.getText(), cbState.getValue(), userFlight, userSeat);  
                                    successfulReservationMessage();
                            }
                            break;
                    case "400" :
                            if (flight400.isFlightFull()) {
                                    flightIsFullMessage(userFlight);
                            }
                            else if (flight400.isSmokingSectionFull()) {
                                    smokingSectionFullMessage(userFlight);
                            }
                            else if (flight400.isSeatTaken(userSeat)) {
                                    seatOccupiedMessage(userFlight, userSeat);
                            }
                            else {
                                    flight400.reserveSeat(firstName.getText().trim(), lastName.getText().trim(), street.getText(), 
                                           city.getText(), zipcode.getText(), cbState.getValue(), userFlight, userSeat); 
                                 successfulReservationMessage();
                            }
                            break;
                    case "500" :
                            if (flight500.isFlightFull()) {
                                    flightIsFullMessage(userFlight);
                            }
                            else if (flight500.isSmokingSectionFull()) {
                                    smokingSectionFullMessage(userFlight);
                            }
                            else if (flight500.isSeatTaken(userSeat)) {
                                    seatOccupiedMessage(userFlight, userSeat);
                            }
                            else {
                                    flight500.reserveSeat(firstName.getText().trim(), lastName.getText().trim(), street.getText(), 
                                            city.getText(), zipcode.getText(), cbState.getValue(), userFlight, userSeat); 
                                    successfulReservationMessage();
                            }
                            break;    
                    default: fieldsLeftEmptyMessage();
                }
                reservationStage.close();
           }
           catch (Exception e) {
                fieldsLeftEmptyMessage();
           }
        });
        
        cancelButton.setOnAction((ActionEvent cancel) -> {
                reservationStage.close();
        });
        
        gridPane.add(submitButton, 0, 8);
        gridPane.setHalignment(submitButton, HPos.CENTER);
        gridPane.add(cancelButton, 1, 8);
        gridPane.setHalignment(cancelButton, HPos.CENTER);
        
        
        Scene reservationScene = new Scene(gridPane);
        reservationStage.setScene(reservationScene);
        reservationStage.show();      
    } 
    
    /**
     * notifies the user that the fields were left empty
     */
    void fieldsLeftEmptyMessage() {
        Stage emptyFieldStage = new Stage();
        emptyFieldStage.setTitle("Fields Left Empty Error Message");
        BorderPane emptyFPane = new BorderPane();
        emptyFPane.setPadding(new Insets(11, 12, 13, 14));
                
        Label message = new Label("All fields must be completed \n \tbefore submission");
        message.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
        message.setAlignment(Pos.CENTER);
        emptyFPane.setCenter(message);
        
        emptyFPane.setStyle("-fx-background-color: lightcoral");
        Scene emptyFScene = new Scene(emptyFPane, 400, 150);
        emptyFieldStage.setScene(emptyFScene);
        emptyFieldStage.show();          
    }
    
    /**
     * Message that pops up if the user successfully made a reservation
     */
    void successfulReservationMessage() {
            Stage successResStage = new Stage();
            successResStage.setTitle("Successful Reservation");
            BorderPane cSuccessPane = new BorderPane();
            cSuccessPane.setPadding(new Insets(11, 12, 13, 14));
            
            Label message = new Label("Registration was Successful!");
            message.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
            message.setAlignment(Pos.CENTER);
            cSuccessPane.setCenter(message);
    
            cSuccessPane.setStyle("-fx-background-color: palegreen");
            Scene successResScene = new Scene(cSuccessPane);
            successResStage.setScene(successResScene);
            successResStage.show();
    }
    
    void cancelResFailureMessage() {
        Stage cancelFailStage = new Stage();
        cancelFailStage.setTitle("Failed to Cancel Reservation");
        BorderPane cFailPane = new BorderPane();
        cFailPane.setPadding(new Insets(11, 12, 13, 14));
        
        Label message = new Label(" Reservation was unsuccessfully deleted.\n"
                + "\tThe reservation was not found.");
        message.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
        message.setAlignment(Pos.CENTER);
        cFailPane.setCenter(message);
        
        cFailPane.setStyle("-fx-background-color: lightcoral");
        Scene cFailScene = new Scene(cFailPane);
        cancelFailStage.setScene(cFailScene);
        cancelFailStage.show();
    }
    
    void cancelResSuccessMessage() {
        Stage cancelResStage = new Stage();
        cancelResStage.setTitle("Succesfully Cancelled Reservation");
        BorderPane cResPane = new BorderPane();
        cResPane.setPadding(new Insets(11, 12, 13, 14));
        
        Label message = new Label("Reservation was successfully cancelled.");
        message.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
        message.setAlignment(Pos.CENTER);
        cResPane.setCenter(message);
        
        cResPane.setStyle("-fx-background-color: palegreen");
        Scene cResScene = new Scene(cResPane);
        cancelResStage.setScene(cResScene);
        cancelResStage.show();
    }
      
    void seatOccupiedMessage(String flightInfo, int userSeat) {
        Stage seatOccStage = new Stage();
        seatOccStage.setTitle("Seat Already Reserved Error");
        BorderPane seatOccPane = new BorderPane();
        seatOccPane.setPadding(new Insets(11, 12, 13, 14));
        
        Label message = new Label("Flight " + flightInfo + " Seat #" 
                + userSeat + " is already reserved");
        message.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
        message.setAlignment(Pos.CENTER);
        seatOccPane.setCenter(message);
        
        seatOccPane.setStyle("-fx-background-color: salmon");
        Scene occSeatScene = new Scene(seatOccPane);
        seatOccStage.setScene(occSeatScene);
        seatOccStage.show();
    }
    
    void smokingSectionFullMessage(String flightInfo) {
        Stage sectionFullStage = new Stage();
        sectionFullStage.setTitle("Smoking Section Reservation Error");
        BorderPane fullSectionPane = new BorderPane();
        fullSectionPane.setPadding(new Insets(11, 12, 13, 14));
        
        Label fullSectionMessage = new Label("Flight " + flightInfo + "'s smoking section is"
                + " completely Booked.\nWould you like to make a non-smoking reservation?");
        fullSectionMessage.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
        fullSectionMessage.setAlignment(Pos.CENTER);
        fullSectionPane.setCenter(fullSectionMessage);
        
        ToggleButton yesButton = new ToggleButton("Yes");
        yesButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));
        yesButton.setStyle("-fx-base: palegreen ");
        yesButton.setAlignment(Pos.CENTER);
        
        yesButton.setOnAction((ActionEvent yes) -> {
                nonSmokingReservation();
                sectionFullStage.close();
        });
        
        ToggleButton noButton = new ToggleButton("No");
        noButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));
        noButton.setStyle("-fx-base: lightcoral");
        noButton.setAlignment(Pos.CENTER);
        
        noButton.setOnAction((ActionEvent no) -> {
                sectionFullStage.close();
        });
        
        HBox holdButtonsBox = new HBox(5);
        holdButtonsBox.setAlignment(Pos.TOP_CENTER);
        holdButtonsBox.getChildren().addAll(yesButton, noButton);
        
        fullSectionPane.setBottom(holdButtonsBox);
        
        fullSectionPane.setStyle("-fx-background-color: pink");
        Scene fullSectionScene = new Scene(fullSectionPane);
        sectionFullStage.setScene(fullSectionScene);
        sectionFullStage.show();
    }
    
    void nonSmokingSectionFullMessage(String flightInfo) {
        Stage sectionFullStage = new Stage();
        sectionFullStage.setTitle("Non-Smoking Section Reservation Error");
        BorderPane fullSectionPane = new BorderPane();
        fullSectionPane.setPadding(new Insets(11, 12, 13, 14));
        
        Label fullSectionMessage = new Label("Flight " + flightInfo + "'s non-smoking section is"
                + " completely Booked.\n\tWould you like to make a smoking reservation?");
        fullSectionMessage.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
        fullSectionMessage.setAlignment(Pos.CENTER);
        fullSectionPane.setCenter(fullSectionMessage);
        
        ToggleButton yesButton = new ToggleButton("Yes");
        yesButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));
        yesButton.setStyle("-fx-base: palegreen ");
        yesButton.setAlignment(Pos.CENTER);
        
        yesButton.setOnAction((ActionEvent yes) -> {
                smokingReservation();
                sectionFullStage.close();
        });
        
        ToggleButton noButton = new ToggleButton("No");
        noButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));
        noButton.setStyle("-fx-base: lightcoral");
        noButton.setAlignment(Pos.CENTER);
        
        noButton.setOnAction((ActionEvent no) -> {
                sectionFullStage.close();
        });
        
        HBox holdButtonsBox = new HBox(5);
        holdButtonsBox.setAlignment(Pos.TOP_CENTER);
        holdButtonsBox.getChildren().addAll(yesButton, noButton);
        
        fullSectionPane.setBottom(holdButtonsBox);
        Scene fullSectionScene = new Scene(fullSectionPane);
        sectionFullStage.setScene(fullSectionScene);
        sectionFullStage.show();
    }
    
    void flightIsFullMessage (String fullFlight) { 
      
        Stage fullStage = new Stage();
        fullStage.setTitle("Error with Reservation");
        BorderPane fullFlightPane = new BorderPane();
        fullFlightPane.setPadding(new Insets(11, 12, 13, 14));
        
        Label fullFlightMessage = new Label("Flight " + fullFlight + " is completely Booked");
        fullFlightMessage.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
        fullFlightMessage.setAlignment(Pos.CENTER);
        fullFlightPane.setCenter(fullFlightMessage);
        
        fullFlightPane.setStyle("-fx-background-color: darksalmon");
        Scene fullFlightScene = new Scene(fullFlightPane);
        fullStage.setScene(fullFlightScene);
        fullStage.show();
    }  
    
    ComboBox<String> createNonSmokingBox() {
       
        ComboBox<String> nonSmokingBox = new ComboBox<>();
        nonSmokingBox.setPrefWidth(200);
        String [] nonSmokingArray = {"6", "7", "8", "9", "10"};
        ObservableList<String> allNonSmokingSeats;
        allNonSmokingSeats = FXCollections.observableArrayList(nonSmokingArray);
        nonSmokingBox.getItems().addAll(allNonSmokingSeats);
        return nonSmokingBox;
    }
    
    ComboBox<String> createSmokingBox () {
        
        ComboBox<String> smokeBox = new ComboBox<>();
        
        smokeBox.setPrefWidth(200);
        String [] smokeSeatsArray = { "1", "2", "3", "4", "5" };
        
        ObservableList<String> allSmokeSeats;
        allSmokeSeats = FXCollections.observableArrayList(smokeSeatsArray);
        
        smokeBox.getItems().addAll(smokeSeatsArray);
        return smokeBox;
    }
    
    ComboBox<String> createFlightComboBox() {

        ComboBox<String> flightBox = new ComboBox<>();
            
        flightBox.setPrefWidth(200);
        String [] flightArray = { "100", "200", "300", "400", "500" };
        
        ObservableList<String> allFlights = FXCollections.observableArrayList(flightArray);
            
        flightBox.getItems().addAll(allFlights);  
        return flightBox;     
    }
    
    ComboBox<String> createStateComboBox() {
            
            ComboBox<String> stateCBox = new ComboBox<>();            
            stateCBox.setPrefWidth(200);
            
            // Array of 50 states
            String [] arrayOfStates = { "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", 
                                                 "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", 
                                                 "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
                                                 "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", 
                                                 "SD", "TN",  "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY" };
        
            ObservableList<String> states = FXCollections.observableArrayList(arrayOfStates);
            
            stateCBox.getItems().addAll(states);
            
            return stateCBox;     
    }
    
    Menu createBoardingPassMenu() {
      
        Menu boardingPassMenu = new Menu ("Print Boarding Pass");
        
        MenuItem toPrinterItem = new MenuItem("To Printer");
        toPrinterItem.setAccelerator(KeyCombination.keyCombination("shift+P"));
        toPrinterItem.setOnAction((ActionEvent printer) -> {
            findPassengerForBoardingPass("printer");
        });
        
        MenuItem toScreenItem = new MenuItem("To Screen");
        toScreenItem.setAccelerator(KeyCombination.keyCombination("shift+s"));
        toScreenItem.setOnAction((ActionEvent screen) -> {
            findPassengerForBoardingPass("screen");
        });
        
        boardingPassMenu.getItems().addAll(toPrinterItem, toScreenItem);
        return boardingPassMenu;
    }
    
    void findPassengerForBoardingPass(String howToHandle) {
                
        Stage getSeatStage = new Stage();
        
        if (howToHandle == "screen") {
            getSeatStage.setTitle("Find Passenger To Display Boarding Pass");
        }
        else if (howToHandle == "printer") {
            getSeatStage.setTitle("Find Passenger To Print Boarding Pass");
        }
        GridPane getSeatPane = new GridPane();
        getSeatPane.setAlignment(Pos.CENTER);
        getSeatPane.setPadding(new Insets(12.5, 12.5, 13.5, 14.5));
        
        if (howToHandle == "screen") {
            getSeatPane.setStyle("-fx-base: lavenderblush");
        }
        else if (howToHandle == "printer") {
            getSeatPane.setStyle("-fx-base: aquamarine");
        }
        
        getSeatPane.setHgap(5.5);
        getSeatPane.setVgap(5.5);
        
        ToggleGroup tgGroup = new ToggleGroup();
        RadioButton rbName = new RadioButton("Find by Name");
        rbName.setToggleGroup(tgGroup);
        RadioButton rbSeat = new RadioButton("Find by Seat");
        rbSeat.setToggleGroup(tgGroup);
        getSeatPane.add(rbName, 0, 0);
        getSeatPane.add(rbSeat, 1, 0);
        
        getSeatPane.add(new Label("First Name:"), 0, 1);
        TextField firstName = new TextField();
        firstName.setEditable(false);
        getSeatPane.add(firstName, 1, 1);
        
        getSeatPane.add(new Label("Last Name:"), 0, 2);
        TextField lastName = new TextField();
        lastName.setEditable(false);
        getSeatPane.add(lastName, 1, 2);
                
        getSeatPane.add(new Label("Seat#"), 0, 3);
        ComboBox<String> seatBox = createSeatSelection();
        seatBox.setDisable(true);
        getSeatPane.add(seatBox, 1, 3);
        
        rbName.setOnAction((ActionEvent nameButton) -> {
                seatBox.setDisable(true);
                firstName.setEditable(true);
                lastName.setEditable(true);
        });
        
        rbSeat.setOnAction((ActionEvent seatButton) -> {
                seatBox.setDisable(false);
                firstName.clear();
                firstName.setEditable(false);
                lastName.clear();
                lastName.setEditable(false);
        });

        getSeatPane.add(new Label("Flight#:"), 0, 4);
        ComboBox<String> cbFlight = createFlightComboBox();
        getSeatPane.add(cbFlight, 1, 4);

        ToggleButton submitButton = new ToggleButton("Submit");    
        getSeatPane.add(submitButton, 1, 5);
        getSeatPane.setHalignment(submitButton, HPos.CENTER);
        
        submitButton.setOnAction((ActionEvent e) -> {
            try { 
                String userFlight = cbFlight.getValue();
                Passenger tempPassenger = null;
                switch (userFlight) {
                    case "100":                        
                        if (seatBox.isDisabled()) {
                            String first = firstName.getText();
                            String last = lastName.getText();
                            tempPassenger = flight100.getPassenger(first, last);
                        }
                        else {
                            int userSeat = Integer.parseInt(seatBox.getValue());
                            tempPassenger = flight100.getPassenger(userSeat);
                        }
                        break;
                    case "200":
                        if (seatBox.isDisabled()) {
                            String first = firstName.getText();
                            String last = lastName.getText();
                            tempPassenger = flight200.getPassenger(first, last);
                        }
                        else {
                                int userSeat = Integer.parseInt(seatBox.getValue());
                                tempPassenger = flight200.getPassenger(userSeat);
                        }   
                        break;
                    case "300":
                        if (seatBox.isDisabled()) {
                            String first = firstName.getText();
                            String last = lastName.getText();
                            tempPassenger = flight300.getPassenger(first, last);
                        }
                        else {
                            int userSeat = Integer.parseInt(seatBox.getValue());
                            tempPassenger = flight300.getPassenger(userSeat);
                        }
                        break;
                    case "400":
                        if (seatBox.isDisabled()) {
                            String first = firstName.getText();
                            String last = lastName.getText();
                            tempPassenger = flight400.getPassenger(first, last);
                        }
                        else {
                            int userSeat = Integer.parseInt(seatBox.getValue());
                            tempPassenger = flight400.getPassenger(userSeat);
                        }
                        break;
                    case "500":
                        if (seatBox.isDisabled()) {
                            String first = firstName.getText();
                            String last = lastName.getText();
                            tempPassenger = flight500.getPassenger(first, last);
                        }
                        else {
                            int userSeat = Integer.parseInt(seatBox.getValue());
                            tempPassenger = flight500.getPassenger(userSeat);
                        }
                        break;
                    default : fieldsLeftEmptyMessage();
                }
                if (howToHandle == "screen") {
                    boardingPass(tempPassenger);
                }
                else if(howToHandle == "printer") {
                    printBoardPass(tempPassenger);
                }
                firstName.clear();
                lastName.clear();
            }
            catch(Exception ex) {
                fieldsLeftEmptyMessage();
            }
        });

        Scene getSeatScene = new Scene(getSeatPane, 450, 300);
        getSeatStage.setScene(getSeatScene);
        getSeatStage.show();
    }
    
    void printBoardPass(Passenger passenger) {
        try {
            FileOutputStream outfile = new FileOutputStream("\\\\cts-fp.bhcc.dom\\D116");
            PrintWriter printer = new PrintWriter(outfile, true);
            String flightInfo = "Colossus Airlines - Boarding Pass";
            String first = "\n\rFirst Name: " + passenger.getFirstName();
            String last = "\n\rLast Name: " + passenger.getLastName();
            String seatNumber = "\n\rSeat Number: " + passenger.getSeatNumber();
            String flightNumber = "\n\rFlight Number: " + passenger.getFlightNum();

            printer.print(flightInfo);
            printer.print(first);
            printer.print(last);
            printer.print(seatNumber);
            printer.print(flightNumber);
                    
            printer.print("\f");
            printer.close();
            outfile.close();
            printer = null;
            outfile = null;
            printSuccessMessage();
        }
        catch (Exception ex) {
            printFailureMessage();
        }
    }
    
    /**
     * If the boarding pass was successfully being printed this message
     * will pop up
     */
    void printSuccessMessage() {
        Stage successStage = new Stage();
        successStage.setTitle("Boarding Pass Successfully Printed");
        BorderPane successPane = new BorderPane();
        successPane.setPadding(new Insets(11, 12, 13, 14));
        
        Label message = new Label("Boarding Information was\n    Successfully Printed.");
        message.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
        message.setAlignment(Pos.CENTER);
        successPane.setCenter(message);
        
        successPane.setStyle("-fx-background-color: palegreen");
        Scene successScene = new Scene(successPane);
        successStage.setScene(successScene);
        successStage.show();
    }
    
    /**
     * If the printing of the boarding pass field this message will
     * pop up
     */
    void printFailureMessage() {
        Stage printFailStage = new Stage();
        printFailStage.setTitle("Print Failure Occurred");
        BorderPane failPane = new BorderPane();
        failPane.setPadding(new Insets(11, 12, 13, 14));
        
        Label message = new Label("Print Failure has occurred.");
        message.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20));
        message.setAlignment(Pos.CENTER);
        failPane.setCenter(message);
        
        failPane.setStyle("-fx-background-color: lightcoral");
        Scene failScene = new Scene(failPane, 300, 150);
        printFailStage.setScene(failScene);
        printFailStage.show();
    }
    
    /**
     * displays the boarding pass for passenger to the screen
     * @param passenger 
     */
    void boardingPass(Passenger passenger) {
        Stage boardingPassStage = new Stage();
        Scene boardingPassScene;
           
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(11,12,13,14));

        if (passenger == null) {
            // error message
            boardingPassStage.setTitle("Boarding Pass Error");
            Label message = new Label();
            message.setText("Requested Reservation Could\n"
                    + "\t     Not Be Found.");
            message.setAlignment(Pos.CENTER);
            message.setFont(Font.font("Courier",FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 24));
            pane.setCenter(message);
            pane.setStyle("-fx-background-color: lightcoral");
            boardingPassScene = new Scene(pane, 360, 200);
        }
        else {
            boardingPassStage.setTitle("Colossus Airlines - Boarding Pass");
            ImageView seeImage = new ImageView ( new Image ("airplanelogo.jpg"));
            seeImage.setManaged(true);
            pane.setRight(seeImage);
        
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(11, 12, 13, 14));
            grid.setHgap(40);
        
            Label lastLabel = new Label();
            lastLabel.setText("Last Name:");
            lastLabel.setUnderline(true);
            lastLabel.setFont(Font.font("Courier", FontWeight.BLACK, FontPosture.REGULAR, 29));
            grid.add(lastLabel, 0, 0);
        
            Label last = new Label();
            last.setText(passenger.getLastName());
            last.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 26));
            grid.add(last, 1, 0);
        
            Label firstLabel = new Label();
            firstLabel.setText("First Name:");
            firstLabel.setUnderline(true);
            firstLabel.setFont(Font.font("Courier", FontWeight.BLACK, FontPosture.REGULAR, 29));
            grid.add(firstLabel, 0, 1);
        
            Label first = new Label();
            first.setText(passenger.getFirstName());
            first.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 26));
            grid.add(first, 1, 1);
        
            Label flightLabel = new Label();
            flightLabel.setText("Flight#:");
            flightLabel.setUnderline(true);
            flightLabel.setFont(Font.font("Courier", FontWeight.BLACK, FontPosture.REGULAR, 29));
            grid.add(flightLabel, 0, 2);
        
            Label flightNum = new Label();
            flightNum.setText(passenger.getFlightNum());
            flightNum.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 26));
            grid.add(flightNum, 1, 2);
        
            Label seatLabel = new Label();
            seatLabel.setText("Seat#:");
            seatLabel.setUnderline(true);
            seatLabel.setFont(Font.font("Courier", FontWeight.BLACK, FontPosture.REGULAR, 29));
            grid.add(seatLabel, 0, 3);
        
            Label seatNumber = new Label();
            seatNumber.setText(String.valueOf(passenger.getSeatNumber()));
            seatNumber.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 26));
            grid.add(seatNumber, 1, 3);
        
            pane.setCenter(grid);
            pane.setStyle("-fx-background-color: white");
            boardingPassScene = new Scene(pane, 900, 325);
            boardingPassStage.setScene(boardingPassScene);
        }
        
        boardingPassStage.setScene(boardingPassScene);
        boardingPassStage.show();
    }
    
    /**
     * This is called in the stop() method because all of the objects need to 
     * be saved to the file when the program is closed.
     */
    public void saveBookingsToFile() {
        try {
            ObjectOutputStream os;
            os = new ObjectOutputStream (new FileOutputStream("Bookings.dat"));
            
            os.writeObject(flight100);
            os.writeObject(flight200);
            os.writeObject(flight300);
            os.writeObject(flight400);
            os.writeObject(flight500);
        
            os.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
            
    /**
     * This method displays all seats using a carriage return between each
     * flight so that users can quickly tell which flights are coupled 
     * with one another.
     */
    void displayAllSeats() {
        
        Stage displayAllStage= new Stage();
        displayAllStage.setTitle("Display All Seat Assignments");
        ScrollPane scPane = new ScrollPane();
        scPane.setStyle("-fx-base: linen");
        
        VBox displayAllPane = new VBox();
        displayAllPane.setAlignment(Pos.CENTER);
        displayAllPane.setPadding(new Insets(11, 12, 13, 14));
        
        Label message = new Label("Displaying All Seat Assignments");
        message.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.REGULAR, 26));
        message.setUnderline(true);
        message.setAlignment(Pos.CENTER);
        displayAllPane.getChildren().add(message);
        
        GridPane labelPane = new GridPane();
        labelPane.setPadding(new Insets(11, 12, 13, 14));
        labelPane.setHgap(40);
        labelPane.setAlignment(Pos.CENTER);
        displayAllPane.getChildren().add(labelPane);
        
        Label flight = createLabelForDisplay("Flight#", 50);
        flight.setUnderline(true);
        labelPane.add( flight, 0, 0);
        
        Label last = createLabelForDisplay("LastName", 135);
        last.setUnderline(true);
        labelPane.add( last, 1, 0);
        
        Label first = createLabelForDisplay("First Name", 135);
        first.setUnderline(true);
        labelPane.add( first, 2, 0);
        
        Label seat = createLabelForDisplay("Seat#", 50);
        seat.setUnderline(true);
        labelPane.add( seat, 3, 0);
        
           
        // Display All the passengers on flight 100        
        Passenger [] sortedFlight100 = flight100.getSortedFlight();        
        GridPane gridFlight100 = createGridPaneForSortedFlight();
        for (int i = 0; i < sortedFlight100.length; i++) {
            if (sortedFlight100[i] != null) {
                gridFlight100.add(createLabelForDisplay(sortedFlight100[i].getFlightNum(), 50), 0, i);
                gridFlight100.add(createLabelForDisplay(sortedFlight100[i].getLastName(),135), 1, i);
                gridFlight100.add(createLabelForDisplay(sortedFlight100[i].getFirstName(), 135), 2, i);
                gridFlight100.add(createLabelForDisplay(String.valueOf(sortedFlight100[i].getSeatNumber()),50), 3, i);
            }
        }
        displayAllPane.getChildren().add(gridFlight100);
        
        // Display All the passengers on flight 200        
        Passenger [] sortedFlight200 = flight200.getSortedFlight();        
        GridPane gridFlight200 =  createGridPaneForSortedFlight();
        for (int i = 0; i < sortedFlight200.length; i++) {
            if (sortedFlight200[i] != null) {
                gridFlight200.add(createLabelForDisplay(sortedFlight200[i].getFlightNum(), 50), 0, i);
                gridFlight200.add(createLabelForDisplay(sortedFlight200[i].getLastName(),135), 1, i);
                gridFlight200.add(createLabelForDisplay(sortedFlight200[i].getFirstName(), 135), 2, i);
                gridFlight200.add(createLabelForDisplay(String.valueOf(sortedFlight200[i].getSeatNumber()),50), 3, i);
            }
        }
        displayAllPane.getChildren().add(gridFlight200);
                       
        // Display All the passengers on flight 300
        Passenger [] sortedFlight300 = flight300.getSortedFlight();        
        GridPane gridFlight300 =  createGridPaneForSortedFlight();
        for (int i = 0; i < sortedFlight300.length; i++) {
            if (sortedFlight300[i] != null) {
                gridFlight300.add(createLabelForDisplay(sortedFlight300[i].getFlightNum(), 50), 0, i);
                gridFlight300.add(createLabelForDisplay(sortedFlight300[i].getLastName(),135), 1, i);
                gridFlight300.add(createLabelForDisplay(sortedFlight300[i].getFirstName(), 135), 2, i);
                gridFlight300.add(createLabelForDisplay(String.valueOf(sortedFlight300[i].getSeatNumber()),50), 3, i);
            }
        }
        displayAllPane.getChildren().add(gridFlight300);
        
        // Display All the passengers on flight 400
        Passenger [] sortedFlight400 = flight400.getSortedFlight();        
        GridPane gridFlight400 = createGridPaneForSortedFlight();
        for (int i = 0; i < sortedFlight400.length; i++) {
            if (sortedFlight400[i] != null) {
                gridFlight400.add(createLabelForDisplay(sortedFlight400[i].getFlightNum(), 50), 0, i);
                gridFlight400.add(createLabelForDisplay(sortedFlight400[i].getLastName(),135), 1, i);
                gridFlight400.add(createLabelForDisplay(sortedFlight400[i].getFirstName(), 135), 2, i);
                gridFlight400.add(createLabelForDisplay(String.valueOf(sortedFlight400[i].getSeatNumber()),50), 3, i);
            }
        }
       displayAllPane.getChildren().add(gridFlight400);
           
        // Display All the passengers on flight 500
        Passenger [] sortedFlight500 = flight500.getSortedFlight();        
        GridPane gridFlight500 = createGridPaneForSortedFlight();      
        for (int i = 0; i < sortedFlight500.length; i++) {
            if (sortedFlight500[i] != null) {
                gridFlight500.add(createLabelForDisplay(sortedFlight500[i].getFlightNum(), 50), 0, i);
                gridFlight500.add(createLabelForDisplay(sortedFlight500[i].getLastName(),135), 1, i);
                gridFlight500.add(createLabelForDisplay(sortedFlight500[i].getFirstName(), 135), 2, i);
                gridFlight500.add(createLabelForDisplay(String.valueOf(sortedFlight500[i].getSeatNumber()),50), 3, i);
            }
        }
        displayAllPane.getChildren().add(gridFlight500);
        
        scPane.setContent(displayAllPane);
        scPane.setFitToWidth(true);
        Scene displayAllScene = new Scene(scPane, 650, 700);
        displayAllStage.setScene(displayAllScene);
        displayAllStage.show();
    }
  
    /**
     * Creates the labels for the display all method, 
     * whatever information concerning a passenger or flight
     * passed will be represented by a Label with a preferred width 
     * equal to the double width value.
     * 
     * @param flightInfo
     * @param width
     * @return Label
     */
    Label createLabelForDisplay(String flightInfo, double width) {
        Label label = new Label();
        label.setText(flightInfo);
        label.setFont(Font.font("Courier", FontWeight.BLACK, FontPosture.REGULAR, 15));
        label.setPrefWidth(width);
        return label;
    }
    
    /**
     * Returns a GridPane for each flight when I load all of the passengers
     * as nodes on to the GridPane, when I am displaying all the passengers
     * @return GridPane 
     */
    GridPane createGridPaneForSortedFlight() {
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(11, 12, 13, 14));
        pane.setHgap(40);
        pane.setAlignment(Pos.CENTER); 
        return pane;
    }
    
    @Override
    public void stop() {
        saveBookingsToFile();
    }
}