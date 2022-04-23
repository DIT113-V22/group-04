package com.example.drawer;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

public class ManualControl extends AppCompatActivity {
    private Button readMeScreen;
    private Button manualControlScreen;
    private Button drawControlScreen;
    private View innerCircle;
    private View outerCircle;
    private int outerRadius = 0;
    private int innerRadius = 0;
    private int centerScrX = 0;
    private int centerScrY = 0;
    private TextView speedStat;
    private TextView angleStat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);


        readMeScreen = findViewById(R.id.ReadMEScreen);
        manualControlScreen = findViewById(R.id.ManualScreen);
        drawControlScreen = findViewById(R.id.DrawScreen);
        speedStat = findViewById(R.id.speedStat);
        angleStat = findViewById(R.id.angleSTat);
        innerCircle = findViewById(R.id.innerCircle);
        outerCircle = findViewById(R.id.outerCircle);



        readMeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReadMEScreen();
            }
        });

        manualControlScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openManualScreen();
            }
        });

        drawControlScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawScreen();
            }
        });

        innerCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                circleOnTouch(motionEvent);

                return false;
            }
        });


        outerCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                circleOnTouch(motionEvent);

                return false;
            }

        });

    }

    public void circleOnTouch(MotionEvent event){

        Drawable OC, IC;
        Resources res = getResources();
        OC = ResourcesCompat.getDrawable(res, R.drawable.outer_circle, null);
        IC = ResourcesCompat.getDrawable(res, R.drawable.inner_circle, null);

        outerRadius = OC.getMinimumWidth()/2;
        innerRadius = IC.getMinimumWidth()/2;

        int traversX = (int)event.getRawX()-90;
        int traversY = (int)event.getRawY()-90;

        Pair screenDimension = getScreenDimensions();

        centerScrX = (int) ((int)screenDimension.first/2);
        centerScrY = (int) ((int)screenDimension.second/1.25);

        int centerX = (int) (centerScrX -90);
        int centerY = (int) (centerScrY -90);



        if(traversX > (centerX + outerRadius)){
            innerCircle.setX(centerX + outerRadius);
        }else if(traversX < (centerX - outerRadius)){
            innerCircle.setX(centerX - outerRadius);
        }else{
            innerCircle.setX(traversX);
        }

        if(traversY > (centerY + outerRadius)){
            innerCircle.setY(centerY + outerRadius);
        }else if(traversY < (centerY - outerRadius)){
            innerCircle.setY(centerY - outerRadius);
        }else{
            innerCircle.setY(traversY);
        }


        outerCircle.setX(centerX-outerRadius);
        outerCircle.setY(centerY-outerRadius);


        if(event.getAction() == MotionEvent.ACTION_UP){
            innerCircle.setX(centerX);
            innerCircle.setY(centerY);
        }

        carSpeed(event);
        carAngle(event);

    }

    public void carSpeed(MotionEvent event){

        int speedTempX;
        int speedTempY;


        speedTempX = (int) event.getRawX()-centerScrX;
        speedTempY = (int) event.getRawY()-centerScrY;

        if(speedTempX < 0 ){
            speedTempX *= -1 ;
        }
        if (speedTempY < 0){
            speedTempY *= -1;
        }

        int speedTemp = (int) Math.sqrt((speedTempX*speedTempX)+(speedTempY*speedTempY));

        int startTemp = (int) Math.sqrt((centerScrX*centerScrX)+(centerScrY*centerScrY));

        if (speedTemp > startTemp) speedTemp = startTemp;

        if (speedTemp > outerRadius) speedTemp = outerRadius;

        int speedProc = (speedTemp * 100)/outerRadius;


        if(event.getAction() == MotionEvent.ACTION_UP){
            speedProc = 0;
        }
        speedStat.setText("The speed percentage: " + speedProc );
    }


    public void carAngle(MotionEvent event){
        double angle;
        int startX = centerScrX ;
        int startY = centerScrY ;
        angle = (Math.toDegrees(Math.atan2((event.getRawY()-startY),(event.getRawX()-startX))*-1));
        if(event.getAction() == MotionEvent.ACTION_UP) angle = 0;

        angleStat.setText("The angle is: "+ angle);
    }


    public Pair getScreenDimensions(){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        return new Pair(width, height);
    }


    public void openReadMEScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openManualScreen(){
        Intent intent = new Intent(this, ManualControl.class);
        startActivity(intent);
    }

    public void openDrawScreen(){
        Intent intent = new Intent(this, DrawControl.class);
        startActivity(intent);
    }

}
