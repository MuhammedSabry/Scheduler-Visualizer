package sample.controller;

import static sample.controller.QueueController.processes;
import static sample.controller.SelectorController.*;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import sample.Process;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ViewController implements Initializable {
    public Label avgTime;
    public HBox queue_chart_box;
    public Button repeat;
    private int currentTime;
    private HashMap<Process, Integer> waitingTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Initializing the waitingTime map with negative the processes times
        waitingTime = new HashMap<>(processes.size());
        for (Process process : processes)
            waitingTime.put(process, -process.getTime());

        //Sorting processes by arrival time
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        currentTime = processes.get(0).getArrivalTime();

        //Scheduling the drawing and printing to the final screen later so the application
        //won't hangup on the Process addition screen
        Platform.runLater(() -> {

            //Picking the right scheduler
            if (schedulerType.equals(FCFS))
                firstComeFirstServeScheduler();
            else if (schedulerType.equals(SJF) && preemptive)
                shortestJobFirstScheduler(true);
            else if (schedulerType.equals(SJF))
                shortestJobFirstScheduler(false);
            else if (schedulerType.equals(RR))
                roundRobinScheduler();
            else if (preemptive)
                priorityScheduler(true);
            else
                priorityScheduler(false);

            //Calculating the total average waiting time
            double avg = 0;
            Set<Map.Entry<Process, Integer>> entries = waitingTime.entrySet();
            for (Map.Entry<Process, Integer> entry : entries) {
                avg += entry.getValue() - entry.getKey().getArrivalTime();
            }
            //Printing the avg time to label
            avgTime.setText(avgTime.getText() + " " + avg / processes.size());
        });

        //Listener on repeat button to restart the whole application
        repeat.setOnMouseClicked(event -> {

            Parent view = null;
            try {
                view = FXMLLoader.load(getClass().getResource("/sample/view/selector.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Checking that the view isn't null and starting the app from beginning
            assert view != null;
            Stage stage = (Stage) avgTime.getScene().getWindow();
            Scene scene = new Scene(view);
            stage.setScene(scene);

        });

    }

    private void firstComeFirstServeScheduler() {
        for (Process process : processes) {
            int time = process.getTime();
            process.setTime(0);
            draw(process, time);
        }
    }

    private void shortestJobFirstScheduler(boolean preemptive) {
        if (preemptive)
            schedulePreemptive(SJF);
        else {
            processes.sort(Comparator.comparingInt(Process::getTime));
            scheduleNormally();
        }
    }

    private void priorityScheduler(boolean preemptive) {
        processes.sort(Comparator.comparingInt(Process::getPriority));
        if (preemptive)
            schedulePreemptive(PRIORITY);
        else
            scheduleNormally();
    }

    private void schedulePreemptive(String by) {
        boolean flag = true;
        while (flag) {
            flag = false;
            int time = 0;
            if (by.equals(SJF))
                processes.sort(Comparator.comparingInt(Process::getTime));
            for (Process process : processes) {
                if (process.getTime() != 0 && process.getArrivalTime() <= currentTime) {
                    if (by.equals(PRIORITY)) {
                        while (isMoreImportant(process, time) && process.getTime() > time)
                            time++;
                    } else {
                        while (isNotShorter(process, time) && process.getTime() > time)
                            time++;
                    }
                    if (process.getTime() == time) {
                        process.setTime(0);
                        draw(process, time);
                    } else {
                        process.setTime(process.getTime() - time);
                        draw(process, time);
                    }
                    flag = true;
                    break;
                }
            }
        }
    }

    private void roundRobinScheduler() {
        boolean flag = true;
        while (flag) {
            flag = false;
            for (Process process : processes) {
                int time = process.getTime();
                if (time != 0) {
                    flag = true;
                    if (TIME_QUANTUM <= time) {
                        process.setTime(time - TIME_QUANTUM);
                        draw(process, TIME_QUANTUM);
                    } else {
                        process.setTime(0);
                        draw(process, time);
                    }
                }
            }
        }
    }

    private void scheduleNormally() {
        boolean flag = true;
        while (flag) {
            flag = false;
            for (Process process : processes) {
                if (process.getArrivalTime() <= currentTime && process.getTime() != 0) {
                    flag = true;
                    int time = process.getTime();
                    process.setTime(0);
                    draw(process, time);
                    break;
                }
            }
        }
    }

    private void draw(Process process, int end) {
        Label startTime = new Label(String.valueOf(currentTime));

        Label processName = new Label("<- " + process.getName() + " ->");
        processName.setFont(Font.font(16));
        Label endTime = new Label(String.valueOf(currentTime + end));

        HBox box = new HBox(startTime, processName, endTime, new Separator(Orientation.VERTICAL));
        box.setSpacing(8);
        box.setPadding(new Insets(8, 8, 8, 8));
        box.setAlignment(Pos.CENTER);

        queue_chart_box.getChildren().add(box);
        currentTime += end;
        if (process.getTime() == 0)
            waitingTime.put(process, currentTime + waitingTime.get(process));
    }

    private boolean isMoreImportant(Process lowProcess, int time) {
        for (Process process : processes)
            if (process.getPriority() < lowProcess.getPriority() && process.getArrivalTime() <= currentTime + time && process.getTime() != 0)
                return false;
        return true;
    }

    private boolean isNotShorter(Process shorter, int time) {
        for (Process process : processes)
            if (process.getTime() < shorter.getTime() && process.getArrivalTime() <= currentTime + time && process.getTime() != 0)
                return false;
        return true;
    }
}
