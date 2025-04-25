/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.time.LocalDate;
import java.util.Date;

/**
 *
 * @author kawsar
 */
public class AuditTrail {
    private String username;
    private String modificationDate;
    private String file_name;
    private LocalDate dateAfterModified;
    
    
     public AuditTrail(String username, String modificationDate, String file_name, LocalDate dateAfterModified) {
        this.username = username;
        this.modificationDate = modificationDate;
        this.file_name = file_name;
        this.dateAfterModified = dateAfterModified;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public LocalDate getDateAfterModified() {
        return dateAfterModified;
    }

    public void setDateAfterModified(LocalDate dateAfterModified) {
        this.dateAfterModified = dateAfterModified;
    }
}
