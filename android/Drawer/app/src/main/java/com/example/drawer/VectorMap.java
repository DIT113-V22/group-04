package com.example.drawer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class VectorMap {

    private List<Vector> vectorList = new ArrayList<>();


    public List<Vector> getVectorList() {
        return vectorList;
    }

    public VectorMap(int x, int y){
        vectorList.add(new Vector(0, 0, x, y) );

    }

    public VectorMap(){

    }


    public void add(int x, int y){
        Vector lastVector = vectorList.get(vectorList.size() -1);   //gets last vector


        int vx = x - lastVector.xAbsolute;
        int vy = y - lastVector.yAbsolute;

        vectorList.add(new Vector(vx, vy, x, y) );
        /*
        int vx = x - lastVector.xStart + lastVector.x;
        int vy = y - lastVector.yStart + lastVector.y;

        vectorList.add(vectorList.size() ,new Vector(vx, vy, x - vx, y - vy));*/


    }

    public void clear(){
        vectorList.clear();
    }


    public double calculateSize() {
        double Total = 0;

        for (Vector vector : vectorList) {
            Total += Math.sqrt(    Math.pow(vector.x, 2)  +    Math.pow(vector.y, 2)     );
        }

        //System.out.println("----------Total----------" + Total);
        return Total;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Vector vector : vectorList ) {
            stringBuilder.append("-[X," + vector.x + "; Y," + vector.y + "] \n");
        }
        return stringBuilder.toString();
    }


    public void multiply(Vector vector){
        for (Vector iVector : vectorList) {
            iVector.multiply(vector);
        }
    }

    public void multiply(int factor){
        for (Vector iVector: vectorList) {
            iVector.multiply(factor);
        }
    }

    public void multiply(float factor){
        for (Vector iVector: vectorList) {
            iVector.multiply(factor);
        }
    }

}





//********************private vector class**********************************
class Vector{
    public int xAbsolute;
    public int yAbsolute;
    public int x;
    public int y;

    //constructor
    public Vector(int x, int y, int xAbsolute, int yAbsolute){
        this.xAbsolute = xAbsolute;
        this.yAbsolute = yAbsolute;
        this.x = x;
        this.y = y;
    }

    public Vector multiply(Vector vector){
        this.x *= vector.x;
        this.y *= vector.y;
        return this;
    }

    public Vector multiply(int factor){
        return multiply(new Vector(factor, factor, 0, 0) );
    }

    public Vector multiply(float factor){
        this.x = (int) (this.x * factor);
        this.y = (int) (this.y * factor);
        return this;
    }





}