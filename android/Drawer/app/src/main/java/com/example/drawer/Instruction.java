package com.example.drawer;

public class Instruction {

    private double distance;

    private double turn;

    public Instruction(double distance, double turn){
        this.distance = distance;
        this.turn = turn;
    }



    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }



    public double getTurn() {
        return turn;
    }

    public void setTurn(double turn) {
        this.turn = turn;
    }


}