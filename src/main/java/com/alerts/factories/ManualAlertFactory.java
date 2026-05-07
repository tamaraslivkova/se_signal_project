package com.alerts.factories;

import com.alerts.models.BasicAlert;
import com.alerts.models.ManualAlert;

public class ManualAlertFactory extends AlertFactory{

    @Override
    public BasicAlert createAlert(String patientId, String condition, long timestamp) {
        return new ManualAlert(patientId, condition, timestamp);
    }

}
