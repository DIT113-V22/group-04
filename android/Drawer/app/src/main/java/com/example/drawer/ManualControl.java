package com.example.drawer;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MQTTController.init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);
        innerCircle = findViewById(R.id.innerCircle);

        status = findViewById(R.id.statusText);
        MQTTController.update(status, "/smartcar/control/throttle");

        readMeScreen = findViewById(R.id.ReadMEScreen);
        manualControlScreen = findViewById(R.id.ManualScreen);
        drawControlScreen = findViewById(R.id.DrawScreen);
        speedStat = findViewById(R.id.speedStat);
        angleStat = findViewById(R.id.angleSTat);
        innerCircle = findViewById(R.id.innerCircle);
        outerCircle = findViewById(R.id.outerCircle);

        readMeScreen.setOnClickListener(view -> openReadMEScreen());

        manualControlScreen.setOnClickListener(view -> openManualScreen());

        drawControlScreen.setOnClickListener(view -> openDrawScreen());

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

        outerRadius = OC.getMinimumWidth() / 2;
        innerRadius = IC.getMinimumWidth() / 2;

        int traversX = (int)event.getRawX() - 90;
        int traversY = (int)event.getRawY() - 90;

        double angle;
        int startX = centerScrX;
        int startY = centerScrY;
        angle = (Math.toDegrees(Math.atan2((event.getRawY() - startY),(event.getRawX() - startX)) * -1));

        Pair<Integer, Integer> screenDimension = getScreenDimensions();

        centerScrX = (int) ((int)screenDimension.first / 2);
        centerScrY = (int) ((int)screenDimension.second / 1.25);

        float centerX = (int) (centerScrX - 90);
        float centerY = (int) (centerScrY - 90);

        double joystickToPressedDistance = Math.sqrt(
                Math.pow(centerScrX - event.getRawX(), 2) +
                Math.pow(centerScrY - event.getRawY(), 2)
        );

        //thumbstick clipping
        if (joystickToPressedDistance > outerRadius) {
            innerCircle.setX(centerX + (float) Math.cos(Math.toRadians(angle)) * outerRadius);
            innerCircle.setY(centerY + (float) Math.sin(Math.toRadians(angle)) * outerRadius * -1);
        } else {
            innerCircle.setX(traversX);
            innerCircle.setY(traversY);
        }

        outerCircle.setX(centerX-outerRadius);
        outerCircle.setY(centerY-outerRadius);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            innerCircle.setX(centerX);
            innerCircle.setY(centerY);
        }


        int carSpeed = carSpeed(event);
        double carAngle = carAngle(event);

        MQTTController.publish("/smartcar/control/throttle", String.valueOf(carSpeed));
        MQTTController.publish("/smartcar/control/steering", String.valueOf(carAngle));

    }

    public int carSpeed(MotionEvent event) {
        int speedTempX;
        int speedTempY;

        speedTempX = (int) event.getRawX() - centerScrX;
        speedTempY = (int) event.getRawY() - centerScrY;

        if (speedTempX < 0 ) {
            speedTempX *= -1;
        }
        if (speedTempY < 0) {
            speedTempY *= -1;
        }

        int speedTemp = (int) Math.sqrt((speedTempX*speedTempX)+(speedTempY*speedTempY));

        int startTemp = (int) Math.sqrt((centerScrX*centerScrX)+(centerScrY*centerScrY));

        if (speedTemp > startTemp) speedTemp = startTemp;

        if (speedTemp > outerRadius) speedTemp = outerRadius;

        int speedProc = (speedTemp * 100) / outerRadius;

        if (event.getRawY() > centerScrY) speedProc = speedProc * -1;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            speedProc = 0;
        }
        speedStat.setText("The speed percentage: " + speedProc);

        return speedProc;
    }

    public double carAngle(MotionEvent event) {
        double angle;
        int startX = centerScrX;
        int startY = centerScrY;
        angle = (Math.toDegrees(Math.atan2((event.getRawX() - startX),(event.getRawY() - startY)*-1) ));
        //angle = 180-angle;
        if (angle >= 90){
            angle = 180 - angle;
        }else if(angle <= -90){
            angle = -180 - angle;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) angle = 0;
        angleStat.setText("The angle is: " + angle);

        return angle;
    }

    public Pair<Integer, Integer> getScreenDimensions() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        return new Pair<>(width, height);
    }

    public void openReadMEScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openManualScreen() {
        Intent intent = new Intent(this, ManualControl.class);
        startActivity(intent);
    }

    public void openDrawScreen() {
        Intent intent = new Intent(this, DrawControl.class);
        startActivity(intent);
    }
}