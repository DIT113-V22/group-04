package com.example.drawer;

import android.graphics.Point;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Queue;

public class DrawControlRun implements Runnable{
    Queue<Point> pointQueue;
    MQTTController mqttController;
    final String STILL = "0";
    final String FORWARD_SPEED = "25";
    final String TURN_SPEED = "5";
    final String BACKWARD_SPEED = "-25";
    final int FORWARD_ANGLE = 0;
    final int RIGHT_ANGLE = 90;
    final int LEFT_ANGLE = -90;
    final int TOP_RIGHT_ANGLE = 45;
    final int TOP_LEFT_ANGLE = -45;
    final int BACKWARD_ANGLE = 180;
    int diagonalDistance;
    int adjacentDistance;



    public DrawControlRun(int diagonalDistance, int adjacentDistance, Queue<Point> pointQueue, MQTTController mqttController ){
        this.pointQueue = pointQueue;
        this.mqttController = mqttController;
        this.diagonalDistance = diagonalDistance;
        this.adjacentDistance = adjacentDistance;
    }

    @Override
    public void run() {
        Point start = pointQueue.poll();
        int lastMove = 0;


        Point end;

        // TODO Need to find proper upper limit for the loop condition -KC
        for (int i = 0; i < (pointQueue.size() * 10); i++) {

            end = pointQueue.poll();

            Log.d("abcd", "start" + String.valueOf(start));
            Log.d("abcd", "end" + String.valueOf(end));

            int dx = end.x - start.x;
            int dy = end.y - start.y;

            if (dx == 0 && dy == 0) {
                Log.d("movement", "No more than one cell left");

            } else if (dx == 0 || dy == 0) {
                if (dx == 0) {
                    if (dy == 1) {
                        Log.d("movement", "Move backward");


                    } else if (dy == -1) {
                        Log.d("movement", "Move forward");
                    } else {
                        Log.d("movement", "Unexpected" + String.valueOf(dy));
                    }
                } else if (dy == 0) {
                    if (dx == 1) {
                        Log.d("movement", "Move right");
                    } else if (dx == -1) {
                        Log.d("movement", "Move left");
                    } else {
                        Log.d("movement", "Unexpected" + String.valueOf(dx));
                    }
                } else {
                    Log.d("movement", "Unexpected State");
                }
            } else {

                if (dx == 1 && dy == 1) {
                    Log.d("movement", "Move bottom-right");
                } else if (dx == 1 && dy == -1) {
                    Log.d("movement", "Move top-right");
                } else if (dx == -1 && dy == 1) {
                    Log.d("movement", "Move bottom-left");
                } else if (dx == -1 && dy == -1) {
                    Log.d("movement", "Move top-left");
                } else {
                    Log.d("movement", "unexpected State");
                }

            }

            start = end;
        }
    }

    //TODO: ADD DISTANCE CHECKER
    public void moveChecker(int lastMove, int thisMove){
        int odometerAngle = 0;
        int odometerDistance = 0;

        //if(odometerDistance)

        if(lastMove == thisMove){
            mqttController.publish("/smartcar/control/throttle", FORWARD_SPEED);
            mqttController.publish("/smartcar/control/steering", STILL);
        }else{
            mqttController.publish("/smartcar/control/throttle", STILL);
            mqttController.publish("/smartcar/control/steering", STILL);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mqttController.publish("/smartcar/control/steering", "" + (thisMove-lastMove));
            mqttController.publish("/smartcar/control/throttle", TURN_SPEED);
            if(odometerAngle == (thisMove-lastMove)){
                mqttController.publish("/smartcar/control/steering", STILL);
                mqttController.publish("/smartcar/control/throttle", FORWARD_SPEED);
            }
        }
    }
}
