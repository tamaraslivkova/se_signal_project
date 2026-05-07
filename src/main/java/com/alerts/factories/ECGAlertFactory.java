package com.alerts.factories;

import com.alerts.models.BasicAlert;
import com.alerts.models.ECGAlert;

public class ECGAlertFactory extends AlertFactory{
    @Override
    public BasicAlert createAlert(String patientId, String condition, long timestamp) {
        return new ECGAlert(patientId, condition, timestamp);
    }
}
