package com.alerts.models;

public class BloodOxygenAlert extends BasicAlert {

    public BloodOxygenAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

}
