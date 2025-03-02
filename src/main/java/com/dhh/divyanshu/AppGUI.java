package com.dhh.divyanshu;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class AppGUI extends Application {
    Scene mainScene;
    VBox ticketView;
    Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        TicketManagement.readTicketsfromfile();

        VBox layout = new VBox(8);
        layout.setStyle("-fx-padding: 6px;");
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("Bus Ticket Management System");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: blue; -fx-padding: 8px;");
        ticketView = new VBox(4);
        ticketView.setStyle("-fx-padding: 4px; -fx-pref-height: 200px");
        VBox.setVgrow(ticketView, Priority.ALWAYS);
        viewTickets();

        Button bookButton = new Button("Book Ticket");
        bookButton.setOnAction(e -> stage.setScene(bookTicketWindow(stage)));
        layout.getChildren().addAll(title, ticketView, bookButton);

        mainScene = new Scene(layout, 500, 500);

        stage.setTitle("Bus Ticket Management System");
        stage.setResizable(false);

        stage.setScene(mainScene);
        mainStage = stage;
        stage.show();
    }

    private void viewTickets() throws IOException {
        ticketView.getChildren().clear();
        TicketManagement.readTicketsfromfile();
        for (TicketManagement.Ticket ticket: TicketManagement.bookedTickets){
            Button button = new Button();
            button.setOnAction(e -> {
                viewTicketWindow(ticket, TicketManagement.bookedTickets.indexOf(ticket));
            });

            Label id = new Label(String.valueOf(ticket.ticketID));
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Region spacer2 = new Region();
            HBox.setHgrow(spacer2, Priority.ALWAYS);
            Label name = new Label(ticket.passengerName);

            HBox details = new HBox(12, id, spacer, name, spacer2);
            button.setGraphic(details);
            details.setPrefWidth(500);

            ticketView.getChildren().add(button);
        }
    }

    private void viewTicketWindow(TicketManagement.Ticket ticket, int index) {
        Stage view = new Stage();
        view.initModality(Modality.APPLICATION_MODAL);

        // Create labels
        Label ticketIdLabel = new Label("Ticket ID: ");
        Label ticketIdValueLabel = new Label(String.valueOf(ticket.ticketID));
        Label passengerNameLabel = new Label("Passenger Name: ");
        Label passengerNameValueLabel = new Label(ticket.passengerName);
        Label toLabel = new Label("To: ");
        Label toValueLabel = new Label(ticket.placeTo);
        Label fromLabel = new Label("From: ");
        Label fromValueLabel = new Label(ticket.placeFrom);
        Label seatNumberLabel = new Label("Seat Number: ");
        Label seatNumberValueLabel = new Label(String.valueOf(ticket.seatNumber));
        Label fareLabel = new Label("Fare: ");
        Label fareValueLabel = new Label(String.valueOf(ticket.fare));

        // Create button
        Button deleteTicketButton = new Button("DELETE TICKET");
        deleteTicketButton.setOnAction(e -> {
            Alert ask = new Alert(Alert.AlertType.CONFIRMATION);
            ask.setTitle("Confirm Ticket Deletion");
            ask.setHeaderText("Delete Ticket " + ticket.ticketID);
            ask.setContentText("Are you sure you want to delete this ticket owned by " + ticket.passengerName + "?");
            Optional<ButtonType> res = ask.showAndWait();
            if(res.get() == ButtonType.OK) {
                try {
                    TicketManagement.deleteTicket(index);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    viewTickets();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                view.close();
            }
            else {
                try {
                    viewTickets();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                ask.close();
            }
        });

        // Create HBox layouts for each label/value pair
        HBox ticketIdBox = new HBox(ticketIdLabel, ticketIdValueLabel);
        HBox passengerNameBox = new HBox(passengerNameLabel, passengerNameValueLabel);
        HBox toBox = new HBox(toLabel, toValueLabel);
        HBox fromBox = new HBox(fromLabel, fromValueLabel);
        HBox seatNumberBox = new HBox(seatNumberLabel, seatNumberValueLabel);
        HBox fareBox = new HBox(fareLabel, fareValueLabel);

        // Set spacing and alignment for each HBox
        double spacing = 10;
        Insets insets = new Insets(10);
        ticketIdBox.setSpacing(spacing);
        passengerNameBox.setSpacing(spacing);
        toBox.setSpacing(spacing);
        fromBox.setSpacing(spacing);
        seatNumberBox.setSpacing(spacing);
        fareBox.setSpacing(spacing);
        ticketIdBox.setAlignment(Pos.CENTER_LEFT);
        passengerNameBox.setAlignment(Pos.CENTER_LEFT);
        toBox.setAlignment(Pos.CENTER_LEFT);
        fromBox.setAlignment(Pos.CENTER_LEFT);
        seatNumberBox.setAlignment(Pos.CENTER_LEFT);
        fareBox.setAlignment(Pos.CENTER_LEFT);

        // Create a VBox layout to hold all the HBox layouts
        VBox root = new VBox(ticketIdBox, passengerNameBox, toBox, fromBox, seatNumberBox, fareBox, deleteTicketButton);
        root.setSpacing(20);
        root.setPadding(insets);

        Scene scene = new Scene(root, 300, 300);
        view.setScene(scene);
        view.showAndWait();
    }

    private Scene bookTicketWindow(Stage mainStage) {
        // Set up the labels and text fields
        Label titleLabel = new Label("Book a Ticket");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 8px");
        Label fromLabel = new Label("From:");
        Label toLabel = new Label("To:");
        Label nameLabel = new Label("Passenger Name:");
        ChoiceBox<String> fromTextField = new ChoiceBox<>();
        fromTextField.getItems().addAll(TicketManagement.route);
        ChoiceBox<String> toTextField = new ChoiceBox<>();
        toTextField.getItems().addAll(TicketManagement.route);
        TextField nameTextField = new TextField();
        nameTextField.setPromptText("Your Name");

        fromTextField.setValue("Amritsar");
        toTextField.setValue("New Delhi");

        // Set up the generator labels
        Label fareLabel = new Label("Fare:");
        Label fareValueLabel = new Label();
        Label ticketIdLabel = new Label("Ticket ID:");
        Label ticketIdValueLabel = new Label();
        Label seatNumberLabel = new Label("Seat Number:");
        Label seatNumberValueLabel = new Label();

        fareValueLabel.setStyle("-fx-text-fill: green;");
        ticketIdValueLabel.setStyle("-fx-text-fill: green;");
        seatNumberValueLabel.setStyle("-fx-text-fill: green;");

        // Set up the buttons
        Button checkButton = new Button("CHECK FOR TICKET");
        Button bookButton = new Button("BOOK");
        bookButton.setDisable(true);
        checkButton.setOnAction(e -> {
            Alert error = new Alert(Alert.AlertType.INFORMATION);
            error.setTitle("Info");
            error.setHeaderText("Invalid Details");

            String gotName = nameTextField.getText().trim();
            String gotTo = toTextField.getValue();
            String gotFrom = fromTextField.getValue();

            if(gotName.isEmpty()){
                error.setContentText("Please enter your name");
                error.showAndWait();
                bookButton.setDisable(true);
                return;
            }
            if(gotFrom.equals(gotTo)){
                error.setContentText("From and To cannot be same");
                error.showAndWait();
                bookButton.setDisable(true);
                return;
            }

            try {
                boolean full = TicketManagement.vacantSeat == TicketManagement.totalSeats;
                if(full){
                    error.setHeaderText("The Bus is Full");
                    error.setContentText("Tickets not available");
                    error.showAndWait();
                    bookButton.setDisable(true);
                    mainStage.setScene(mainScene);
                }

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            fareValueLabel.setText(String.valueOf(TicketManagement.calculateFare(gotFrom, gotTo)));
            ticketIdValueLabel.setText(String.valueOf(TicketManagement.currentVacantTicketID));
            seatNumberValueLabel.setText(String.valueOf(TicketManagement.vacantSeat + 1));
            bookButton.setDisable(false);
        });
        bookButton.setOnAction(e -> {
            String gotName = nameTextField.getText().trim();
            String gotTo = toTextField.getValue();
            String gotFrom = fromTextField.getValue();
            try {
                TicketManagement.bookTicket(gotFrom, gotTo, gotName);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            mainStage.setScene(mainScene);
            try {
                viewTickets();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        Button cancelButton = new Button("CANCEL");
        cancelButton.setOnAction(e -> mainStage.setScene(mainScene));

        // Set up the layouts
        HBox fromBox = new HBox(10, fromLabel, fromTextField);
        HBox toBox = new HBox(10, toLabel, toTextField);
        HBox nameBox = new HBox(10, nameLabel, nameTextField);
        HBox buttonBox = new HBox(10, checkButton, bookButton, cancelButton);
        HBox fareBox = new HBox(70, fareLabel, fareValueLabel);
        HBox ticketIdBox = new HBox(70, ticketIdLabel, ticketIdValueLabel);
        HBox seatNumberBox = new HBox(70, seatNumberLabel, seatNumberValueLabel);
        VBox centerBox = new VBox(10, fromBox, toBox, nameBox, fareBox, ticketIdBox, seatNumberBox, buttonBox);
        BorderPane borderPane = new BorderPane(centerBox, titleLabel, null, null, null);
        centerBox.setAlignment(Pos.CENTER);
        buttonBox.setAlignment(Pos.CENTER);
        titleLabel.setAlignment(Pos.CENTER);
        borderPane.setPadding(new Insets(12));

        // Set up the scene and show the window
        Scene scene = new Scene(borderPane);
        return scene;
    }

    public static void main(String[] args) {
        launch();
    }
}

class TicketManagement {

    static int totalSeats = 25, vacantSeat = 0;
    static String driverName = " Harri ";
    static ArrayList<Ticket> bookedTickets = new ArrayList<>();
    static ArrayList<String> route = new ArrayList<>(List.of("Amritsar", "Beas", "Jalandhar", "Phagwara", "Ludhiana", "Sirhind", "Ambala", "New Delhi"));
    static int currentVacantTicketID = (int)(Math.random() * 80 + 10) * 100;

    static int getVacantTicketID(){
        int now = currentVacantTicketID;
        currentVacantTicketID++;
        return now;
    }
    static double calculateFare(String from, String to){
        return 200 * Math.abs(route.indexOf(to) - route.indexOf(from));
    }
    static int getVacantSeat(){
        vacantSeat++;
        return vacantSeat;
    }

    static class Ticket {
        public String placeFrom, placeTo, passengerName;

        // auto generated
        int ticketID, seatNumber;
        double fare;

        Ticket(String from, String to, String name){
            this.placeFrom = from;
            this.placeTo = to;
            this.passengerName = name;

            this.ticketID = getVacantTicketID();
            this.fare = calculateFare(from, to);
            this.seatNumber = getVacantSeat();
        }

        String getTicketFormatted() {
            return ticketID + ":" + placeFrom + ":" + placeTo + ":" + passengerName + ":" + fare + ":" + seatNumber;
        }
        public static boolean validateTicketFormat(String readLine) {
            return readLine.matches("^\\d{4}:[\\w ]+:[\\w ]+:[\\w ]+:[\\d\\.]+:\\d+$");
        }

        public static Ticket getTicket(String ticketFormat) {
            String[] tokens = ticketFormat.split(":");

            vacantSeat = Integer.parseInt(tokens[5])-1;
            currentVacantTicketID = Integer.parseInt(tokens[0]);

            Ticket ticket = new Ticket(tokens[1], tokens[2], tokens[3]);
            return ticket;
        }
    }

    static boolean bookTicket(String placeFrom, String placeTo, String passengerName) throws IOException {
        if(vacantSeat == totalSeats){
            return false;
        }

        Ticket ticket = new Ticket(placeFrom, placeTo, passengerName);
        System.out.println(ticket.getTicketFormatted());
        bookedTickets.add(ticket);
        writeTicketstofile();
        return true;
    }

    static void deleteTicket(int index) throws IOException {
        bookedTickets.remove(index);
        writeTicketstofile();
    }

    static File ticketsFile = new File("tickets.txt");
    static {
        try {
            ticketsFile.createNewFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void writeTicketstofile() throws IOException {
        ticketsFile.createNewFile();
        FileWriter ticketsFileWriter = new FileWriter(ticketsFile);
        for(Ticket ticket : bookedTickets) {
            ticketsFileWriter.write(ticket.getTicketFormatted() + '\n');
        }
        ticketsFileWriter.close();
    }

    static void readTicketsfromfile() throws IOException {
        ticketsFile.createNewFile();
        Scanner ticketsFileScanner = new Scanner(ticketsFile);
        ArrayList<Ticket> readBookedTickets = new ArrayList<>();

        while(ticketsFileScanner.hasNextLine()){
            String readLine = ticketsFileScanner.nextLine();
            if(Ticket.validateTicketFormat(readLine)){
                readBookedTickets.add(Ticket.getTicket(readLine));
            }
        }

        ticketsFileScanner.close();

        bookedTickets = readBookedTickets;
    }
}