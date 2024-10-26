package com.hq.jadb.service;

import com.hq.jadb.config.AdbConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

public class AdbService {
    private final String adbPath;

    public AdbService() {
        this.adbPath = getAdbPath();
    }

    public String executeCommand(List<String> command){
        try {
            LinkedList<String> commandAdb = new LinkedList<>(command);
            commandAdb.addFirst(adbPath);

            Process process = new ProcessBuilder(commandAdb).start();
            BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
            StringBuilder output = new StringBuilder();
            String line;

            while ( (line = reader.readLine()) != null ) {
                output.append(line).append("\n");
            }

            return output.toString();
        } catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }

        return null;
    }

    public static String getAdbPath() {
        try {
            ClassLoader classLoader = AdbService.class.getClassLoader();
            InputStream adbStream = classLoader.getResourceAsStream(AdbConfig.ADB_PATH);

            if (adbStream == null) {
                throw new IllegalArgumentException("adb.exe no encontrado en los recursos");
            }

            Path tempAdbPath = Files.createTempFile("adb", ".exe");

            Files.copy(adbStream, tempAdbPath, StandardCopyOption.REPLACE_EXISTING);

            File adbFile = tempAdbPath.toFile();
            adbFile.setExecutable(true);

            return adbFile.getAbsolutePath();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

}
