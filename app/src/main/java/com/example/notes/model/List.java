package com.example.notes.model;

import java.io.Serializable;

public class List implements Serializable {

    private String name;
    private String id;
    private int taskNumber = 0;

    public List(String name, int taskNumber) {
        this.name = name;
        this.taskNumber = taskNumber;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(int taskNumber) {
        this.taskNumber = taskNumber;
    }
}
