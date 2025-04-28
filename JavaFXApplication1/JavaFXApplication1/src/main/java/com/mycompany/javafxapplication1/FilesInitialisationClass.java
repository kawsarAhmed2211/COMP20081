/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.time.LocalDate;

/**
 *
 * @author kawsar
 */
public class FilesInitialisationClass {
    private String fileName;
    private long fileSize;
    private String fileOwner;
    private String encryptionKey;
    private LocalDate fileCreationDate;
    private LocalDate fileModificationDate;
    private boolean deleteFlag;

    public FilesInitialisationClass(String fileName, long fileSize, 
            String fileOwner, String encryptionKey, LocalDate fileCreationDate, 
            LocalDate fileModificationDate, boolean deleteFlag) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileOwner = fileOwner;
        this.encryptionKey = encryptionKey;
        this.fileCreationDate = fileCreationDate;
        this.fileModificationDate = fileModificationDate;
        this.deleteFlag = deleteFlag;
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileOwner() {
        return fileOwner;
    }

    public void setFileOwner(String fileOwner) {
        this.fileOwner = fileOwner;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public LocalDate getFileCreationDate() {
        return fileCreationDate;
    }

    public void setFileCreationDate(LocalDate fileCreationDate) {
        this.fileCreationDate = fileCreationDate;
    }

    public LocalDate getFileModificationDate() {
        return fileModificationDate;
    }

    public void setFileModificationDate(LocalDate fileModificationDate) {
        this.fileModificationDate = fileModificationDate;
    }

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }
}

