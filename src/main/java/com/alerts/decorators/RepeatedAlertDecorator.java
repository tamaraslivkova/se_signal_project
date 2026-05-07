package com.alerts.decorators;

import com.alerts.Alert;

public class RepeatedAlertDecorator extends AlertDecorator{
    private int repeatCount;
    private int interval; //in minutes

    public RepeatedAlertDecorator(Alert alert, int repeatCount, int interval) {
        super(alert);
        this.repeatCount = repeatCount;
        this.interval = interval;
    }
    
    @Override
    public String getCondition() {
        return super.getCondition() + ", Count: " + repeatCount + ", Interval: " + interval + " min";
    }
}
