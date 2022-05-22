package com.example.drawer;

import android.os.SystemClock;
import android.widget.Chronometer;

import java.util.ArrayList;
import java.util.LinkedList;

public class ManualRecordingRun implements Runnable {
    private Chronometer executeTimer ;
    private int executeTimerInt;
    private boolean executeTimerBool;
    private LinkedList carTimerQueue;
    private MQTTController mqttController;
    private LinkedList carSpeedQueue;
    private LinkedList carAngleQueue;
    private boolean obstacle = false;

    public ManualRecordingRun(LinkedList carTimerQueue, LinkedList carSpeedQueue, LinkedList carAngleQueue, MQTTController mqttController, Chronometer executeTimer){
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
        if(!executeTimerBool){
            executeTimer.setBase(SystemClock.elapsedRealtime());
            executeTimer.start();
            executeTimerBool = true;
        }
        //Go through and publish all commands
        for(int m = 0; m < carSpeedQueue.size(); m++) {
            boolean timerChecked = true;

            while(timerChecked && !obstacle){
                executeTimerInt = (int) (SystemClock.elapsedRealtime() - executeTimer.getBase());
                ///TODO: TEST WITHOUT /10
                //Publish only with correct timing
                if ((int) executeTimerInt  == (int) ((int) carTimerQueue.get(m) ))
                {mqttController.publish("/smartcar/control/throttle", String.valueOf(carSpeedQueue.get(m)));
                    mqttController.publish("/smartcar/control/steering", String.valueOf(carAngleQueue.get(m)));
                    timerChecked = false;
                }
            }
        }
        executeTimer.stop();
    }
}
