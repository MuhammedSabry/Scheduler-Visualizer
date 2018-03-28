package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sample.Process;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static sample.controller.SelectorController.*;

public class QueueController implements Initializable {

    static ObservableList<Process> processes;
    public TableView<Process> processes_list;
    public TextField priority_field;
    public Button add;
    public TextField process_name;
    public TextField process_time;
    public Button next;
    public TextField arrivalTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Initializing processes list
        processes = FXCollections.observableArrayList();

        //Initializing TableView columns
        TableColumn<Process, String> col2 = new TableColumn<>("Process");
        col2.setCellValueFactory(new PropertyValueFactory<>("name"));
        col2.setSortable(false);

        TableColumn<Process, Integer> col3 = new TableColumn<>("Time");
        col3.setCellValueFactory(new PropertyValueFactory<>("time"));
        col3.setSortable(false);

        TableColumn<Process, Integer> col4 = new TableColumn<>("Arrival Time");
        col4.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        col4.setSortable(false);
        col4.setMinWidth(50);

        TableColumn<Process, Integer> col5 = new TableColumn<>("Priority");
        col5.setCellValueFactory(new PropertyValueFactory<>("priority"));
        col5.setSortable(false);

        processes_list.getColumns().setAll(col2, col3, col4, col5);

        //Checking if we should disable the priority field
        if (schedulerType.equals(PRIORITY)) {
            priority_field.setDisable(false);
        } else {
            priority_field.setText("0");
            priority_field.setDisable(true);
        }

        //Listener for the add process button
        add.setOnMouseClicked(event -> {
            String pName = process_name.getText();
            String pTime = process_time.getText();
            String priority = priority_field.getText();
            String aTime = arrivalTime.getText();
            if (pName != null && pTime != null && priority != null) {
                if (isNumeric(pTime, 1) && isNumeric(priority, 0) && isNumeric(aTime, 0)) {
                    Process process = new Process(pName, Integer.parseInt(pTime), Integer.parseInt(priority), Integer.parseInt(aTime));
                    processes.add(process);
                    processes_list.setItems(processes);
                    process_name.setText("P" + (processes.size() + 1));
                } else if (!isNumeric(pTime, 1))
                    validate(process_time, 1);
                else if (!isNumeric(aTime, 0)) validate(arrivalTime, 0);
                else validate(priority_field, 0);
            }
        });

        //Adding listener for the Next button so we proceed with the next screen
        next.setOnMouseClicked(event -> {
            if (!processes.isEmpty()) {
                Parent data = null;
                try {
                    data = FXMLLoader.load(getClass().getResource("/sample/view/view.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Stage stage = (Stage) next.getScene().getWindow();
                assert data != null;
                Scene scene = new Scene(data);
                stage.setScene(scene);
            } else {
                processes_list.setStyle("-fx-focus-color:RED");
                processes_list.requestFocus();
            }
        });

        //Adding listeners for the buttons and text fields (Just for styling)
        processes.addListener((ListChangeListener<Process>) c -> {
            if (c.getList().size() > 0) processes_list.setStyle("");
        });
        arrivalTime.textProperty().addListener((observable, oldValue, newValue) -> validate(arrivalTime, 0));
        process_time.textProperty().addListener((observable, oldValue, newValue) -> validate(process_time, 1));
        priority_field.textProperty().addListener((observable, oldValue, newValue) -> validate(priority_field, 0));
    }

    /**
     * checking if the given String is a number or not
     * also checking if that number is bigger than the given int @Param :compare
     */
    static boolean isNumeric(String time, int compare) {
        int inti;
        try {
            inti = Integer.parseInt(time);
        } catch (Exception e) {
            return false;
        }
        return inti >= compare;
    }

    /**
     * Styling method to check if a given field matches the properties
     * and style it accordingly
     */
    static void validate(TextField textField, int compare) {
        if (textField.getText().isEmpty() || !isNumeric(textField.getText(), compare)) {
            {
                textField.setStyle("-fx-focus-color:RED");
                textField.requestFocus();
            }
        } else {
            textField.setStyle("");
        }
    }
}
