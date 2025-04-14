/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author kawsar
 */



import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

public class TerminalController implements Initializable {

    @FXML
    private TextField terminalCommandTextFieldID;

    @FXML
    private TextArea terminalCommandTextAreaID;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic
    }

    @FXML
    private void terminalCommandTextFieldAction(ActionEvent event) {
        System.out.println("Enter key pressed in terminalCommandTextField!");
        // Optional: execute command here
    }

    @FXML
    private void terminalExecuteButtonAction(ActionEvent event) {
        System.out.println("Execute button pressed!");
        // Optional: handle command execution here
    }
    
    @FXML
    private void switchToSecondary(ActionEvent event){
        System.out.println("Execute button pressed!");
    }
}
