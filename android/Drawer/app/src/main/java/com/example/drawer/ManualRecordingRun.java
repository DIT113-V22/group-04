package com.example.drawer;

import android.os.SystemClock;
import android.widget.Chronometer;

import java.util.ArrayList;
import java.util.LinkedList;

public class ManualRecordingRun extends java.util.TimerTask {
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

    @Override
    public void run() {
        if(!executeTimerBool){
            executeTimer.setBase(SystemClock.elapsedRealtime());
            executeTimer.start();
            executeTimerBool = true;
        }
        for(int m = 0; m < carSpeedQueue.size(); m++) {
            boolean timerChecked = true;

            while(timerChecked && !obstacle){
                executeTimerInt = (int) (SystemClock.elapsedRealtime() - executeTimer.getBase());
                if ((int) executeTimerInt / 10 >= (int) ((int) carTimerQueue.get(m) / 10))
                {mqttController.publish("/smartcar/control/throttle", String.valueOf(carSpeedQueue.get(m)));
                    mqttController.publish("/smartcar/control/steering", String.valueOf(carAngleQueue.get(m)));
                    timerChecked = false;
                }
            }
        }
        executeTimer.stop();
    }
}