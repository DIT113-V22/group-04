package com.drawer.canvas;

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

}
