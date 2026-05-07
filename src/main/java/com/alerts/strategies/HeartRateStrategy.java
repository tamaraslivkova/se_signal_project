package com.alerts.strategies;

import java.util.ArrayList;
import java.util.List;

import com.alerts.AlertGenerator;
import com.alerts.factories.ECGAlertFactory;
import com.alerts.models.BasicAlert;
import com.datamanagement.Patient;
import com.datamanagement.PatientRecord;

public class HeartRateStrategy implements AlertStrategy {
    private final ECGAlertFactory factory = new ECGAlertFactory();
    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        List<PatientRecord> allRecords = patient.getRecords(0,System.currentTimeMillis());
        if (allRecords.isEmpty()) {
            return;  //nothing to evaluate yet
        }       
        PatientRecord latestRecord = allRecords.getLast();
        String patientId = String.valueOf(latestRecord.getPatientId());
        long now = latestRecord.getTimestamp();
        List<PatientRecord> RecordsWithin10Minutes = patient.getRecords(now - 600000, now);
        List<PatientRecord> ECGRecords = new ArrayList<>();
        double sum = 0;
        for(PatientRecord record : RecordsWithin10Minutes) {
            if(record.getRecordType().equals("ECG")) {
                ECGRecords.add(record);
                sum = sum + record.getMeasurementValue();
            }
        }
        if (ECGRecords.isEmpty()) {
            return; //to avoid division by zero
        }
        double average = sum/ECGRecords.size();
        double abnormalThreshold = average * 1.3; //was not specified

        if(latestRecord.getMeasurementValue() > abnormalThreshold) {
            BasicAlert alert = factory.createAlert(patientId, "Abnormal ECG peak", now);
            generator.triggerAlert(alert);
        }
    }

}
