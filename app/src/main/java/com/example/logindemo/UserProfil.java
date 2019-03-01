package com.example.logindemo;

public class UserProfil {

    public String age;
    public String email;
    public String name;

   UserProfil(){

   }

    public UserProfil(String age, String email, String name) {
        this.age = age;
        this.email = email;
        this.name = name;
   }

    public String getAge() {
        return this.age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
