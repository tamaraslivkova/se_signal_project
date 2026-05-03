package com;

import com.cardiogenerator.HealthDataSimulator;
import com.datamanagement.DataStorage;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            DataStorage.main(new String[]{});
        } else {
            try {
                HealthDataSimulator.main(new String[]{});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
