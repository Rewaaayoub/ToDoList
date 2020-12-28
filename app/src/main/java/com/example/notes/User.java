package com.example.notes;

public class User {
    private String  email  ;
    private int id = 0;
    private String name;


    public User( String name , String email) {
        this.email = email;
        this.id = ++id;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User() {
    }


}
