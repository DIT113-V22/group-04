package com.drawer.canvas;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

/**
 * VectorMap implementation based on canvas-grid solution.
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
}