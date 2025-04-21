/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.javafxapplication1;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.crypto.spec.*;
import javax.crypto.*;
import java.security.AlgorithmParameters;
import java.security.spec.KeySpec;


public class FileChunking {
    private FileManagementController fileManagementController= new FileManagementController();
    private String chunkedFilePath = fileManagementController.getHomePath()+"/chunkedfile";
    private FileEncryptionDecryption fileEncrypterDecrypter = new FileEncryptionDecryption();

    public String getChunkedFilePath() {
        return chunkedFilePath;
    }

//    public void splitFileIntoEncryptedChunks(File file, String password) {
//        final int chunkSizeKB = 1024; // Chunk size in kilobytes
//        byte[] buffer = new byte[chunkSizeKB * 5];
//        int bytesRead;
//        int chunkIndex = 0;
//
//        try (FileInputStream fis = new FileInputStream(file)) {
//            while ((bytesRead = fis.read(buffer)) > 0) {
//                byte[] chunk = new byte[bytesRead];
//                System.arraycopy(buffer, 0, chunk, 0, bytesRead);
//                byte[] encryptedChunk = fileEncrypterDecrypter.encryptChunk(chunk, password);
//                saveEncryptedChunkToFile(encryptedChunk, file.getName(), chunkIndex);
//                chunkIndex++;
//                System.out.println(chunkedFilePath);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private void saveEncryptedChunkToFile(byte[] chunk, String fileName, int chunkIndex) {
//        try {
//            String outputFileName = chunkedFilePath + fileName + "_encrypted_chunk" + chunkIndex + ".dat";
//            try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
//                fos.write(chunk);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public void deleteEncryptedChunks(File file) {
//        String fileName = file.getName();
//        for (int i = 0; ; i++) {
//            String chunkFileName = chunkedFilePath + fileName + "_encrypted_chunk" + i + ".dat";
//            File chunkFile = new File(chunkFileName);
//            if (chunkFile.exists()) {
//                if (chunkFile.delete()) {
//                    System.out.println("Encrypted chunk file deleted successfully: " + chunkFile.getName());
//                } else {
//                    System.err.println("Failed to delete encrypted chunk file: " + chunkFile.getName());
//                }
//            } else {
//                System.out.println("Encrypted chunk files deleted for file: " + fileName);
//                break; // Exit loop when no more encrypted chunks are found
//            }
//        }
//    }

    public void splitFileIntoChunks(File file) {
        final int chunkSizeKB = 1024; // Chunk size in kilobytes
        byte[] buffer = new byte[chunkSizeKB * 5];
        int bytesRead;
        int chunkIndex = 0;

        try (FileInputStream fis = new FileInputStream(file)) {
            while ((bytesRead = fis.read(buffer)) > 0) {
                byte[] chunk = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunk, 0, bytesRead);
                saveChunkToFile(chunk, file.getName(), chunkIndex);
                chunkIndex++;
            }
            System.out.println(chunkedFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveChunkToFile(byte[] chunk, String fileName, int chunkIndex) {
        try {
            String outputFileName = chunkedFilePath + fileName + "_chunk" + chunkIndex + ".dat";
            try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
                fos.write(chunk);
                System.out.println(chunkedFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteChunkFiles(File file) {
        String fileName = file.getName();
        for (int i = 0; ; i++) {
            String chunkFileName = chunkedFilePath + fileName + "_chunk" + i + ".dat";
            File chunkFile = new File(chunkFileName);
            if (chunkFile.exists()) {
                if (chunkFile.delete()) {
                    System.out.println("Chunk file deleted successfully: " + chunkFile.getName());
                } else {
                    System.err.println("Failed to delete chunk file: " + chunkFile.getName());
                }
            } else {
                System.out.println("Chunk files deleted for file: " + fileName);
                break; // Exit loop when no more chunks are found
            }
        }
    }
}


