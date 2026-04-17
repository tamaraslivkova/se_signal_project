package com.cardiogenerator.generators;

import com.cardiogenerator.outputs.OutputStrategy;
/**
 * Defines rules for generating health data for a patient.
 * 
 * Implementations of this interface produce simulated patient data and
 * output it using the given output strategy.
 */
public interface PatientDataGenerator {
    void generate(int patientId, OutputStrategy outputStrategy);
}