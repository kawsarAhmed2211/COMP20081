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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class DB {

    private static final String DB_URL = "jdbc:sqlite:comp20081.db";
    private static final int TIMEOUT = 30;
    private static final String TABLE_NAME = "Users";
    private String dataBaseFilesTable = "Files";//add delete attribute
    private String dataBaseACLTable = "ACL";
    private String dataBaseAuditTable = "Audit";
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

    public void createUserTable() throws ClassNotFoundException {
        String createQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name String, password String)";
        executeUpdate(createQuery);
    }

    public void deleteUserTable() throws ClassNotFoundException {
        String deleteQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        executeUpdate(deleteQuery);
    }

    public void addUserToTable(String username, String password) throws InvalidKeySpecException, ClassNotFoundException {
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

    public void updateUserPasswordTable(String username, String oldPassword, String newPassword) throws InvalidKeySpecException, ClassNotFoundException {
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

    public ObservableList<User> getAllUsersFromUserTable() throws ClassNotFoundException {
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

    private void executeUpdate(String query, String... params) throws ClassNotFoundException {
    try (Connection connection = getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i + 1, params[i]);
        }

        preparedStatement.executeUpdate();
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
    
    public void createFilesTable() throws ClassNotFoundException {
    String query = "CREATE TABLE IF NOT EXISTS " + dataBaseFilesTable + " (" +
        "file_name TEXT PRIMARY KEY, " +
        "fileSize INTEGER, " +
        "fileOwner TEXT, " +
        "encryptionKey TEXT, " +
        "fileCreationDate TEXT, " +
        "fileModificationDate TEXT, " +
        "delete_flag INTEGER DEFAULT 0, " +
        "FOREIGN KEY(fileOwner) REFERENCES users(username) ON DELETE CASCADE)";
    executeUpdate(query);
}
    
    public void addDataToFilesTable(String file_name, long fileSize, String fileOwner)
            throws InvalidKeySpecException, ClassNotFoundException {

        LocalDate currentDate = LocalDate.now();
        String fileCreationDate = currentDate.format(DateTimeFormatter.ISO_DATE);
        String encryptionKey = generateSalt(32);

        String query = "INSERT INTO " + dataBaseFilesTable +
                " (file_name, fileSize, fileOwner, encryptionKey, fileCreationDate, fileModificationDate) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedstatement = connection.prepareStatement(query)) {

            preparedstatement.setString(1, file_name);
            preparedstatement.setLong(2, fileSize);
            preparedstatement.setString(3, fileOwner);
            preparedstatement.setString(4, encryptionKey);
            preparedstatement.setString(5, fileCreationDate);
            preparedstatement.setString(6, fileCreationDate);

            preparedstatement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteUserFilesTable(String file_name) throws ClassNotFoundException {
        String deleteQuery = "delete from " + dataBaseFilesTable + " Where file_name = '" + file_name + "'";
        executeUpdate(deleteQuery);
    }
    
    public void updateDataInFilesTable(String file_name, long fileSize, boolean delete_flag)
        throws InvalidKeySpecException, ClassNotFoundException {

        int d_flag = delete_flag ? 1 : 0;
        String now = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        String updateQuery = "UPDATE " + dataBaseFilesTable + " SET " +
                "fileModificationDate = ?, fileSize = ?, delete_flag = ? " +
                "WHERE file_name = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedSatement = connection.prepareStatement(updateQuery)) {

            preparedSatement.setString(1, now);
            preparedSatement.setLong(2, fileSize);
            preparedSatement.setInt(3, d_flag);
            preparedSatement.setString(4, file_name);

            preparedSatement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    public ObservableList<FilesInitialisationClass> getDataFromFilesTable() throws ClassNotFoundException {
        ObservableList<FilesInitialisationClass> files = FXCollections.observableArrayList();
        String query = "SELECT * FROM " + dataBaseFilesTable;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.setQueryTimeout(TIMEOUT);
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                LocalDate creationDate = LocalDate.parse(rs.getString("fileCreationDate"), DateTimeFormatter.ISO_DATE);
                LocalDate modificationDate = LocalDate.parse(rs.getString("fileModificationDate"), DateTimeFormatter.ISO_DATE);
                boolean deleteFlag = rs.getInt("delete_flag") != 0;

                files.add(new FilesInitialisationClass(
                    rs.getString("file_name"),
                    rs.getLong("fileSize"),
                    rs.getString("fileOwner"),
                    rs.getString("encryptionKey"),
                    creationDate,
                    modificationDate,
                    deleteFlag
                ));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return files;
    }
    
    /*public ObservableList<FilesInitialisationClass> getAllDataFromFilesTable() throws ClassNotFoundException {
        ObservableList<FilesInitialisationClass> result = FXCollections.observableArrayList();
        String query = "SELECT * FROM " + this.dataBaseFilesTable;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.setQueryTimeout(TIMEOUT);
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                LocalDate fileCreationDate = LocalDate.parse(rs.getString("fileCreationDate"), DateTimeFormatter.ISO_DATE);
                LocalDate fileModificationDate = LocalDate.parse(rs.getString("fileModificationDate"), DateTimeFormatter.ISO_DATE);
                boolean deleteFlag = rs.getInt("delete_flag") != 0;

                result.add(new FilesInitialisationClass(
                    rs.getString("file_name"),
                    rs.getLong("fileSize"),
                    rs.getString("fileOwner"),
                    rs.getString("encryption_key"),
                    fileCreationDate,
                    fileModificationDate,
                    deleteFlag
                ));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }*/

    public FilesInitialisationClass getFileDataFromFilesTable(String filename) throws ClassNotFoundException {
        FilesInitialisationClass result = null;
        String query = "SELECT * FROM " + this.dataBaseFilesTable + " WHERE file_name = ?";

        try (Connection conn =getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setQueryTimeout(TIMEOUT);
            preparedStatement.setString(1, filename);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                LocalDate creationDate = LocalDate.parse(rs.getString("creation_date"), DateTimeFormatter.ISO_DATE);
                LocalDate modificationDate = LocalDate.parse(rs.getString("modification_date"), DateTimeFormatter.ISO_DATE);
                boolean deleteFlag = rs.getInt("delete_flag") != 0;

                result = new FilesInitialisationClass(
                    rs.getString("file_name"),
                    rs.getLong("size"),
                    rs.getString("owner"),
                    rs.getString("encryption_key"),
                    creationDate,
                    modificationDate,
                    deleteFlag
                );
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public boolean isFileNameUnavailable(String fileName) throws ClassNotFoundException {
        boolean unavailable = false;
        String query = "SELECT file_name FROM " + this.dataBaseFilesTable + " WHERE file_name = ?";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setQueryTimeout(TIMEOUT);
            preparedStatement.setString(1, fileName);
            ResultSet rs = preparedStatement.executeQuery();

            unavailable = rs.next();

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return unavailable;
    }
    
    public void createACLDatabaseTable() throws ClassNotFoundException {
    String query = "CREATE TABLE IF NOT EXISTS access_control_list (\n" +
        "    Granter TEXT,\n" +
        "    Grantee TEXT,\n" +
        "    file_name TEXT,\n" +
        "    fileOwner TEXT,\n" +
        "    Permission TEXT,\n" +
        "    PRIMARY KEY (Grantee, file_name),\n" +
        "    FOREIGN KEY (file_name) REFERENCES files(file_name) ON DELETE CASCADE,\n" +
        "    FOREIGN KEY (fileOwner) REFERENCES users(username) ON DELETE CASCADE\n" +
        ")";
    executeUpdate(query);
}

    
    public void addDataToACLTable(String granter, String grantee, String file_name, String fileOwner, String permission)
    throws InvalidKeySpecException, ClassNotFoundException {
        String query = "INSERT INTO " + dataBaseACLTable +
                " (Granter, Grantee, file_name, fileOwner, Permission) " +
                "VALUES (?, ?, ?, ?, ?)";
        executeUpdate(query, granter, grantee, file_name, fileOwner, permission);
    }

    
    public void deleteDataFromACLTable(String grantee, String file_name)
            throws InvalidKeySpecException, ClassNotFoundException{
        String query = "DELETE FROM " + dataBaseACLTable +
               " WHERE file_name = '" + file_name + "' AND grantee = '" + grantee + "'";

        executeUpdate(query, file_name, grantee);
    }
    
    public ACL getACLDataForAFileAndUserFromACLTable(String grantee, String file_name) 
        throws ClassNotFoundException {
        ACL result = null;
        String query = "SELECT * FROM " + this.dataBaseACLTable + 
                " WHERE file_name = ? AND grantee = ?";

        try (Connection conn = getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setQueryTimeout(TIMEOUT);
            preparedStatement.setString(1, file_name);
            preparedStatement.setString(2, grantee);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                result = new ACL(
                    rs.getString("granter"),
                    rs.getString("grantee"),
                    rs.getString("file_name"),
                    rs.getString("fileOwner"),
                    rs.getString("permission")
                );
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    public ObservableList<ACL> getDataFromACLTable() throws ClassNotFoundException {
    ObservableList<ACL> acl = FXCollections.observableArrayList();
        String query = "SELECT * FROM " + dataBaseACLTable;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.setQueryTimeout(TIMEOUT);
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                acl.add(new ACL(
                    rs.getString("granter"),
                    rs.getString("grantee"),
                    rs.getString("file_name"),
                    rs.getString("fileOwner"),
                    rs.getString("permission")
                ));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return acl;
    }
    
    
    public void createAudiTrailTable()throws ClassNotFoundException{
        String query = "CREATE TABLE IF NOT EXISTS " + dataBaseAuditTable + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user TEXT, " +
                "modification TEXT, " +
                "file_name TEXT, " +
                "dateOfModification TEXT, " +
                "FOREIGN KEY (user) REFERENCES users(username) ON DELETE CASCADE" +
                ")";
        executeUpdate(query);
    }
    
        public void addAuditTrailData(String username, String modification, String fileName, String dateOfModification)
            throws ClassNotFoundException {
            String query = "INSERT INTO " + dataBaseAuditTable + 
                           " (user, modification, file_name, dateOfModification) " +
                           "VALUES (?, ?, ?, ?)";
            executeUpdate(query, username, modification, fileName, dateOfModification);
        }
        
        public ObservableList<AuditTrail> getDataFromAuditTrail() throws ClassNotFoundException {
            ObservableList<AuditTrail> auditTrails = FXCollections.observableArrayList();
            String query = "SELECT user, modification, file_name, dateOfModification FROM " + dataBaseAuditTable;

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.setQueryTimeout(TIMEOUT);
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    String username = rs.getString("user");
                    String modification = rs.getString("modification");
                    String fileName = rs.getString("file_name");
                    String dateStr = rs.getString("dateOfModification");

                    LocalDate dateAfterModified = LocalDate.parse(dateStr); // assumes format is yyyy-MM-dd

                    auditTrails.add(new AuditTrail(username, modification, fileName, dateAfterModified));
                }

            } catch (SQLException ex) {
                Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            }

            return auditTrails;
        }






}
