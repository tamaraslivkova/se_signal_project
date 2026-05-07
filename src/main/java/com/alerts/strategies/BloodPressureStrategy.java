package com.alerts.strategies;

import java.util.ArrayList;
import java.util.List;

import com.alerts.AlertGenerator;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.alerts.models.BasicAlert;
import com.datamanagement.Patient;
import com.datamanagement.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy{
    private final AlertFactory factory = new BloodPressureAlertFactory();

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        List<PatientRecord> allRecords = patient.getRecords(0,System.currentTimeMillis());
        if (allRecords.isEmpty()) {
            return;  //nothing to evaluate yet
        }       
        PatientRecord latestRecord = allRecords.getLast();
        String recordType = latestRecord.getRecordType();
        double recordValue = latestRecord.getMeasurementValue();
        String patientId = String.valueOf(latestRecord.getPatientId());
        long timestamp = latestRecord.getTimestamp();

        if(recordType.equals("SystolicPressure")) {
            if(recordValue > 180) {
                String condition = "Critical Threshold: Systolic BP above 180";
                BasicAlert alert = factory.createAlert(patientId, condition, timestamp);
                generator.triggerAlert(alert);
            }
            if(recordValue < 90) {
                String condition = "Critical Threshold: Systolic BP below 90";
                BasicAlert alert = factory.createAlert(patientId, condition, timestamp);
                generator.triggerAlert(alert);
            }
        }
        if(recordType.equals("DiastolicPressure")) {
            if(recordValue > 120) {
                String condition = "Critical Threshold: Diastolic BP above 120";
                BasicAlert alert = factory.createAlert(patientId, condition, timestamp);
                generator.triggerAlert(alert);
            }
            if(recordValue < 60) {
                String condition = "Critical Threshold: Diastolic BP below 60";
                BasicAlert alert = factory.createAlert(patientId, condition, timestamp);
                generator.triggerAlert(alert);
            }
        }
        List<PatientRecord> systolicPressureRecords = new ArrayList<>();
        List<PatientRecord> diastolicPressureRecords = new ArrayList<>();

        for(PatientRecord patientRecord : allRecords) {
            if(patientRecord.getRecordType().equals("SystolicPressure")) {
                systolicPressureRecords.add(patientRecord);
            }
            if(patientRecord.getRecordType().equals("DiastolicPressure")) {
                diastolicPressureRecords.add(patientRecord);
            }
        }
        checkTrend(systolicPressureRecords, "Systolic Pressure", patientId, generator);
        checkTrend(diastolicPressureRecords, "Diastolic Pressure", patientId, generator);
    }

    public void checkTrend(List<PatientRecord> filtered, String type, String patientId, AlertGenerator generator) {
        if (filtered.size() >= 4) {
            PatientRecord r1 = filtered.get(filtered.size() - 1);
            PatientRecord r2 = filtered.get(filtered.size() - 2);
            PatientRecord r3 = filtered.get(filtered.size() - 3);
            PatientRecord r4 = filtered.get(filtered.size() - 4);

            double val1 = r1.getMeasurementValue();
            double val2 = r2.getMeasurementValue();
            double val3 = r3.getMeasurementValue();
            double val4 = r4.getMeasurementValue();
            long timestamp = r1.getTimestamp();
                    
            if((val1 - val2 > 10) && (val2 - val3 > 10) && (val3 - val4 > 10)) {
                BasicAlert alert = factory.createAlert(patientId, "Increasing trend in " + type, timestamp);
                generator.triggerAlert(alert);
            } else if ((val2 - val1 > 10) && (val3 - val2 > 10) && (val4 - val3 > 10)) {
                BasicAlert alert = factory.createAlert(patientId, "Decreasing trend in " + type, timestamp);
                generator.triggerAlert(alert);
            }
        }
    }    
}
