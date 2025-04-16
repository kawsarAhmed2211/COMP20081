/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author ntu-user
 */
public class User {
    private SimpleStringProperty user;
    private SimpleStringProperty pass;

    User(String user, String pass) {
        this.user = new SimpleStringProperty(user);
        this.pass = new SimpleStringProperty(pass);
    }
    
    User(String user){
        this.user = new SimpleStringProperty(user);
;    }
    
    private static User currentUser;
    
    public String getUser() {
        return user.get();
    }

    public void setUser(String user) {
        this.user.set(user);
    }

    public String getPass() {
        return pass.get();
    }

    public void setPass(String pass) {
        this.pass.set(pass);
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
}
