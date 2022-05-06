package com.example.drawer;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class TempCanvas extends Activity {

    private CanvasGrid pixelGrid ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_canvas);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);




        //CanvasGrid pixelGrid = new CanvasGrid(this);
        CanvasGrid pixelGrid = findViewById(R.id.canvasGrid);
        //pixelGrid = new CanvasGrid(this);
        pixelGrid.setNumColumns(60);
        pixelGrid.setNumRows(60);
        pixelGrid.setCellLength(10);

        //LayoutParams params = new LayoutParams(1000, 1400);

        //setContentView(pixelGrid);
        //addContentView(pixelGrid, params);*/
    }

}
