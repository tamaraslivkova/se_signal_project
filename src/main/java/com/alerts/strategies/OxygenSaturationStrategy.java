package com.alerts.strategies;

import java.util.ArrayList;
import java.util.List;

import com.alerts.AlertGenerator;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.alerts.models.BasicAlert;
import com.datamanagement.Patient;
import com.datamanagement.PatientRecord;

public class OxygenSaturationStrategy implements AlertStrategy {
    private final BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        List<PatientRecord> allRecords = patient.getRecords(0,System.currentTimeMillis());
        if (allRecords.isEmpty()) {
            return;  //nothing to evaluate yet
        }       
        PatientRecord latestRecord = allRecords.getLast();
        String patientId = String.valueOf(latestRecord.getPatientId());
        long timestamp = latestRecord.getTimestamp();

        if(latestRecord.getMeasurementValue() < 92) {
            BasicAlert alert = factory.createAlert(patientId, "Low blood saturation", timestamp);
            generator.triggerAlert(alert);
        }

        List<PatientRecord> bloodSaturationRecords = new ArrayList<>();
        for (PatientRecord patientRecord : allRecords) {
            if(patientRecord.getRecordType().equals("Saturation")) {
                bloodSaturationRecords.add(patientRecord);
            }
        }

        double valueNow = latestRecord.getMeasurementValue();
        long tenMinutesAgo = timestamp - 600000;
        for (PatientRecord pastRecord : bloodSaturationRecords) {
            if (pastRecord.getTimestamp() >= tenMinutesAgo && pastRecord.getTimestamp() < timestamp) {
                if(pastRecord.getMeasurementValue() - valueNow >= 5) {
                    BasicAlert alert = factory.createAlert(patientId, "Rapid drop of blood saturation", timestamp);
                    generator.triggerAlert(alert);
                    return; //not to trigger more triggers if one was already triggered
                }
            }
        }
    }
}
