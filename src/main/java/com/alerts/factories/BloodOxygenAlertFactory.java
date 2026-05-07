package com.alerts.factories;

import com.alerts.models.BasicAlert;
import com.alerts.models.BloodOxygenAlert;

public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    public BasicAlert createAlert(String patientId, String condition, long timestamp) {
        return new BloodOxygenAlert(patientId, condition, timestamp);
    }
}
