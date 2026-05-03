package datamanagement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach; 
import static org.junit.jupiter.api.Assertions.*; 

import com.datamanagement.DataStorage;
import com.datamanagement.Patient;
import com.alerts.AlertGenerator;
import com.alerts.Alert; 
import java.util.List;
public class AlertGeneratorTest {
    private DataStorage storage;    
    private AlertGenerator generator;

    @BeforeEach
    void setUp() {
        storage = new DataStorage(); 
        generator = new AlertGenerator(storage); 
    }

    @Test
    void testBloodPressureIncreasingTrend() {
        int patientId = 1;
        Patient patient = new Patient(patientId);
        long now = System.currentTimeMillis();

        patient.addRecord(100.0, "SystolicPressure", now - 3000);
        patient.addRecord(111.0, "SystolicPressure", now - 2000);
        patient.addRecord(122.0, "SystolicPressure", now - 1000);
        patient.addRecord(133.0, "SystolicPressure", now);

        generator.evaluateData(patient);
    }

    @Test
    void testBloodPressureDecreasingTrend() {
        int patientId = 2;
        Patient patient = new Patient(patientId);
        long now = System.currentTimeMillis();

        patient.addRecord(133.0, "SystolicPressure", now - 3000);
        patient.addRecord(122.0, "SystolicPressure", now - 2000);
        patient.addRecord(111.0, "SystolicPressure", now - 1000);
        patient.addRecord(100.0, "SystolicPressure", now);

        generator.evaluateData(patient);
    }

    @Test
    void testHighSystolicPressure() {
        int patientId = 3;
        Patient patient = new Patient(patientId);
        long now = System.currentTimeMillis();

        patient.addRecord(181.0, "SystolicPressure", now);

        generator.evaluateData(patient);
    }
    @Test
    void testHighDiastolicPressure() {
        int patientId = 4;
        Patient patient = new Patient(patientId);
        long now = System.currentTimeMillis();

        patient.addRecord(181.0, "DiastolicPressure", now);

        generator.evaluateData(patient);
    }
    @Test
    void testLowSystolicPressure() {
        int patientId = 5;
        Patient patient = new Patient(patientId);
        long now = System.currentTimeMillis();

        patient.addRecord(85.0, "SystolicPressure", now);

        generator.evaluateData(patient);
    }
     @Test
    void testLowDiastolicPressure() {
        int patientId = 6;
        Patient patient = new Patient(patientId);
        long now = System.currentTimeMillis();

        patient.addRecord(55.0, "DiastolicPressure", now);

        generator.evaluateData(patient);
    }
    @Test
    void testLowBloodSaturation() {
        int patientId = 7;
        Patient patient = new Patient(patientId);
        long now = System.currentTimeMillis();

        patient.addRecord(90.0, "Saturation", now);

        generator.evaluateData(patient);
    }
    @Test 
    void testRapidDropSaturation() {
        int patientId = 8;
        Patient patient = new Patient(patientId);
        long now = System.currentTimeMillis();

        patient.addRecord(98.0, "Saturation", now - 3000);
        patient.addRecord(96.0, "Saturation", now - 2000);
        patient.addRecord(97.0, "Saturation", now - 1000);
        patient.addRecord(91.0, "Saturation", now);

        generator.evaluateData(patient);
    }
    @Test
    void testHypotensiveHypoxemia() {
        int patientId = 9;
        Patient patient = new Patient(patientId);
        long now = System.currentTimeMillis();

        patient.addRecord(88.0, "SystolicPressure", now - 1000);
        patient.addRecord(91.0, "Saturation", now);
        
        generator.evaluateData(patient);
    }
    @Test 
    void testAbnormalECGData() {
        int patientId = 10;
        Patient patient = new Patient(patientId);
        long now = System.currentTimeMillis();

        patient.addRecord(1.0, "ECG", now - 4000);
        patient.addRecord(1.03, "ECG", now - 3000);
        patient.addRecord(0.99, "ECG", now - 2000);
        patient.addRecord(1.12, "ECG", now - 1000);
        patient.addRecord(1.53, "ECG", now);
        
        generator.evaluateData(patient);
    }
    @Test
    void testTriggeredAlert() {
        int patientId = 11;
        Patient patient = new Patient(patientId);
        long now = System.currentTimeMillis();

        patient.addRecord(1.0, "Alert", now - 3000);
        generator.evaluateData(patient);

        patient.addRecord(0.0, "Alert", now);
        generator.evaluateData(patient);
    }
}
