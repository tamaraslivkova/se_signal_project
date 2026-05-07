package com.alerts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*; 

import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import com.alerts.models.BasicAlert;

public class AlertDecoratorTest {
    @Test
    void testPriorityDecorator() {
        Alert basicAlert = new BasicAlert("1", "High Systolic Blood Pressure", System.currentTimeMillis());
        Alert priorityAlert = new PriorityAlertDecorator(basicAlert);
        assertTrue(priorityAlert.getCondition().contains("URGENT!"));
    }
    @Test
    void testRepeatedDecorator() {
        Alert basicAlert = new BasicAlert("1", "High Systolic Blood Pressure", System.currentTimeMillis());
        Alert repeatedAlert = new RepeatedAlertDecorator(basicAlert, 5, 10);
        assertTrue(repeatedAlert.getCondition().contains("Count: 5"));
        assertTrue(repeatedAlert.getCondition().contains("Interval: 10"));
    }
    @Test
    void testCombinedDecorator() {
        Alert basicAlert = new BasicAlert("1", "High Systolic Blood Pressure", System.currentTimeMillis());
        Alert combinedAlert = new RepeatedAlertDecorator(new PriorityAlertDecorator(basicAlert), 5, 10);
        assertTrue(combinedAlert.getCondition().contains("URGENT!"));
        assertTrue(combinedAlert.getCondition().contains("Count: 5"));
        assertTrue(combinedAlert.getCondition().contains("Interval: 10"));
    }
}
