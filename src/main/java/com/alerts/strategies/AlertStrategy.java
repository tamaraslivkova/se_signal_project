package com.alerts.strategies;

import com.alerts.AlertGenerator;
import com.datamanagement.Patient;

public interface AlertStrategy {
    public void checkAlert(Patient patient, AlertGenerator generator);
}
