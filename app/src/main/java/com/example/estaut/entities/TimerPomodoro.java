package com.example.estaut.entities;

public class TimerPomodoro {

    //atributos
    private long timePomodoro;
    private long timeBreak;
    private double volume;

    public TimerPomodoro(long timePomodoro, long timeBreak, double volume){
        this.timePomodoro = timePomodoro;
        this.timeBreak = timeBreak;
        this.volume = volume;
    }

    public long getTimePomodoro() {
        return timePomodoro;
    }

    public void setTimePomodoro(long timePomodoro) {
        this.timePomodoro = timePomodoro;
    }

    public long getTimeBreak() {
        return timeBreak;
    }

    public void setTimeBreak(long timeBreak) {
        this.timeBreak = timeBreak;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

}
