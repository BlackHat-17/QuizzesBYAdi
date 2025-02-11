package com.example.quizesbyadi;

public class User {
    public String name;
    public String email;
    public String address;
    public String mob;
    public String genderval;
    public String password;
    public String cpassword;

    // Default Constructor (Required for Firebase)
    public User() {}

    // Constructor
    public User(String name, String email, String address, String mob, String genderval, String password, String cpassword) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.mob = mob;
        this.genderval = genderval;
        this.password = password;
        this.cpassword = cpassword;
    }
}
