package com.alerts.models;

public class ECGAlert extends BasicAlert{

    public ECGAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

}
