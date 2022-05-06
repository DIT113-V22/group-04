package com.example.drawer;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class TempCanvas extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        CanvasGrid pixelGrid = new CanvasGrid(this);
        //CanvasGrid pixelGrid = findViewById(R.id.canvasGrid);
        //pixelGrid = new CanvasGrid(this);
        pixelGrid.setNumColumns(50);
        pixelGrid.setNumRows(100);

        //LayoutParams params = new LayoutParams(1000, 1400);

        //setContentView(R.layout.activity_temp_canvas);
        setContentView(pixelGrid);
        //addContentView(pixelGrid, params);
    }

}
