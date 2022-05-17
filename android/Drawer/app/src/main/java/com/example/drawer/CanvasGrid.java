package com.example.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.Object;

import java.util.LinkedList;
import java.util.Queue;

/*
    Class is based on the answer from "Mike M" to the question posed in this Stackoverflow
    thread: https://stackoverflow.com/questions/24842550/2d-array-grid-on-drawing-canvas
 */

public class CanvasGrid extends View {

    public enum ResizeMode{
        AUTO_RESIZE,
        FIT_CONTENT
    }

    // Attributes
    private ResizeMode resizeMode = ResizeMode.FIT_CONTENT;
    private int numColumns = 4;
    private int numRows = 4;
    private int cellLength = 50;
    private Paint blackPaint = new Paint();
    private boolean[][] cellChecked = new boolean[50][100];
    int lastx;
    int lasty;

    Queue<Point> pointQueue = new LinkedList<>();

    // Constructors
    public CanvasGrid(Context context) {
        super(context);
    }

    public CanvasGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    // Getters
    public int getNumColumns() {
        return numColumns;
    }

    public int getNumRows() {
        return numRows;
    }

    public ResizeMode getResizeMode() {
        return resizeMode;
    }

    // Setters
    public void setCellLength(int cellLength) {
        this.cellLength = cellLength;
        calculateDimensions();
    }

    public void setResizeMode(ResizeMode resizeMode) {
        this.resizeMode = resizeMode;
        calculateDimensions();
    }

    public void clear(){
        cellChecked = new boolean[numColumns][numRows];
        pointQueue.clear();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    private void calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            Log.d("tag4", "Number of rows or columns are less than 1");
            return;
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();

        if (resizeMode.equals(ResizeMode.AUTO_RESIZE)) {
            int width = cellLength * numColumns;
            int height = cellLength * numRows;

            layoutParams.width = width;
            layoutParams.height = height;

        } else if (resizeMode.equals(ResizeMode.FIT_CONTENT)){
            int width = layoutParams.width;
            int height = layoutParams.height;

            numColumns = (int) Math.floor( width / cellLength );
            numRows = (int) Math.floor( (height / cellLength) );
        }

        cellChecked = new boolean[numColumns][numRows];
        invalidate();
    }

    // The current implementation assumes slow drawing (i.e. each cell will be adjacent
    // to another cell in one of the 8 possible directions.
    // The implementation is currently incompatible with the Bresenham's drawing algorithm.
    public void executePath() {

        Point start = pointQueue.poll();

        Point end;

        // TODO Need to find proper upper limit for the loop condition -KC
        for (int i = 0; i < (pointQueue.size()*10); i++) {

            end = pointQueue.poll();

            Log.d("abcd", "start" + String.valueOf(start));
            Log.d("abcd", "end" + String.valueOf(end));

            int dx = end.x - start.x;
            int dy = end.y - start.y;

            if (dx == 0 && dy == 0) {
                Log.d("movement", "No more than one cell left");

            } else if (dx == 0 || dy == 0) {
                if (dx == 0) {
                    if (dy == 1) {
                        Log.d("movement", "Move backward");
                    } else if (dy == -1) {
                        Log.d("movement", "Move forward");
                    } else {
                        Log.d("movement", "Unexpected" + String.valueOf(dy));
                    }
                } else if (dy == 0) {
                    if (dx == 1) {
                        Log.d("movement", "Move right");
                    } else if (dx == -1) {
                        Log.d("movement", "Move left");
                    } else {
                        Log.d("movement", "Unexpected" + String.valueOf(dx));
                    }
                } else {
                    Log.d("movement", "Unexpected State");
                }
            } else {

                if (dx == 1 && dy == 1) {
                    Log.d("movement", "Move bottom-right");
                } else if (dx == 1 && dy == -1) {
                    Log.d("movement", "Move top-right");
                } else if (dx == -1 && dy == 1) {
                    Log.d("movement", "Move bottom-left");
                } else if (dx == -1 && dy == -1) {
                    Log.d("movement", "Move top-left");
                } else {
                    Log.d("movement", "unexpected State");
                }

            }

            start = end;

        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (numColumns == 0 || numRows == 0) {
            return;
        }

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (cellChecked[i][j]) {
                    canvas.drawRect(i * cellLength, j * cellLength,
                            (i + 1) * cellLength, (j + 1) * cellLength,
                            blackPaint);
                    if (!pointQueue.contains(new Point(i, j))) {
                        pointQueue.add(new Point(i, j));
                    }
                }
            }
        }

//        for (Point p : pointQueue) {
//            Log.d("testa", String.valueOf(p));
//        }


        // TODO Are getters needed here? Perhaps attributes should be used -KC

        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellLength, 0, i * cellLength, getNumRows() * cellLength, blackPaint);
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellLength, getNumColumns() * cellLength, i * cellLength, blackPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            int column = (int)(event.getX() / cellLength);
            int row = (int)(event.getY() / cellLength);

            try {

                if (cellChecked[column][row] != true) {
                    cellChecked[column][row] = true;
                    lastx = column;
                    lasty = row;

                }

            } catch(Exception e) {
                e.printStackTrace();
            }

            invalidate();

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            int column = (int)(event.getX() / cellLength);
            int row = (int)(event.getY() / cellLength);

            try {

                if (cellChecked[column][row] != true) {
                    cellChecked[column][row] = true;
                    // if the difference between current x, y and new x, y is bigger than 1 draw a line in between
                    //if(Math.abs(row - lastx) > 1 || Math.abs(column - lasty) > 1 ) gridDrawLine(lastx, lasty, column, row);
                    
                    if(column != lastx && row != lasty){
                        vectorMap.add(column, row);
                    }
                    lastx = column;
                    lasty = row;
                }

            } catch(Exception e) {
                e.printStackTrace();
            }

            invalidate();
        }
        System.out.println("-----------Total------------: "+ vectorMap.calculateSize());
        //System.out.println(vectorMap.toString());

        return true;
    }


    //Bresenham's line algorithm for cell checked src: https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
        // TODO: 2022-05-10 might need to rewrite the while true

//    private void gridDrawLine(int x0, int y0, int x1, int y1){
//        //Delta X, Y
//        int dx = Math.abs(x1 - x0);
//        int dy = -Math.abs(y1 - y0);
//
//        //incrementations variable using ternary operator
//        int sx = (x0 < x1)? 1: -1;
//        int sy = (y0 < y1)? 1: -1;
//
//        int error = dx + dy;
//
//        while (true){
//            cellChecked[x0][y0] = true;
//
//            if(x0 == x1 && y0 == y1) break;
//            int error2 = error + error; // 2* error
//
//            if (error2 >= dy){
//                if(x0 == x1) break;
//                error += dy;
//                x0 += sx;
//            }
//            if (error2 <= dx){
//                if(y0 == y1) break;
//                error += dx;
//                y0 += sy;
//            }
//        }
//    }


}

