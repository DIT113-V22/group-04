package com.example.drawer;

public class Instruction {

    private double distance;
    private double angle;

    /**
     * Constructs the Instruction Object
     *
     * @param distance Distance before turning
     * @param angle Angle to turn
     */
    public Instruction(double distance, double angle) {
        this.distance = distance;
        this.angle = angle;
    }

    /**
     * Gets the distance of this instruction
     *
     * @return Distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the distance of this instruction
     *
     * @param distance Distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Gets the Angle of this instruction
     *
     * @return Angle
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Sets the distance of this instruction
     *
     * @param angle Angle
     */
    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Returns Properties in format of  "(distance),(angle);"
     *
     * @return String of Properties
     */
    @Override
    public String toString() {
        return "" + distance + ',' + angle + ';';
    }
}
