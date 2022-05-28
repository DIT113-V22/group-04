package com.example.drawer;

import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Queue;

public class DrawControlRun implements Runnable{
    Queue<Point> pointQueue;
    MQTTController mqttController;
    final String STILL = "0";
    final String FORWARD_SPEED = "25";
    final String TURN_SPEED = "5";
    final int FORWARD_ANGLE = 0;
    final int RIGHT_ANGLE = 90;
    final int LEFT_ANGLE = -90;
    final int TOP_RIGHT_ANGLE = 45;
    final int TOP_LEFT_ANGLE = -45;
    final int BOTTOM_LEFT_ANGLE = -135;
    final int BOTTOM_RIGHT_ANGLE = 135;
    final int BACKWARD_ANGLE = 180;
    double totalDistance = 0;
    int lastMove = 0;
    int thisMove = 0;
    double thisDistance = 0;
    double diagonalDistance;
    double adjacentDistance;



    public DrawControlRun(double diagonalDistance, double adjacentDistance, Queue<Point> pointQueue, MQTTController mqttController){
        this.pointQueue = pointQueue;
        this.mqttController = mqttController;
        this.diagonalDistance = diagonalDistance;
        this.adjacentDistance = adjacentDistance;

    }

    @Override
    public void run() {
        Point start = pointQueue.poll();

        //mqttController.publish("/smartcar/control/draw", "100");


        Point end;

        // TODO Need to find proper upper limit for the loop condition -KC
        for (int i = 0; i < (pointQueue.size() * 10); i++) {

            end = pointQueue.poll();

            Log.d("abcd", "start" + String.valueOf(start));
            Log.d("abcd", "end" + String.valueOf(end));
            Log.d("abcd", "SIZE" + String.valueOf(pointQueue.size()));

            int dx = end.x - start.x;
            int dy = end.y - start.y;

            if (dx == 0 && dy == 0) {
                Log.d("movement", "No more than one cell left");

            } else if (dx == 0 || dy == 0) {
                if (dx == 0) {
                    if (dy == 1) {
                        Log.d("movement", "Move backward");
                        if(lastMove < 0) {
                            thisDistance = adjacentDistance;
                            thisMove = BACKWARD_ANGLE * -1;
                        }else {
                            thisDistance = adjacentDistance;
                            thisMove = BACKWARD_ANGLE;
                        }
                        moveChecker();
                        lastMove = thisMove;

                    } else if (dy == -1) {
                        Log.d("movement", "Move forward");
                        thisDistance = adjacentDistance;
                        thisMove = FORWARD_ANGLE;
                        moveChecker();
                        lastMove = thisMove;

                    } else {
                        Log.d("movement", "Unexpected" + String.valueOf(dy));
                    }
                } else if (dy == 0) {
                    if (dx == 1) {
                        Log.d("movement", "Move right");
                        thisDistance = adjacentDistance;
                        thisMove = RIGHT_ANGLE;
                        moveChecker();
                        lastMove = thisMove;
                    } else if (dx == -1) {
                        Log.d("movement", "Move left");
                        thisDistance = adjacentDistance;
                        thisMove = LEFT_ANGLE;
                        moveChecker();
                        lastMove = thisMove;
                    } else {
                        Log.d("movement", "Unexpected" + String.valueOf(dx));
                    }
                } else {
                    Log.d("movement", "Unexpected State");
                }
            } else {

                if (dx == 1 && dy == 1) {
                    Log.d("movement", "Move bottom-right");
                    thisDistance = diagonalDistance;
                    thisMove = BOTTOM_RIGHT_ANGLE;
                    moveChecker();
                    lastMove = thisMove;
                } else if (dx == 1 && dy == -1) {
                    Log.d("movement", "Move top-right");
                    thisDistance = diagonalDistance;
                    thisMove = TOP_RIGHT_ANGLE;
                    moveChecker();
                    lastMove = thisMove;
                } else if (dx == -1 && dy == 1) {
                    Log.d("movement", "Move bottom-left");
                    thisDistance = diagonalDistance;
                    thisMove = BOTTOM_LEFT_ANGLE;
                    moveChecker();
                    lastMove = thisMove;
                } else if (dx == -1 && dy == -1) {
                    Log.d("movement", "Move top-left");
                    thisDistance = diagonalDistance;
                    thisMove = TOP_LEFT_ANGLE;
                    moveChecker();
                    lastMove = thisMove;
                } else {
                    Log.d("movement", "unexpected State");
                }

            }
            if(pointQueue.size() == 0) {
                mqttController.publish("/smartcar/control/throttle", STILL);
                mqttController.publish("/smartcar/control/steering", STILL);
            }

            start = end;
        }
    }

    //TODO: ADD DISTANCE CHECKER
    public void moveChecker(){
        String traveledDistance = "false";
        String traveledAngle = "false";

        thisDistance = thisDistance * 100;
        totalDistance = thisDistance +totalDistance;
        System.out.println("Move " + thisDistance + "m and turn " + (thisMove-lastMove));

        //Continue going forward if same direction
        if(lastMove == thisMove){

            mqttController.publish("/smartcar/control/throttle", FORWARD_SPEED);
            mqttController.publish("/smartcar/control/steering", STILL);
            if(totalDistance != 0){
                mqttController.publish("/smartcar/control/distance", ""+totalDistance);
            }

        }else{
            //Stops car for certain amount of time before next move.
            mqttController.publish("/smartcar/control/throttle", STILL);
            mqttController.publish("/smartcar/control/steering", STILL);

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(totalDistance != 0){
                mqttController.publish("/smartcar/control/distance", String.valueOf(totalDistance));
            }
            System.out.println(totalDistance);
            //Turn until car says it has turned the said amount.
            //    mqttController.publish("/smartcar/control/steering", "" + (thisMove-lastMove));
            //    mqttController.publish("/smartcar/control/throttle", TURN_SPEED);
//
            //    while(traveledAngle == "false"){
            //        mqttController.subscribe("/smartcar/odometer/anglebool");
            //        traveledAngle = mqttController.finishAngle;
            //    }
//
            //    //Go forward until the car says it has reached its destination.
            //    mqttController.publish("/smartcar/control/steering", STILL);
            //    mqttController.publish("/smartcar/control/throttle", FORWARD_SPEED);

        }
        while (traveledDistance == "false") {
            traveledDistance = mqttController.finishDistance;
        }
    }
}
