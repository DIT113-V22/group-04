package com.example.drawer;

import android.graphics.Point;
import android.util.Log;
import java.util.Queue;

public class PathInstructionSet {
    private int moveIndex = 1;
    private final int totalMoves;
    private final Queue<Point> pointQueue;
    private final MQTTController mqttController;
    private final String still = "0";
    private final String forwardSpeed;
    private final String turnSpeed = "5";
    private final String tag = "PathExecutor";
    private final int forwardAngle = 180;
    private final int rightAngle = 90;
    private final int leftAngle = 270;
    private final int topRightAngle = 135;
    private final int topLeftAngle = 225;
    private final int bottomLeftAngle = 315;
    private final int bottomRightAngle = 45;
    private final int backwardAngle = 0;
    private int totalDistance = 0;
    private int lastTurn = 0;
    private final int diagonalDistance;
    private final int adjacentDistance;
    private Point previous;
    private Point current;

    public PathInstructionSet(Queue<Point> pointQueue, MQTTController mqttController, float pathScale, String speed) {
        this.pointQueue = pointQueue;
        this.mqttController = mqttController;
        this.diagonalDistance = (int) Math.sqrt(Math.pow(pathScale, 2) * 2);
        this.adjacentDistance = (int) pathScale;
        this.totalMoves = pointQueue.size();
        this.forwardSpeed = speed;
    }

    public void start() {
        Log.d(tag, "moveIndex: " + moveIndex);
        Log.d(tag, "totalMoves: " + totalMoves);
        previous = pointQueue.poll();
        current = pointQueue.poll();
        if (previous != null && current != null) {
            moveBetween(previous, current);
        }
    }

    public void continueExecution() {
        Log.d(tag, "Previous instruction executed, continuing:");
        Log.d(tag, "moveIndex: " + moveIndex);
        Log.d(tag, "totalMoves: " + totalMoves);
        if (moveIndex < totalMoves) {
            // Continue
            current = pointQueue.poll();
            if (current != null) {
                moveBetween(this.previous, current);
            }
        } else {
            System.out.println("Instruction set complete");
            mqttController.publish("/smartcar/control/throttle", still);
            mqttController.publish("/smartcar/control/steering", still);
            mqttController.publish("/smartcar/control/auto", "0");
        }
    }

    public void moveBetween(Point previous, Point current) {
        Log.d(tag, previous.toString());
        Log.d(tag, current.toString());
        Log.d(tag, "total distance so far: " + totalDistance);
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
                        thisTurn = backwardAngle * -1;
                    } else {
                        thisDistance = adjacentDistance;
                        thisTurn = backwardAngle;
                    }
                    executeMove(thisDistance, thisTurn);
                    lastTurn = thisTurn;
                } else if (dy == -1) {
                    Log.d("movement", "Move forward");
                    thisDistance = adjacentDistance;
                    thisTurn = forwardAngle;
                    executeMove(thisDistance, thisTurn);
                    lastTurn = thisTurn;
                } else {
                    Log.d("movement", "Unexpected" + dy);
                }
            } else {
                if (dx == 1) {
                    Log.d("movement", "Move right");
                    thisDistance = adjacentDistance;
                    thisTurn = rightAngle;
                    executeMove(thisDistance, thisTurn);
                    lastTurn = thisTurn;
                } else if (dx == -1) {
                    Log.d("movement", "Move left");
                    thisDistance = adjacentDistance;
                    thisTurn = leftAngle;
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
                thisTurn = bottomRightAngle;
                executeMove(thisDistance, thisTurn);
                lastTurn = thisTurn;
            } else if (dx == 1 && dy == -1) {
                Log.d("movement", "Move top-right");
                thisDistance = diagonalDistance;
                thisTurn = topRightAngle;
                executeMove(thisDistance, thisTurn);
                lastTurn = thisTurn;
            } else if (dx == -1 && dy == 1) {
                Log.d("movement", "Move bottom-left");
                thisDistance = diagonalDistance;
                thisTurn = bottomLeftAngle;
                executeMove(thisDistance, thisTurn);
                lastTurn = thisTurn;
            } else if (dx == -1 && dy == -1) {
                Log.d("movement", "Move top-left");
                thisDistance = diagonalDistance;
                thisTurn = topLeftAngle;
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
        totalDistance = distance + totalDistance;

        System.out.println("Move " + distance + "cm and turn to heading: " + turn);

        //Continue going forward if same direction
        if (lastTurn == turn) {
            mqttController.publish("/smartcar/control/throttle", forwardSpeed);
            mqttController.publish("/smartcar/control/steering", still);
            if (totalDistance != 0) {
                mqttController.publish("/smartcar/control/distance", String.valueOf(totalDistance));
            }
        } else {
            //Stops car for certain amount of time before next move.
            mqttController.publish("/smartcar/control/throttle", still);
            mqttController.publish("/smartcar/control/steering", still);

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (totalDistance != 0) {
                mqttController.publish("/smartcar/control/distance", String.valueOf(totalDistance));
                mqttController.publish("/smartcar/control/turn", String.valueOf(turn));
                mqttController.publish("/smartcar/control/throttle", forwardSpeed);
                mqttController.publish("/smartcar/control/steering", still);
            }
        }
    }

    public void executeTurn() {

    }
}