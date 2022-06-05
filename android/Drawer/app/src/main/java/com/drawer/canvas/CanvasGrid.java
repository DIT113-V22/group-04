package com.drawer.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.drawer.connectivity.MQTTController;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The CanvasGrid class serves as the foundation for the grid-based canvas used in DrawControl.
 * Class is based on the answer from "Mike M" to the question posed in this Stackoverflow
 * thread: https://stackoverflow.com/questions/24842550/2d-array-grid-on-drawing-canvas.
 *
 * @author Kev049
 * @author YukiMina14
 */
public class CanvasGrid extends View {

    public enum ResizeMode {
        AUTO_RESIZE,
        FIT_CONTENT
    }

    // Attributes
    private ResizeMode resizeMode = ResizeMode.FIT_CONTENT;
    private int numColumns = 4;
    private int numRows = 4;
    private int cellLength = 50;
    private float pathScale = 1;
    private final Paint blackPaint = new Paint();
    private boolean[][] cellChecked = new boolean[50][100];
    private int lastx;
    private int lasty;
    private Point drawPoint = new Point();
    MQTTController mqttController = MQTTController.getInstance();

    private boolean[][] pureCellChecked = new boolean[50][100];
    private VectorMap vectorMap = new VectorMap();
    private double vectorSmoothness = 3.0;
    private boolean firstTouch = true;

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

    public VectorMap getVectorMap() {
        return vectorMap;
    }

    public float getPathScale() {
        return pathScale;
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

    public void setPathScale(float pathScale) {
        this.pathScale = pathScale;
    }

    /**
     * Clears the drawing on the canvas and deletes any associated information.
     */
    public void clear() {
        cellChecked = new boolean[numColumns][numRows];
        pureCellChecked = new boolean[numColumns][numRows];
        firstTouch = true;
        vectorMap = new VectorMap();
        pointQueue.clear();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    /**
     * Resizes canvas grid to fit content.
     */
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

        } else if (resizeMode.equals(ResizeMode.FIT_CONTENT)) {
            int width = layoutParams.width;
            int height = layoutParams.height;

            numColumns = (int) Math.floor(width / cellLength);
            numRows = (int) Math.floor(height / cellLength);
        }

        cellChecked = new boolean[numColumns][numRows];
        firstTouch = true;
        pureCellChecked = new boolean[numColumns][numRows];
        invalidate();
    }

    /**
     * Draws a grid on canvas, and fills in spaces that has been drawn on.
     *
     * @param canvas to draw on.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (numColumns == 0 || numRows == 0) {
            return;
        }

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                drawPoint = new Point(i, j);
                if (cellChecked[i][j]) {
                    canvas.drawRect(i * cellLength, j * cellLength,
                            (i + 1) * cellLength, (j + 1) * cellLength,
                            blackPaint);
                }
                if (pureCellChecked[i][j]) {
                    if (!pointQueue.contains(drawPoint)) {
                        pointQueue.add(drawPoint);
                    }
                }
            }
        }

        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellLength, 0,
                    i * cellLength, numRows * cellLength, blackPaint);
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellLength,
                    numColumns * cellLength, i * cellLength, blackPaint);
        }
    }

    /**
     * Handles touch input on canvas.
     *
     * @param event motion touch event
     *
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int column = (int)(event.getX() / cellLength);
            int row = (int)(event.getY() / cellLength);

            try {
                //why disable going over the same point twice?
                cellChecked[column][row] = true;
                pureCellChecked[column][row] = true;

                if (!firstTouch && (Math.abs(row - lastx) > 1 || Math.abs(column - lasty) > 1)) {
                    gridDrawLine(lastx, lasty, column, row);
                } else {
                    firstTouch = false;
                }

                if (vectorMap.getVectorList().isEmpty()) {
                    vectorMap = new VectorMap(column, row);
                } else {
                    vectorMap.add(column, row);
                }

                lastx = column;
                lasty = row;

            } catch (Exception e) {
                e.printStackTrace();
            }
            invalidate();

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int column = (int)(event.getX() / cellLength);
            int row = (int)(event.getY() / cellLength);

            try {
                if (Math.hypot(column - lastx, row - lasty) > vectorSmoothness) {
                    cellChecked[column][row] = true;
                    pureCellChecked[column][row] = true;
                    // if the difference between current x, y and new x, y is bigger than 1 draw a line in between
                    if (Math.abs(row - lastx) > .5 || Math.abs(column - lasty) > .5) {
                        gridDrawLine(lastx, lasty, column, row);
                    }
                    if (column != lastx && row != lasty) {
                        vectorMap.add(column, row);
                    }
                    lastx = column;
                    lasty = row;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            invalidate();
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * Sends the car movement instructions to execute.
     * The current implementation assumes slow drawing (i.e. each cell will be adjacent
     * to another cell in one of the 8 possible directions. The implementation is unfortunately
     * incompatible with the Bresenham's drawing algorithm.
     *
     * @param speed at which car will drive
     */
    public void executePath(String speed) {
        PathInstructionSet pathInstructionSet = new PathInstructionSet(pointQueue, mqttController, pathScale, speed);
        mqttController.executeInstructionSet(pathInstructionSet);
    }

    /**
     * draws a line between 2 points using Bresenham's line Algorithm
     * Src: https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
     *
     * @param x0 line start x
     * @param y0 line start y
     * @param x1 line end x
     * @param y1 line end y
     */
    private void gridDrawLine(int x0, int y0, int x1, int y1) {

        //Delta X, Y
        int dx = Math.abs(x1 - x0);
        int dy = -Math.abs(y1 - y0);

        //Increments variable using ternary operator
        int sx = (x0 < x1) ? 1 : -1;
        int sy = (y0 < y1) ? 1 : -1;

        int error = dx + dy;

        while (true) {
            cellChecked[x0][y0] = true;

            if (x0 == x1 && y0 == y1) {
                break;
            }

            int error2 = error + error; // 2* error
            if (error2 >= dy) {
                if (x0 == x1) {
                    break;
                }
                error += dy;
                x0 += sx;
            }
            if (error2 <= dx) {
                if (y0 == y1) {
                    break;
                }
                error += dx;
                y0 += sy;
            }
        }
    }
}