/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

/**
 *
 * @author kawsar
 */

public class ACL {
    private String granter;
    private String grantee;
    private String fileName;    
    private String fileOwner;
    private String permission;

    public ACL(String granter, String grantee, String fileName, String fileOwner, String permission) {
        this.granter = granter;
        this.grantee = grantee;
        this.fileName = fileName;
        this.fileOwner = fileOwner;
        this.permission = permission;
    }

    public String getGranter() {
        return granter;
    }

    public void setGranter(String granter) {
        this.granter = granter;
    }

    public String getGrantee() {
        return grantee;
    }

    public void setGrantee(String grantee) {
        this.grantee = grantee;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileOwner() {
        return fileOwner;
    }

    public void setFileOwner(String fileOwner) {
        this.fileOwner = fileOwner;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
