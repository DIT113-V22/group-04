package com.example.drawer;

import android.os.SystemClock;
import android.widget.Chronometer;
import java.util.ArrayList;
import java.util.LinkedList;

public class ManualRecordingRun implements Runnable {
    private final Chronometer executeTimer ;
    private int executeTimerInt;
    private boolean executeTimerBool;
    private final ArrayList<Long> carTimerQueue;
    private final MQTTController mqttController;
    private final ArrayList<Integer> carSpeedQueue;
    private final ArrayList<Integer> carAngleQueue;
    private boolean obstacle = false;
    private long executeCheckingTime = 0;
    private long executeCheckingTimeBeninging = 0;
    private ArrayList<String> finalOutputList;

    public ManualRecordingRun(ArrayList<Long> carTimerQueue,
                              ArrayList<Integer> carAngleQueue,
                              ArrayList<Integer> carSpeedQueue,
                              MQTTController mqttController,
                              Chronometer executeTimer) {
        this.carTimerQueue = carTimerQueue;
        this.mqttController = mqttController;
        this.carAngleQueue =carAngleQueue;
        this.carSpeedQueue = carSpeedQueue;
        this.executeTimer = executeTimer;
    }

    /**
     * Runs recorded commands/instructions. Sent out based on time.
     */
    @Override
    public void run() {
        //Start a new timer if none has been run before
        if (!executeTimerBool) {
            executeTimer.setBase(SystemClock.elapsedRealtime());
            executeTimer.start();
            executeTimerBool = true;
            executeCheckingTimeBeninging = System.currentTimeMillis();
        }
        //Go through and publish all commands
        for (int m = 0; m < carSpeedQueue.size(); m++) {
            boolean timerChecked = true;

            while(timerChecked && !obstacle){
                executeCheckingTime = System.currentTimeMillis();
                //Publish only with correct timing
                if ( (executeCheckingTime - executeCheckingTimeBeninging)  >= carTimerQueue.get(m)) {
                    mqttController.publish("/smartcar/control/throttle", String.valueOf(carSpeedQueue.get(m)));
                    mqttController.publish("/smartcar/control/steering", String.valueOf(carAngleQueue.get(m)));
                    timerChecked = false;
                }
            }
        }
        executeTimer.stop();
    }
}
