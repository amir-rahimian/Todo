package com.example.myapplication;

public class Task {
    private String title , subTitle ;
    private boolean isDone = false;

    public Task(String title, String subTitle) {
        this.title = title;
        this.subTitle = subTitle;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public boolean isDone() {
        return isDone;
    }
}
