package com.example.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CanvasGrid extends View {
    private int numColumns;
    private int numRows;
    private int cellLength = 32;

    private Paint blackPaint = new Paint();
    private boolean[][] cellChecked = new boolean[50][100];

    public CanvasGrid(Context context) {
        //this(context, null);
        super(context);
    }

    public CanvasGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
        calculateDimensions();
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

        //cellLength = getWidth() / numColumns;
        //int h = getHeight();
        //int h = 510;

        //cellLength = h / numRows;
        //cellLength = 32;

        cellChecked = new boolean[numColumns][numRows];

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);

        if (numColumns == 0 || numRows == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (cellChecked[i][j]) {

                    canvas.drawRect(i * cellLength, j * cellLength,
                            (i + 1) * cellLength, (j + 1) * cellLength,
                            blackPaint);
                }
            }
        }

        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellLength, 0, i * cellLength, height, blackPaint);
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellLength, width, i * cellLength, blackPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.d("logfalse", String.valueOf(cellChecked[0][0]));

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            float x = event.getX();
            float y = event.getY();

            Log.d("infinte", String.valueOf(x));
            Log.d("celll", String.valueOf(cellLength));

            int column = (int)(event.getX() / cellLength);
            int row = (int)(event.getY() / cellLength);

//            if (row >= 30 || row < 0) {
//                row = 29;
//            }
//
//            if (column >= 16 || column < 0) {
//                column = 15;
//            }

            Log.d("infinte", String.valueOf(row));
            Log.d("infinte", String.valueOf(column));

            cellChecked[column-1][row-1] = true;
            invalidate();

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            int column = (int)(event.getX() / cellLength);
            int row = (int)(event.getY() / cellLength);

//            if (row >= 30 || row < 0) {
//                row = 29;
//            }
//
//            if (column >= 16 || column < 0) {
//                column = 15;
//            }

            cellChecked[column-1][row-1] = true;
            invalidate();
        }

        return true;
    }
}