package com.mycompany.javafxapplication1;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileManagementController {

    @FXML
    private Button createButtonFileID;

    @FXML
    private Button updateButtonFileID;

    @FXML
    private Button deleteFileID;

    @FXML
    private Button viewFileID;
    
    @FXML
    private Button switchToSecondaryButtonID;
    
    @FXML
    private TextField userTextField;
    
    private String username;

    // Main working directory for file storage
    private final String homePath = System.getProperty("user.dir") + "/src/Files";
    
    public String getUsername() {
        return this.userTextField.getText();
    }
    
    public void setUser(String username){
        this.username = username;
        userTextField.setText(username);
    }
    
    @FXML
    private void createFileButtonAction() {
        promptAndCreateFile();
    }

    @FXML
    private void updateFileButtonAction(ActionEvent event) {
        promptAndUpdateFile(event);
    }

    @FXML
    private void deleteFileAction(ActionEvent event) {
        promptAndDeleteFile(event);
    }

    @FXML
    private void viewFileActon(ActionEvent event) {
        promptAndReadFile(event);
    }

    private void promptAndCreateFile() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create File");
        dialog.setHeaderText("Enter the filename to create:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(filename -> {
            try {
                File dir = new File(homePath);
                if (!dir.exists()) dir.mkdirs();

                File file = new File(homePath + "/" + filename);
                if (file.exists()) {
                    showAlert("Warning", "File already exists.");
                } else {
                    String[] cmd = {"x-terminal-emulator", "-e", "bash", "-c", "cd \"" + homePath + "\" && nano \"" + filename + "\""};
                    new ProcessBuilder(cmd).start();
                }
            } catch (IOException e) {
                showAlert("Error", "Error creating file: " + e.getMessage());
            }
        });
    }

    private void promptAndUpdateFile(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Update");
        fileChooser.setInitialDirectory(new File(homePath+"/"+getUsername()));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null && file.exists()) {
            String[] cmd = {
                "x-terminal-emulator", "-e", "bash", "-c",
                "cd \"" + file.getParent() + "\" && nano \"" + file.getName() + "\""
            };
            try {
                new ProcessBuilder(cmd).start();
            } catch (IOException e) {
                showAlert("Error", "Could not open file: " + e.getMessage());
            }
        } else {
            showAlert("Error", "File not selected or does not exist.");
        }
    }

    private void promptAndDeleteFile(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Delete");
        fileChooser.setInitialDirectory(new File(homePath + "/"+getUsername()));

        File file = fileChooser.showOpenDialog(stage);

        if (file != null && file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                showAlert("Success", "File deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete file.");
            }
        } else {
            showAlert("Error", "File not selected or does not exist.");
        }
    }

    private void promptAndReadFile(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Read File");
        File file = fileChooser.showOpenDialog(stage);
        fileChooser.setInitialDirectory(new File(homePath+"/"+ getUsername()));


        if (file != null && file.exists()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                showAlert("File Contents", content.toString());
            } catch (IOException e) {
                showAlert("Error", "Could not read file: " + e.getMessage());
            }
        } else {
            showAlert("Error", "No file selected or file does not exist.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void switchToSecondary(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("secondary.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            ((Stage) switchToSecondaryButtonID.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}