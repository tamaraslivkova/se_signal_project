package com.alerts.strategies;

import java.util.List;

import com.alerts.AlertGenerator;
import com.alerts.factories.ManualAlertFactory;
import com.alerts.models.BasicAlert;
import com.datamanagement.Patient;
import com.datamanagement.PatientRecord;

public class ManualAlertStrategy implements AlertStrategy {
    private final ManualAlertFactory factory = new ManualAlertFactory();

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        List<PatientRecord> allRecords = patient.getRecords(0,System.currentTimeMillis());
        if (allRecords.isEmpty()) {
            return;  //nothing to evaluate yet
        }
        PatientRecord latestRecord = allRecords.getLast();
        if (latestRecord.getRecordType().equals("Alert")) {
            String patientId = String.valueOf(latestRecord.getPatientId());
            long timestamp = latestRecord.getTimestamp();
            double measurementValue = latestRecord.getMeasurementValue();
            if (measurementValue == 1.0) {
                BasicAlert alert = factory.createAlert(patientId, "Manual alert triggered by patient/nurse", timestamp);
                generator.triggerAlert(alert);
            } else if (measurementValue == 0.0) {
                BasicAlert alert = factory.createAlert(patientId, "Manual alert resolved", timestamp);
                generator.triggerAlert(alert);
            }
        }
    }
}
