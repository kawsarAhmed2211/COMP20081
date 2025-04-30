/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import java.io.IOException;
import java.lang.ProcessBuilder;
import java.lang.Process;
import java.io.File;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.ZipFile;

/**
 *
 * @author kawsar
 */
public class ContainerClass {
    private static final String[] containers_name = {
        "javafxapplication1_file-user-container1_1",
        "javafxapplication1_file-user-container2_1",
        "javafxapplication1_file-user-container3_1",
        "javafxapplication1_file-user-container4_1",
        "javafxapplication1_file-user-container5_1",
        "javafxapplication1_file-user-container6_1"
    };

    private static final String[] REMOTE_HOST = {
        "localhost", // or "127.0.0.1" because Docker exposes on your machine
        "localhost",
        "localhost",
        "localhost",
        "localhost",
        "localhost"
    };

    private static final String[] USERNAME = {
        "root", "root", "root", "root", "root", "root"
    };

    private static final String[] PASSWORD = {
        "rootpassword", "rootpassword", "rootpassword", "rootpassword", "rootpassword", "rootpassword"
    };

    private static final int[] REMOTE_PORT = {
        5000 ,5001, 5002, 5003, 5004, 5005
    };

    private static final boolean[] working_containers = {
        true, true, true, true, true, true
    };

    private static final int SESSION_TIMEOUT = 10000;
    private static final int CHANNEL_TIMEOUT = 5000;
    
    public int numberOfContainers(){
        int NOContainers = 0;
        for (int i = 0; i < working_containers.length; i++) {
            if(working_containers[i]){
                NOContainers++;
            }
        }
        return NOContainers;
    }
    
    public String[] containerDetail(String containerName){
        for (int i = 0; i < working_containers.length; i++) {
            if((containerName.equals(containers_name[i]))&&(working_containers[i])){
                String[] details = {REMOTE_HOST[i], USERNAME[i],PASSWORD[i]};
                return details;
            }
        }
        String[] details = {"","",""};
        return details;
    }
    
    public boolean containerExist(String containerName){
       for (int i = 0; i < working_containers.length; i++) {
            if((containerName.equals(containers_name[i]))&&(working_containers[i])){
                return true;
            }
        }
       return false;
   }
   
    public void chunkfile(String fileName){
        ProcessBuilder splitProcessBuilder = new ProcessBuilder("bash", "-c", 
                "split -n " + Integer.toString(this.numberOfContainers()) +  " -d "+ fileName + " " +fileName);
        try{
           Process splitProcess = splitProcessBuilder.start();
            int splitExitCode = splitProcess.waitFor();
            if (splitExitCode != 0) {
                System.err.println("Error splitting the file.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
   }
   
    public void unchunkfile(String fileName){
       try {
            ProcessBuilder catProcessBuilder = new ProcessBuilder("bash", "-c", "cat "+fileName+"* > " + fileName);
            Process catProcess = catProcessBuilder.start();
            int catExitCode = catProcess.waitFor();
            if (catExitCode != 0) {
                System.err.println("Error concatenating the files.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }//makes sure there isn't a completed zip file as cat will create a corrupted file then 
   
    public void zipMyfile(FilesInitialisationClass fileObj){
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setCompressionLevel(CompressionLevel.MAXIMUM);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        try{
            File existingZipFile = new File("temp/"+fileObj.getFileName()+".zip");
            if (existingZipFile.exists()) {
                existingZipFile.delete(); // Delete the existing zip file
            }
            ZipFile zipFile = new ZipFile("temp/"+fileObj.getFileName()+".zip", fileObj.getEncryptionKey().toCharArray());
            zipFile.addFile(new File("temp/"+fileObj.getFileName()), zipParameters);
        }catch(Exception e) 
        {
            e.printStackTrace();
            
        }
    }
   
    public void unzipMyfile(FilesInitialisationClass fileObj){
        try{
            ZipFile zipFile = new ZipFile("temp/"+fileObj.getFileName()+".zip", fileObj.getEncryptionKey().toCharArray());
            zipFile.extractAll("temp/");
        }
        catch(Exception e) 
        {
            e.printStackTrace();
            
        }
    }

}
