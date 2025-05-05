/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
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
   
    public void chunkfile(File fileName){
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
    
    public void getFile(FilesInitialisationClass fileObj){
        int x = 0;
        for (int i = 0; i < working_containers.length; i++) {
            if(working_containers[i]){
               getFileChunk(fileObj.getFileName()+".zip",i,fileChunkNO(x));
                x++;
                }
            }
        unchunkfile("temp/"+fileObj.getFileName()+".zip");
        unzipMyfile(fileObj);
    }
        
    public void getFileChunk(String fileName, int number, String chunkNumber) {
        String localFile = "temp/" + fileName + chunkNumber;
        String remoteFile = "/root/" + fileName + chunkNumber;

        Session session = null;
        Channel channel = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(USERNAME[number], REMOTE_HOST[number], REMOTE_PORT[number]);
            session.setPassword(PASSWORD[number]);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect(SESSION_TIMEOUT);

            channel = session.openChannel("sftp");
            channel.connect(CHANNEL_TIMEOUT);
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            // Perform the actual file download (SFTP GET)
            sftpChannel.get(remoteFile, localFile);
            System.out.println("Downloaded: " + remoteFile + " -> " + localFile);

            sftpChannel.exit(); // Clean exit from channel
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    
    public void sendFile(FilesInitialisationClass fileObj){
        int x = 0;
        for (int i = 0; i < working_containers.length; i++) {
            if(working_containers[i]){
                sendFileChunk(fileObj.getFileName()+".zip",i,fileChunkNO(x));
                x++;
            }
        }
    }
    
    public void sendFileChunk(String fileName, int number, String chunkNumber) {
        String localFile =   "/home/kawsar/Documents/COMP20081/src/Files/"+fileName + chunkNumber;
        String remoteFile = "/root/" + fileName + chunkNumber;

        Session session = null;
        Channel channel = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(USERNAME[number], REMOTE_HOST[number], REMOTE_PORT[number]);
            session.setPassword(PASSWORD[number]);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect(SESSION_TIMEOUT);

            channel = session.openChannel("sftp");
            channel.connect(CHANNEL_TIMEOUT);
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            sftpChannel.put(localFile, remoteFile);
            System.out.println("Uploaded " + localFile + " to " + remoteFile);

            sftpChannel.exit();
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    

    public void deleteFile(FilesInitialisationClass fileObj){
        int x = 0;
        for (int i = 0; i < working_containers.length; i++) {
            if(working_containers[i]){
                deleteFileChunk(fileObj.getFileName()+".zip",i,fileChunkNO(x));
                x++;
            }
        }
    }
    
    public void deleteFileChunk(String fileName, int number, String chunkNumber) {
        String remoteFile = "/root/" + fileName + chunkNumber;

        Session session = null;
        Channel channel = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(USERNAME[number], REMOTE_HOST[number], REMOTE_PORT[number]);
            session.setPassword(PASSWORD[number]);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect(SESSION_TIMEOUT);

            channel = session.openChannel("sftp");
            channel.connect(CHANNEL_TIMEOUT);
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            sftpChannel.rm(remoteFile);
            System.out.println("Deleted remote file: " + remoteFile);

            sftpChannel.exit();
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    
    
    public String fileChunkNO(int number){
        if (number<10){
            return "0"+Integer.toString(number);
        }
        return Integer.toString(number);
    }

}
