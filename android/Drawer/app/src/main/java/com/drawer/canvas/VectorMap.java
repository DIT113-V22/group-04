package com.drawer.canvas;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing MQTT connections, subscriptions, and publishing of messages.
 *
 * @author YukiMina14
 */
public class VectorMap {

    private final List<Vector> vectorList = new ArrayList<>();

    /**
     * Gets this Vector list.
     *
     * @return list of Vectors
     */
    public List<Vector> getVectorList() {
        return vectorList;
    }

    /**
     * Constructs the Vector map taking in the starting coordinates.
     *
     * @param x  X coordinate for the first vector
     * @param y  Y coordinate for the first vector
     */
    public VectorMap(int x, int y) {
        vectorList.add(new Vector(0, 0, x, y));
    }

    /**
     * Constructs empty Vector map.
     */
    public VectorMap() {

    }

    /**
     * add a Vector to Vector map.
     *
     * @param x vector x
     * @param y vector y
     */
    public void add(int x, int y) {
        Vector lastVector = vectorList.get(vectorList.size() - 1);   //gets last vector

        int vx = x - lastVector.absoluteX;
        int vy = y - lastVector.absoluteY;

        vectorList.add(new Vector(vx, vy, x, y));
    }

    /**
     * Erases the vector map.
     */
    public void clear() {
        vectorList.clear();
    }

    /**
     * Calculates the size of the vector Path.
     *
     * @return Size of the path
     */
    public double calculateSize() {
        double total = 0;

        for (Vector vector : vectorList) {
            total += Math.sqrt(Math.pow(vector.posX, 2) + Math.pow(vector.posY, 2));
        }

        return total;
    }

    /**
     * All coordinates to string.
     *
     * @return string of coordinates
     */
    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Vector vector : vectorList) {
            stringBuilder.append("-[X,").append(vector.posX).append("; Y,").append(vector.posY).append("] \n");
        }
        return stringBuilder.toString();
    }


    /**
     * gets the angle between 2 vectors.
     *
     * @param vector1 Base vector
     * @param vector2 2nd vector
     *
     * @return angle between vectors
     */
    public double getVectorAngle(Vector vector1, Vector vector2) {
        double angle;
        Vector dotProductVec = new Vector(vector1);
        dotProductVec.multiply(vector2);
        double dotProduct = dotProductVec.posX + dotProductVec.posY;

        angle = (dotProduct / (vector1.getMagnitude() * vector2.getMagnitude()));

        angle = (Math.acos(angle) * (180 / Math.PI));

        Vector newPoint = new Vector(vector1);
        newPoint.add(vector2);

        if (vector1.posX != 0) { //if not vertical
            double h = (double) vector1.posY / (double) vector1.posX;

            if (vector1.posX > 0) { // car pointing to the right
                if (newPoint.posY > (newPoint.posX * h)) {
                    angle *= -1;
                }
            } else { // car pointing left
                if (newPoint.posY < (newPoint.posX * h)) {
                    angle *= -1;
                }
            }

        } else if (newPoint.posY > 0) { // if downward
            if (newPoint.posX < 0) {
                angle *= -1;
            }
        } else if (newPoint.posY < 0) { //if upward
            if (newPoint.posX > 0) {
                angle *= -1;
            }
        }

        return angle;
    }

    /**
     * Multiplies All the vectors with another vector.
     *
     * @param vector Vector to multiply with the current one
     */
    public void multiply(Vector vector) {
        for (Vector v : vectorList) {
            v.multiply(vector);
        }
    }

    /**
     * Multiplies all vectors with integer.
     *
     * @param factor multiplication factor
     */
    public void multiply(int factor) {
        for (Vector v : vectorList) {
            v.multiply(factor);
        }
    }

    /**
     * Multiplies all vectors with float.
     *
     * @param factor multiplication factor
     */
    public void multiply(float factor) {
        for (Vector v : vectorList) {
            v.multiply(factor);
        }
    }
}