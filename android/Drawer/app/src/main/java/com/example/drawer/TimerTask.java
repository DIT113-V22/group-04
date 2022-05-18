package com.example.drawer;

import android.os.SystemClock;
import android.widget.Chronometer;

import java.util.ArrayList;
import java.util.LinkedList;

public class TimerTask extends java.util.TimerTask {
    private Chronometer executeTimer;
    private int executeTimerInt;
    private boolean executeTimerBool;
    private LinkedList carTimerQueue;
    private MQTTController mqttController;
    private int m;
    private LinkedList carSpeedQueue;
    private LinkedList carAngleQueue;

    public TimerTask(LinkedList carTimerQueue, MQTTController mqttController, Chronometer executeTimer, boolean executeTimerBool, LinkedList carSpeedQueue, LinkedList carAngleQueue, int m){
        this.carTimerQueue = carTimerQueue;
        this.mqttController = mqttController;
        this.executeTimer = executeTimer;
        this.executeTimerBool = executeTimerBool;
        this.m = m;
        this.carAngleQueue =carAngleQueue;
        this.carSpeedQueue = carSpeedQueue;

    }

    @Override
    public void run() {
        executeTimerInt = (int) (SystemClock.elapsedRealtime() - executeTimer.getBase());
        if ((int) executeTimerInt / 10 == (int) ((int) carTimerQueue.get(m) / 10)) {
            mqttController.publish("/smartcar/control/throttle", String.valueOf(carSpeedQueue.get(m)));
            mqttController.publish("/smartcar/control/steering", String.valueOf(carAngleQueue.get(m)));
        }
    }
}
