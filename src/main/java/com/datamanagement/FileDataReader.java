package com.datamanagement;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class FileDataReader implements DataReader{
    private String outputDirectory;

    public FileDataReader(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        Path directoryPath = Paths.get(outputDirectory);

        try {
        DirectoryStream<Path> stream = Files.newDirectoryStream(directoryPath, "*.txt");
        for (Path path : stream) {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                parseAndStore(line, dataStorage);
            }
        }
        } catch (Exception e) {

        }
    }

    public void parseAndStore(String line, DataStorage dataStorage) {
        try {
            //split by commas
            String[] parts = line.split(",");

            int patientId = Integer.parseInt(parts[0].split(": ")[1].trim());
            long timestamp = Long.parseLong(parts[1].split(": ")[1].trim());
            String recordType = parts[2].split(": ")[1].trim();
            String rawData = parts[3].split(": ")[1].trim();  
            double measurementValue;
            
            if (recordType.equalsIgnoreCase("Alert")) {
                if (rawData.equalsIgnoreCase("triggered")) {
                    measurementValue = 1.0; // Triggered = 1.0
                } else if (rawData.equalsIgnoreCase("resolved")) {
                    measurementValue = 0.0; // Resolved = 0.0
                } else {
                    return; 
            }
            } else {
                String numericString = rawData.replace("%", ""); //get rid of % if it is in the data
                measurementValue = Double.parseDouble(numericString);
            }

            dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
        } catch (Exception e) {
            System.err.print("An error occured while processing line: [" + line + "]");
        }
    }
}
