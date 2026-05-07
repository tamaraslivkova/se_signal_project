package com.alerts;

import com.alerts.strategies.AlertStrategy;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.HypotensiveHypoxemiaStrategy;
import com.alerts.strategies.ManualAlertStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.datamanagement.DataStorage;
import com.datamanagement.Patient;
import java.util.List;
import java.util.ArrayList;

/** //A fragment should be a noun phrase or verb phrase, not a complete sentence
 * Monitors patient data and generates alerts when certain predefined conditions 
 * are met.
 * Uses a {@link DataStorage} instance to access patient data and evaluate it 
 * against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private List<AlertStrategy> strategies = new ArrayList<>();

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * Uses {@code DataStorage} to retrieve, monitor and evaluate patient data.
     * @param dataStorage provides access to patient data.
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        //add strategies to the strategies list
        strategies.add(new BloodPressureStrategy());
        strategies.add(new HeartRateStrategy());
        strategies.add(new OxygenSaturationStrategy());
        strategies.add(new HypotensiveHypoxemiaStrategy());
        strategies.add(new ManualAlertStrategy());
    }

    /** //rephrased documentation
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. 
     * {@link #triggerAlert} defines the specific conditions under which an alert
     * will be triggered.
     * @param patient the patient data to evaluate for alert conditions.
     */
    public void evaluateData(Patient patient) {
        for (AlertStrategy strategy : strategies) {
            strategy.checkAlert(patient, this);
        }
    }

    /** //documentation rephrased 
     * Triggers an alert for the monitoring system. 
     * Can be extended to notify medical staff, log the alert, or perform other 
     * actions. 
     * Assumes that the alert information is fully formed when passed as an
     * argument.
     * @param alert the alert object containing details about the alert condition.
     */
    public void triggerAlert(Alert alert) {
        System.out.println("!!! ALERT TRIGGERED !!!");
        System.out.println("Patient ID: " + alert.getPatientId());
        System.out.println("Condition:  " + alert.getCondition());
        System.out.println("Timestamp:  " + alert.getTimestamp());
        System.out.println("-----------------------");
    }
}
