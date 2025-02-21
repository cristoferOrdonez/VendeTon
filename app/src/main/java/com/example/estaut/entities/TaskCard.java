package com.example.estaut.entities;

public class TaskCard {

    //atirbutos
    private String task;
    private boolean completed;

    public TaskCard(String task, boolean completed){

        this.task = task;
        this.completed = completed;

    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getTask() {
        return task;
    }

    public boolean isCompleted() {
        return completed;
    }
}
