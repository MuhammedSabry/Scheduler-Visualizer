package sample.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static sample.controller.QueueController.isNumeric;
import static sample.controller.QueueController.validate;

public class SelectorController {

    static String FCFS = "First Come First Served";
    static String SJF = "Shortest Job First";
    static String RR = "Round Robin";
    static String PRIORITY = "Priority";
    static int TIME_QUANTUM;
    private String PREEMPTIVE = "Preemptive";
    private String NON_PREEMPTIVE = "Non-Preemptive";

    static boolean preemptive = false;
    static String schedulerType;

    public TextField RR_Time;
    public Button next;
    public ChoiceBox scheduler_permittivity;
    public ChoiceBox scheduler_type;

    public void initialize() {

        scheduler_type.setItems(FXCollections.observableArrayList(FCFS, SJF, PRIORITY, RR));
        scheduler_permittivity.setItems(FXCollections.observableArrayList(PREEMPTIVE, NON_PREEMPTIVE));
        scheduler_permittivity.setValue(NON_PREEMPTIVE);

        //Listener for the next button and validating all fields + styling
        next.setOnMouseClicked(event -> {
            if (scheduler_type.getValue() != null) {
                if (scheduler_type.getValue().equals(RR) && RR_Time.getText() != null) {
                    if (isNumeric(RR_Time.getText(), 1)) {
                        TIME_QUANTUM = Integer.parseInt(RR_Time.getText());
                    } else {
                        RR_Time.setStyle("-fx-focus-color: RED;");
                        RR_Time.requestFocus();
                        return;
                    }
                } else if (scheduler_type.getValue().equals(RR)) {
                    RR_Time.setStyle("-fx-focus-color: RED;");
                    RR_Time.requestFocus();
                    return;
                }
                preemptive = scheduler_permittivity.getValue() == PREEMPTIVE;
                schedulerType = (String) scheduler_type.getValue();

                Parent data = null;
                try {
                    data = FXMLLoader.load(getClass().getResource("/sample/view/queue.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Stage stage = (Stage) next.getScene().getWindow();
                assert data != null;
                Scene scene = new Scene(data);
                stage.setScene(scene);

            } else {
                scheduler_type.setStyle("-fx-focus-color: RED;");
                scheduler_type.requestFocus();
            }
        });

        //Just a listener on scheduler type for styling and activating
        // or disabling the fields accordingly
        scheduler_type.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                scheduler_type.styleProperty().setValue("");
                String value = (String) newValue;
                if (value.equals(SJF) || value.equals(PRIORITY)) {
                    RR_Time.setDisable(true);
                    scheduler_permittivity.setDisable(false);
                } else if (value.equals(RR)) {
                    scheduler_permittivity.setDisable(true);
                    RR_Time.setDisable(false);
                } else {
                    scheduler_permittivity.setDisable(true);
                    RR_Time.setDisable(true);
                }
            }
        });

        RR_Time.textProperty().addListener((observable, oldValue, newValue) -> validate(RR_Time, 1));
    }
}
