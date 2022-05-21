package com.example.drawer;

import java.util.ArrayList;
import java.util.List;

public class VectorMap {

    private List<Vector> vectorList = new ArrayList<>();

    public List<Vector> getVectorList() {
        return vectorList;
    }

    public VectorMap(int x, int y) {
        vectorList.add(new Vector(0, 0, x, y));
    }

    public VectorMap() {

    }

    public void add(int x, int y) {
        Vector lastVector = vectorList.get(vectorList.size() - 1);   //gets last vector


        int vx = x - lastVector.absoluteX;
        int vy = y - lastVector.absoluteY;

        vectorList.add(new Vector(vx, vy, x, y));
        /*
        int vx = x - lastVector.xStart + lastVector.x;
        int vy = y - lastVector.yStart + lastVector.y;

        vectorList.add(vectorList.size() ,new Vector(vx, vy, x - vx, y - vy));*/
    }

    public void clear() {
        vectorList.clear();
    }

    public double calculateSize() {
        double total = 0;

        for (Vector vector : vectorList) {
            total += Math.sqrt(Math.pow(vector.posX, 2) + Math.pow(vector.posY, 2));
        }

        //System.out.println("----------Total----------" + Total);
        return total;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Vector vector : vectorList) {
            stringBuilder.append("-[X," + vector.posX + "; Y," + vector.posY + "] \n");
        }
        return stringBuilder.toString();
    }

    public void multiply(Vector vector) {
        for (Vector v : vectorList) {
            v.multiply(vector);
        }
    }

    public void multiply(int factor) {
        for (Vector v : vectorList) {
            v.multiply(factor);
        }
    }

    public void multiply(float factor) {
        for (Vector v : vectorList) {
            v.multiply(factor);
        }
    }

}