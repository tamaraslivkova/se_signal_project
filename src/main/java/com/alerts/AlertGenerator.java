package com.alerts;

import com.datamanagement.DataStorage;
import com.datamanagement.Patient;
import com.datamanagement.PatientRecord;
import java.util.List;
import java.util.ArrayList;

/** //A fragment should be a noun phrase or verb phrase, not a complete sentence
 * Monitors patient data and generates alerts when certain predefined conditions 
 * are met.
 * Uses a {@link DataStorage} instance to access patient data and evaluate it 
 * against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * Uses {@code DataStorage} to retrieve, monitor and evaluate patient data.
     * @param dataStorage provides access to patient data.
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /** //rephrased documentation
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. 
     * {@link #triggerAlert} defines the specific conditions under which an alert
     * will be triggered.
     * @param patient the patient data to evaluate for alert conditions.
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> allRecords = patient.getRecords(0,System.currentTimeMillis());
        if (allRecords.isEmpty()) {
            return;  //nothing to evaluate yet
        }       
        PatientRecord latestRecord = allRecords.getLast();
        String recordType = latestRecord.getRecordType();
        //check for hypotensive hypoxemia alert
        checkHypotensiveHypoxemia(patient, latestRecord);

        //check diastolic blood pressure 
        //note: if hypotensive hypoxemia alert is triggered low systolic pressure alert will be triggered as well separately
        if (recordType.equals("SystolicPressure") || recordType.equals("DiastolicPressure")) {
            checkBloodPressure(latestRecord, allRecords);
        }
        //check blood saturation
        if(recordType.equals("Saturation")) {
            checkBloodSaturation(latestRecord, allRecords);
        }
        //check ECG
        if(recordType.equals("ECG")) {
            checkECG(patient, latestRecord);
        }
        //check for a triggered alert
        if (recordType.equals("Alert")) {
            checkTriggeredAlert(latestRecord);
        }
    }

    public void checkBloodPressure(PatientRecord latestRecord, List<PatientRecord> allRecords) {
        String recordType = latestRecord.getRecordType();
        double recordValue = latestRecord.getMeasurementValue();
        String patientId = String.valueOf(latestRecord.getPatientId());
        long timestamp = latestRecord.getTimestamp();

        if(recordType.equals("SystolicPressure")) {
            if(recordValue > 180) {
                String condition = "Critical Threshold: Systolic BP above 180";
                triggerAlert(new Alert(patientId, condition, timestamp));
            }
            if(recordValue < 90) {
                String condition = "Critical Threshold: Systolic BP below 90";
                triggerAlert(new Alert(patientId, condition, timestamp));
            }
        }
        if(recordType.equals("DiastolicPressure")) {
            if(recordValue > 120) {
                String condition = "Critical Threshold: Diastolic BP above 120";
                triggerAlert(new Alert(patientId, condition, timestamp));
            }
            if(recordValue < 60) {
                String condition = "Critical Threshold: Diastolic BP below 60";
                triggerAlert(new Alert(patientId, condition, timestamp));
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
        checkTrend(systolicPressureRecords, "Systolic Pressure", patientId);
        checkTrend(diastolicPressureRecords, "Diastolic Pressure", patientId);
        
    }
    public void checkTrend(List<PatientRecord> filtered, String type, String patientId) {
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
                triggerAlert(new Alert(patientId, "Increasing trend in " + type, timestamp));
            } else if ((val2 - val1 > 10) && (val3 - val2 > 10) && (val4 - val3 > 10)) {
                triggerAlert(new Alert(patientId, "Decreasing trend in " + type, timestamp));
            }
        }
    }
    public void checkBloodSaturation(PatientRecord latestRecord, List<PatientRecord> allRecords) {
        String patientId = String.valueOf(latestRecord.getPatientId());
        long timestamp = latestRecord.getTimestamp();

        if(latestRecord.getMeasurementValue() < 92) {
            triggerAlert(new Alert(patientId, "Low blood saturation", timestamp));
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
                    triggerAlert(new Alert(patientId, "Rapid drop of blood saturation", timestamp));
                    return; //not to trigger more triggers if one was already triggered
                }
            }
        }
    }
    public void checkHypotensiveHypoxemia(Patient patient, PatientRecord latestRecord) {
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
                triggerAlert(new Alert(patientId, "Hypotensive Hypoxemia Alert", now));
            }
        }
    }
    public void checkECG(Patient patient, PatientRecord latestRecord) {
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
        double average = sum/ECGRecords.size();
        double abnormalThreshold = average * 1.3; //was not specified

        if(latestRecord.getMeasurementValue() > abnormalThreshold) {
            triggerAlert(new Alert(patientId, "Abnormal ECG peak", now));
        }
    }
    public void checkTriggeredAlert(PatientRecord latestRecord) {
        String patientId = String.valueOf(latestRecord.getPatientId());
        long timestamp = latestRecord.getTimestamp();
        double measurementValue = latestRecord.getMeasurementValue();
        if (measurementValue == 1.0) {
            triggerAlert(new Alert(patientId, "Manual alert triggered by patient/nurse", timestamp));
        } else if (measurementValue == 0.0) {
            triggerAlert(new Alert(patientId, "Manual alert resolved", timestamp));
        }
        
    }


    /** //documentation rephrased 
     * Triggers an alert for the monitoring system. 
     * Can be extended to notify medical staff, log the alert, or perform other 
     * actions. 
     * Assumes that the alert information is fully formed when passed as an
     * argument.
     * @param alert the alert object containing details about the alert condition.
     */
    private void triggerAlert(Alert alert) {
        // TODO Implementation might involve logging the alert or notifying staff
        System.out.println("!!! ALERT TRIGGERED !!!");
        System.out.println("Patient ID: " + alert.getPatientId());
        System.out.println("Condition:  " + alert.getCondition());
        System.out.println("Timestamp:  " + alert.getTimestamp());
        System.out.println("-----------------------");
    }
}
