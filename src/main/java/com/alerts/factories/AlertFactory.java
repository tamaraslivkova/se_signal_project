package com.alerts.factories;

import com.alerts.models.BasicAlert;

public abstract class AlertFactory {
    public abstract BasicAlert createAlert(String patientId, String condition, long timestamp);
}
