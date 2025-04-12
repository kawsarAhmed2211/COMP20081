package com.mycompany.javafxapplication1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DB {

    private static final String DB_URL = "jdbc:sqlite:comp20081.db";
    private static final int TIMEOUT = 30;
    private static final String TABLE_NAME = "Users";

    private final Random random = new SecureRandom();
    private final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int iterations = 10000;
    private final int keyLength = 256;
    private String saltValue;

    public DB() {
        try {
            File saltFile = new File(".salt");
            if (!saltFile.exists()) {
                saltValue = generateSalt(30);
                try (FileWriter writer = new FileWriter(saltFile)) {
                    writer.write(saltValue);
                }
            } else {
                try (Scanner scanner = new Scanner(saltFile)) {
                    if (scanner.hasNextLine()) {
                        saltValue = scanner.nextLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createTable() throws ClassNotFoundException {
        String createQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name String, password String)";
        executeUpdate(createQuery);
    }

    public void deleteTable() throws ClassNotFoundException {
        String deleteQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        executeUpdate(deleteQuery);
    }

    public void addUser(String username, String password) throws InvalidKeySpecException, ClassNotFoundException {
        String addQuery = "INSERT INTO " + TABLE_NAME + " (name, password) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedstatement = connection.prepareStatement(addQuery)) {

            preparedstatement.setString(1, username);
            preparedstatement.setString(2, generateSecurePassword(password));
            preparedstatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean validateUser(String username, String password) throws InvalidKeySpecException, ClassNotFoundException {
        String query = "SELECT password FROM " + TABLE_NAME + " WHERE name = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password");
                return hashedPassword.equals(generateSecurePassword(password));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void updatePassword(String username, String oldPassword, String newPassword) throws InvalidKeySpecException, ClassNotFoundException {
        if (!validateUser(username, oldPassword)) {
            System.out.println("Incorrect current password. Try again.");
            return;
        }

        String updateQuery = "UPDATE " + TABLE_NAME + " SET password = ? WHERE name = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, generateSecurePassword(newPassword));
            preparedStatement.setString(2, username);
            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Password updated successfully.");
            } else {
                System.out.println("Update failed.");
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ObservableList<User> getAllUsers() throws ClassNotFoundException {
        ObservableList<User> users = FXCollections.observableArrayList();
        String query = "SELECT name, password FROM " + TABLE_NAME;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.setQueryTimeout(TIMEOUT);
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                users.add(new User(rs.getString("name"), rs.getString("password")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return users;
    }

    private String generateSalt(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private byte[] hash(char[] password, byte[] salt) throws InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        Arrays.fill(password, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }
    }

    public String generateSecurePassword(String password) throws InvalidKeySpecException {
        byte[] hashed = hash(password.toCharArray(), saltValue.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hashed);
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection(DB_URL);
    }

    private void executeUpdate(String query) throws ClassNotFoundException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(TIMEOUT);
            statement.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void log(String message) {
        System.out.println(message);
    }

    public String getTableName() {
        return TABLE_NAME;
    }
}
