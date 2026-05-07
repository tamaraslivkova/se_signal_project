package com.alerts.factories;

import com.alerts.models.BasicAlert;
import com.alerts.models.HypotensiveHypoxemiaAlert;

public class HypotensiveHypoxemiaAlertFactory extends AlertFactory {

    @Override
    public BasicAlert createAlert(String patientId, String condition, long timestamp) {
        return new HypotensiveHypoxemiaAlert(patientId, condition, timestamp);
    }

}
