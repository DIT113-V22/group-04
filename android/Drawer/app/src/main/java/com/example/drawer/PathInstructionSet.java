package com.example.drawer;

import android.graphics.Point;
import android.util.Log;
import java.util.Queue;

public class PathInstructionSet {
    private int moveIndex = 1;
    private final int totalMoves;
    private final Queue<Point> pointQueue;
    private final MQTTController mqttController;
    private final String STILL = "0";
    private final String forwardSpeed;
    private final String TURN_SPEED = "5";
    private final String TAG = "PathExecutor";
    private final int FORWARD_ANGLE = 180;
    private final int RIGHT_ANGLE = 90;
    private final int LEFT_ANGLE = 270;
    private final int TOP_RIGHT_ANGLE = 135;
    private final int TOP_LEFT_ANGLE = 225;
    private final int BOTTOM_LEFT_ANGLE = 315;
    private final int BOTTOM_RIGHT_ANGLE = 45;
    private final int BACKWARD_ANGLE = 0;
    private int totalDistance = 0;
    private int lastTurn = 0;
    private final int diagonalDistance;
    private final int adjacentDistance;
    private Point previous;
    private Point current;

    public PathInstructionSet(Queue<Point> pointQueue, MQTTController mqttController, float pathScale, String speed) {
        this.pointQueue = pointQueue;
        this.mqttController = mqttController;
        this.diagonalDistance = (int) Math.sqrt(Math.pow(pathScale, 2) + Math.pow(pathScale, 2));
        this.adjacentDistance = (int) pathScale;
        this.totalMoves = pointQueue.size();
        this.forwardSpeed = speed;
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
        Log.d(TAG, "total distance so far: " + totalDistance);
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
        //totalDistance = distance + totalDistance;

        System.out.println("Move " + distance + "cm and turn to heading: " + turn);

        //Continue going forward if same direction
        if (lastTurn == turn) {
            mqttController.publish("/smartcar/control/throttle", forwardSpeed);
            mqttController.publish("/smartcar/control/steering", STILL);
            if (distance != 0) {
                mqttController.publish("/smartcar/control/distance", String.valueOf(distance));
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

            if (distance != 0) {
                mqttController.publish("/smartcar/control/distance", String.valueOf(distance));
                mqttController.publish("/smartcar/control/turn", String.valueOf(turn));
                mqttController.publish("/smartcar/control/throttle", forwardSpeed);
                mqttController.publish("/smartcar/control/steering", STILL);
            }

            //Turn until car says it has turned the said amount.

            //Go forward until the car says it has reached its destination.
            //mqttController.publish("/smartcar/control/throttle", FORWARD_SPEED);
            //mqttController.publish("/smartcar/control/steering", STILL);
        }
    }

    public void executeTurn() {

    }
}
