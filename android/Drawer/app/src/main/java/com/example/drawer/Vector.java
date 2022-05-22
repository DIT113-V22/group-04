package com.example.drawer;

public class Vector {
    public int absoluteX;
    public int absoluteY;
    public int posX;
    public int posY;

    //constructor
    public Vector(int x, int y, int absoluteX, int absoluteY) {
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
        this.posX = x;
        this.posY = y;
    }

    public Vector(int x, int y){
        this.posX = x;
        this.posY = y;
    }

    //copy constructor
    public Vector(com.example.drawer.Vector vector){
        this.posX = vector.posX;
        this.posY = vector.posY;
        this.absoluteX = vector.absoluteX;
        this.absoluteY = vector.absoluteY;
    }

    public com.example.drawer.Vector add(com.example.drawer.Vector vector){
        this.posX += vector.posX;
        this.posY += vector.posY;
        return this;
    }

    public Vector multiply(Vector vector) {
        this.posX *= vector.posX;
        this.posY *= vector.posY;
        return this;
    }

    public Vector multiply(int factor) {
        return multiply(new Vector(factor, factor, 0, 0));
    }

    public Vector multiply(float factor) {
        this.posX = (int) (this.posX * factor);
        this.posY = (int) (this.posY * factor);
        return this;
    }

    public double getMagnitude(){
        return Math.sqrt( Math.pow(posX, 2) + Math.pow(posY, 2) );
    }



}
