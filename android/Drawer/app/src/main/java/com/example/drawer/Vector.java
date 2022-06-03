package com.example.drawer;

/**
 * Class for creating Mathematical vectors.
 *
 * @author YukiMina14
 */

public class Vector {
    public int absoluteX;
    public int absoluteY;
    public int posX;
    public int posY;

    /**
     * Constructs the Vector Object, notes down the absolute coordinates of the Vector's Tip.
     *
     * @param x vector x value
     * @param y vector y value
     * @param absoluteX absolute x coordinate of tip
     * @param absoluteY absolute y coordinate of tip
     */
    public Vector(int x, int y, int absoluteX, int absoluteY) {
        this.absoluteX = absoluteX;
        this.absoluteY = absoluteY;
        this.posX = x;
        this.posY = y;
    }

    /**
     * Constructs the Vector Object, without noting down tip coordinates.
     *
     * @param x vector x value
     * @param y vector y value
     */
    public Vector(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    /**
     * Constructs the Vector Object by copying another.
     *
     * @param vector vector to copy from
     */
    public Vector(Vector vector) {
        this.posX = vector.posX;
        this.posY = vector.posY;
        this.absoluteX = vector.absoluteX;
        this.absoluteY = vector.absoluteY;
    }

    /**
     * Adds 2 vectors together using another vector.
     *
     * @param vector Vector to add to this one
     *
     * @return Current Vector
     */
    public Vector add(Vector vector) {
        this.posX += vector.posX;
        this.posY += vector.posY;
        return this;
    }

    /**
     * Multiplies 2 vectors together using another vector.
     *
     * @param vector Vector to multiply with the current one
     *
     * @return Current Vector
     */
    public void multiply(Vector vector) {
        this.posX *= vector.posX;
        this.posY *= vector.posY;
    }

    /**
     * Multiplies vector with integer.
     *
     * @param factor multiplication factor
     *
     * @return Current Vector
     */
    public void multiply(int factor) {
        multiply(new Vector(factor, factor, 0, 0));
    }

    /**
     * Multiplies vector with float.
     *
     * @param factor multiplication factor
     *
     * @return Current Vector
     */
    public void multiply(float factor) {
        this.posX = (int) (this.posX * factor);
        this.posY = (int) (this.posY * factor);
    }

    /**
     * Calculates the size of the vector.
     *
     * @return Vector Size
     */
    public double getMagnitude() {
        return Math.sqrt(Math.pow(posX, 2) + Math.pow(posY, 2));
    }
}
