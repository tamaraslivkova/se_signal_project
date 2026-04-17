//violation: wrong package name, no underscores
package com.cardiogenerator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;
/**
 * File based implementation of OutputStrategy.
 * 
 * Writes generated patient health data to text files, organized by data 
 * label. Each label is stored in a separate file within the base directory.
 */
//violation: first letter of class name should be capital (F not f)
public class FileOutputStrategy implements OutputStrategy {
    //violation: wrong non-constant field name, should be written in lowerCamelCase
    private String baseDirectory;
    //violation: wrong constant name style, should be UPPER_SNAKE_CASE
    public final ConcurrentHashMap<String, String> FILE_MAP = new ConcurrentHashMap<>();

    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory; //extra line
    }
    /**
     * Outputs patient health data by creating a base directory and writing data to a file with a given label.
     * 
     * @param patientId ID of the patient.
     * @param timeStamp the time at which the data was generated.
     * @param label name of the file.
     * @param data generated patient health data.
     */
    @Override //violation: wrong non-constant field name, should be written in lowerCamelCase
    public void output(int patientId, long timeStamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable //violation: wrong non-constant field name, should be written in lowerCamelCase
        String filePath = FILE_MAP.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
            //wrong indentation
            Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timeStamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}