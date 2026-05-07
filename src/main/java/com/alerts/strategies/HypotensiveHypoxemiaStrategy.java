package com.alerts.strategies;

import java.util.List;

import com.alerts.AlertGenerator;
import com.alerts.factories.HypotensiveHypoxemiaAlertFactory;
import com.alerts.models.BasicAlert;
import com.datamanagement.Patient;
import com.datamanagement.PatientRecord;

public class HypotensiveHypoxemiaStrategy implements AlertStrategy {
private final HypotensiveHypoxemiaAlertFactory factory = new HypotensiveHypoxemiaAlertFactory();

    @Override
    public void checkAlert(Patient patient, AlertGenerator generator) {
        List<PatientRecord> allRecords = patient.getRecords(0,System.currentTimeMillis());
        if (allRecords.isEmpty()) {
            return;  //nothing to evaluate yet
        }       
        PatientRecord latestRecord = allRecords.getLast();
        String patientId = String.valueOf(latestRecord.getPatientId());
        long now = latestRecord.getTimestamp();
        List<PatientRecord> RecordsWithinHour = patient.getRecords(now - 3600000, now);
        double lastSystolicBloodPressure = 0;
        double lastBloodSaturation = 0;

        for(int i = RecordsWithinHour.size() - 1; i >= 0; i--) {
            PatientRecord record = RecordsWithinHour.get(i);
            //find most recent systolic pressure
            if(lastSystolicBloodPressure == 0 && record.getRecordType().equals("SystolicPressure")) {
                lastSystolicBloodPressure = record.getMeasurementValue();
            }
            //find most recent blood saturation
            if(lastBloodSaturation == 0 && record.getRecordType().equals("Saturation")) {
                lastBloodSaturation = record.getMeasurementValue();
            }
            //stop if both are found
            if(lastSystolicBloodPressure != 0 && lastBloodSaturation != 0) {
                break;
            }
        }
        if(lastSystolicBloodPressure != 0 && lastBloodSaturation != 0) {
            if (lastSystolicBloodPressure < 90 && lastBloodSaturation < 92) {
                BasicAlert alert = factory.createAlert(patientId, "Hypotensive Hypoxemia Alert", now);
                generator.triggerAlert(alert);
            }
        }
    }
}
