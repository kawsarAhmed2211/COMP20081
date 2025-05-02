package com.mycompany.javafxapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TerminalController {

    private String cred;
    
    @FXML
    private TextField userTextField; // Holds username (e.g., fetched from DB)

    @FXML
    private TextField terminalCommandTextFieldID;

    @FXML
    private Button terminalExecuteButtonID;

    @FXML
    private TextArea terminalCommandTextAreaID;

    @FXML
    private Button switchToSecondaryButtonID;

    private String username;
    
    private File currentDirectory;
    
    private final String homePath = System.getProperty("user.dir") + "/src/Files";

   public void initialise(String credentials) {
        this.cred = credentials;
        userTextField.setText(credentials);
    }

    public String getUsername() {
        return this.userTextField.getText();
    }

    private File getOrCreateUserDirectory() {
        File userDir = new File(homePath+ "/"+getUsername());
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
        if (currentDirectory == null) {
            currentDirectory = userDir;
        }
        return userDir;
    }

    // ✅ Triggered when Execute button is pressed
    @FXML
private void terminalExecuteButtonAction(ActionEvent event) {
    String command = terminalCommandTextFieldID.getText().trim();

    if (command.isEmpty()) {
        terminalCommandTextAreaID.setText("Please enter a command.");
        return;
    }

    // Ensure user directory exists
    File userDir = getOrCreateUserDirectory();

    if (command.startsWith("nano ")) {
        // Handle launching nano in a new terminal
        try {
            String[] cmdArray = {
                "x-terminal-emulator", // or use "terminator", "gnome-terminal", or "xterm"
                "-e",
                "bash", "-c", "cd \"" + currentDirectory.getAbsolutePath() + "\" && " + command
            };
            new ProcessBuilder(cmdArray).start();
        } catch (IOException e) {
            terminalCommandTextAreaID.setText("Error launching nano: " + e.getMessage());
            e.printStackTrace();
        }
        return;
    }
    
    if(command.startsWith("whoami")){
        String name = userTextField.getText();
        terminalCommandTextAreaID.setText(name);
        return;

    }

    // Regular command execution in currentDirectory
    try {
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
        processBuilder.directory(currentDirectory);  // Maintains working directory
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder outputBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            outputBuilder.append(line).append("\n");
        }

        // Handle stderr
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }

        if (errorOutput.length() > 0) {
            terminalCommandTextAreaID.setText("Error: " + errorOutput);
        } else {
            terminalCommandTextAreaID.setText(outputBuilder.toString());
        }

        // Update current directory if 'cd' is used
        if (command.startsWith("cd ")) {
            String targetDir = command.substring(3).trim();
            File newDir = new File(currentDirectory, targetDir).getCanonicalFile();
            if (newDir.exists() && newDir.isDirectory() && newDir.getAbsolutePath().startsWith(getOrCreateUserDirectory().getAbsolutePath())) {
                currentDirectory = newDir;
            } else {
                terminalCommandTextAreaID.setText("Error: Directory not found or access denied.");
            }
        }

    } catch (IOException e) {
        terminalCommandTextAreaID.setText("Error executing command: " + e.getMessage());
        e.printStackTrace();
    }
}


    // ✅ Switch FXML view
    @FXML
    private void switchToSecondary(ActionEvent event) {
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
