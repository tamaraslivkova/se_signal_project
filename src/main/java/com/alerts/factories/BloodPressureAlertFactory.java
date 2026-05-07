package com.alerts.factories;

import com.alerts.models.BasicAlert;
import com.alerts.models.BloodPressureAlert;

public class BloodPressureAlertFactory extends AlertFactory {
    @Override
    public BasicAlert createAlert(String patientId, String condition, long timestamp) {
        return new BloodPressureAlert(patientId, condition, timestamp);
    }
}
