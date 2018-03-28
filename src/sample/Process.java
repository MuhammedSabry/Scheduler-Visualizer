package sample;

public class Process {
    private String name;
    private int time;
    private int priority;
    private int arrivalTime;

    public Process(String pName, int time, int priority, int arrTime) {
        this.name = pName;
        this.time = time;
        this.priority = priority;
        this.arrivalTime = arrTime;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getPriority() {
        return priority;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }
}
