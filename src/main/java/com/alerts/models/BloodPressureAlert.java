package com.alerts.models;

public class BloodPressureAlert extends BasicAlert{

    public BloodPressureAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

}
