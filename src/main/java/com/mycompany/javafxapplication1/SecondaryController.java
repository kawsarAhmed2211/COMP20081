package com.mycompany.javafxapplication1;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;



public class SecondaryController {
    
    private String cred;
    
    @FXML
    private TextField userTextField;
    
    @FXML
    private TableView dataTableView;

    @FXML
    private Button secondaryButton;
    
    @FXML
    private Button refreshBtn;
    
    @FXML
    private TextField customTextField;
    
    @FXML
    private Button terminalButton;
    
    @FXML
    private Button fileManagementButton;
    
    @FXML
    private Button updatePasswordButtonID;
    
    @FXML
    private void RefreshBtnHandler(ActionEvent event){
        Stage primaryStage = (Stage) customTextField.getScene().getWindow();
        customTextField.setText((String) primaryStage.getUserData());
    }
        
    @FXML
    private void switchToPrimary(){
        Stage secondaryStage = new Stage();
        Stage primaryStage = (Stage) secondaryButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("primary.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 640, 480);
            secondaryStage.setScene(scene);
            secondaryStage.setTitle("Login");
            secondaryStage.show();
            primaryStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialise(String[] credentials) {
        this.cred = credentials[0];
        userTextField.setText(credentials[0]);
        customTextField.setText("Users");
        DB myObj = new DB();
        ObservableList<User> data;
        try {
            data = myObj.getAllUsersFromUserTable();
            TableColumn user = new TableColumn("User");
        user.setCellValueFactory(
        new PropertyValueFactory<>("user"));

        TableColumn pass = new TableColumn("Pass");
        pass.setCellValueFactory(
            new PropertyValueFactory<>("pass"));
        dataTableView.setItems(data);
        dataTableView.getColumns().addAll(user, pass);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SecondaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void switchToTerminal(ActionEvent event){
        //Stage secondaryStage = new Stage();
        Stage terminalStage = (Stage) terminalButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("terminal.fxml"));
            Parent root = loader.load();
            TerminalController terminalController = loader.getController();
            terminalController.initialise(this.cred);
            
            Scene scene = new Scene(root, 640, 480);
            terminalStage.setScene(scene);
            //secondaryStage.setTitle("Login");
            terminalStage.show();
            //secondaryStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void switchToFileManagementController(ActionEvent event){
        Stage secondaryStage = new Stage();
        Stage fileControllerStage = (Stage) fileManagementButton.getScene().getWindow();
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fileManagement.fxml"));
            Parent root = loader.load();
            FileManagementController fileManagementController = loader.getController();
            
            Scene scene = new Scene(root, 640, 480);
            fileControllerStage.setScene(scene);
            fileManagementController.setUser(userTextField.getText());
            fileControllerStage.show();
            secondaryStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void updatePasswordButtonAction(ActionEvent event) {
        Stage stage = (Stage) updatePasswordButtonID.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("update.fxml"));
            Parent root = loader.load();

            // Optionally pass data to the update controller
            UpdateController updateController = loader.getController();
            //updateController.initialise(userTextField.getText()); // example if you have an init method

            Scene scene = new Scene(root, 640, 480);
            stage.setScene(scene);
            stage.setTitle("Update Password");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
