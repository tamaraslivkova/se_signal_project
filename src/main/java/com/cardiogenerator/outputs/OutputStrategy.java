package com.cardiogenerator.outputs;
/**
 * Defines a strategy for outputting of health data.
 * 
 * Implementations determine how and where patient data is delivered,
 * such as console output, file storage, or network transmission.
 */
public interface OutputStrategy {
    void output(int patientId, long timestamp, String label, String data);
}
