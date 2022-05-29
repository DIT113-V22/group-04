package com.example.drawer;

import android.graphics.Point;
import android.util.Log;
import java.util.Queue;

public class DrawControlRun {
    int moveIndex = 1;
    int totalMoves;
    Queue<Point> pointQueue;
    MQTTController mqttController;
    final String STILL = "0";
    final String FORWARD_SPEED = "25";
    final String TURN_SPEED = "5";
    private final String TAG = "PathExecutor";
    final int FORWARD_ANGLE = 0;
    final int RIGHT_ANGLE = 90;
    final int LEFT_ANGLE = -90;
    final int TOP_RIGHT_ANGLE = 45;
    final int TOP_LEFT_ANGLE = -45;
    final int BOTTOM_LEFT_ANGLE = -135;
    final int BOTTOM_RIGHT_ANGLE = 135;
    final int BACKWARD_ANGLE = 180;
    int totalDistance = 0;
    int lastTurn = 0;
    //int thisTurn = 0;
    //double thisDistance = 0;
    int diagonalDistance;
    int adjacentDistance;
    Point previous;
    Point current;

    public DrawControlRun(float pathScale, Queue<Point> pointQueue, MQTTController mqttController) {
        this.pointQueue = pointQueue;
        this.mqttController = mqttController;
        this.diagonalDistance = (int) Math.sqrt(pathScale * 2);
        this.adjacentDistance = (int) pathScale;
        this.totalMoves = pointQueue.size();
    }

    public void start() {
        Log.d(TAG, "moveIndex: " + moveIndex);
        Log.d(TAG, "totalMoves: " + totalMoves);
        previous = pointQueue.poll();
        current = pointQueue.poll();
        if (previous != null && current != null) {
            moveBetween(previous, current);
        }
    }

    public void continueExecution() {
        Log.d(TAG, "Previous instruction executed, continuing:");
        Log.d(TAG, "moveIndex: " + moveIndex);
        Log.d(TAG, "totalMoves: " + totalMoves);
        if (moveIndex < totalMoves) {
            // Continue
            current = pointQueue.poll();
            if (current != null) {
                moveBetween(this.previous, current);
            }
        } else {
            System.out.println("Instruction set complete");
            mqttController.publish("/smartcar/control/throttle", STILL);
            mqttController.publish("/smartcar/control/steering", STILL);
            mqttController.publish("/smartcar/control/auto", "0");
        }
    }

    public void moveBetween(Point previous, Point current) {
        Log.d(TAG, previous.toString());
        Log.d(TAG, current.toString());
        Log.d(TAG, "total distance: " + totalDistance);
        int thisDistance;
        int thisTurn;
        int dx = current.x - previous.x;
        int dy = current.y - previous.y;

        if (dx == 0 && dy == 0) {
            Log.d("movement", "No more than one cell left");
        } else if (dx == 0 || dy == 0) {
            if (dx == 0) {
                if (dy == 1) {
                    Log.d("movement", "Move backward");
                    if (lastTurn < 0) {
                        thisDistance = adjacentDistance;
                        thisTurn = BACKWARD_ANGLE * -1;
                    } else {
                        thisDistance = adjacentDistance;
                        thisTurn = BACKWARD_ANGLE;
                    }
                    executeMove(thisDistance, thisTurn);
                    lastTurn = thisTurn;
                } else if (dy == -1) {
                    Log.d("movement", "Move forward");
                    thisDistance = adjacentDistance;
                    thisTurn = FORWARD_ANGLE;
                    executeMove(thisDistance, thisTurn);
                    lastTurn = thisTurn;
                } else {
                    Log.d("movement", "Unexpected" + dy);
                }
            } else {
                if (dx == 1) {
                    Log.d("movement", "Move right");
                    thisDistance = adjacentDistance;
                    thisTurn = RIGHT_ANGLE;
                    executeMove(thisDistance, thisTurn);
                    lastTurn = thisTurn;
                } else if (dx == -1) {
                    Log.d("movement", "Move left");
                    thisDistance = adjacentDistance;
                    thisTurn = LEFT_ANGLE;
                    executeMove(thisDistance, thisTurn);
                    lastTurn = thisTurn;
                } else {
                    Log.d("movement", "Unexpected" + dx);
                }
            }
        } else {
            if (dx == 1 && dy == 1) {
                Log.d("movement", "Move bottom-right");
                thisDistance = diagonalDistance;
                thisTurn = BOTTOM_RIGHT_ANGLE;
                executeMove(thisDistance, thisTurn);
                lastTurn = thisTurn;
            } else if (dx == 1 && dy == -1) {
                Log.d("movement", "Move top-right");
                thisDistance = diagonalDistance;
                thisTurn = TOP_RIGHT_ANGLE;
                executeMove(thisDistance, thisTurn);
                lastTurn = thisTurn;
            } else if (dx == -1 && dy == 1) {
                Log.d("movement", "Move bottom-left");
                thisDistance = diagonalDistance;
                thisTurn = BOTTOM_LEFT_ANGLE;
                executeMove(thisDistance, thisTurn);
                lastTurn = thisTurn;
            } else if (dx == -1 && dy == -1) {
                Log.d("movement", "Move top-left");
                thisDistance = diagonalDistance;
                thisTurn = TOP_LEFT_ANGLE;
                executeMove(thisDistance, thisTurn);
                lastTurn = thisTurn;
            } else {
                Log.d("movement", "unexpected State");
            }
        }
        this.previous = current;
        this.moveIndex++;
    }

    public void executeMove(int distance, int turn) {
        String traveledDistance = "false";
        String traveledAngle = "false";
        totalDistance = distance + totalDistance;

        System.out.println("Move " + distance + "cm and turn " + (turn - lastTurn));

        //Continue going forward if same direction
        if (lastTurn == turn) {
            mqttController.publish("/smartcar/control/throttle", FORWARD_SPEED);
            mqttController.publish("/smartcar/control/steering", STILL);
            if (totalDistance != 0) {
                mqttController.publish("/smartcar/control/distance", String.valueOf(totalDistance));
            }
        } else {
            //Stops car for certain amount of time before next move.
            mqttController.publish("/smartcar/control/throttle", STILL);
            mqttController.publish("/smartcar/control/steering", STILL);

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (totalDistance != 0) {
                mqttController.publish("/smartcar/control/distance", String.valueOf(totalDistance));
                //mqttController.publish("/smartcar/control/throttle", FORWARD_SPEED);
                //mqttController.publish("/smartcar/control/steering", STILL);
            }
            System.out.println(totalDistance);
            //Turn until car says it has turned the said amount.
            //mqttController.publish("/smartcar/control/throttle", TURN_SPEED);
            //mqttController.publish("/smartcar/control/steering", String.valueOf(turn - lastTurn));

            //Go forward until the car says it has reached its destination.
            //mqttController.publish("/smartcar/control/throttle", FORWARD_SPEED);
            //mqttController.publish("/smartcar/control/steering", STILL);
        }
    }
}
