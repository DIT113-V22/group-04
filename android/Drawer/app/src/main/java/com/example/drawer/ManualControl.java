package com.example.drawer;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

public class ManualControl extends AppCompatActivity {
    private Button readMeScreen;
    private Button manualControlScreen;
    private Button drawControlScreen;
    private TextView innerCircle;
    private View outerCircle;
    private int outerRadius = 0;
    private boolean saved = false;
    private int centerX = 0;
    private int centerY = 0;
    private TextView speedStat;
    private TextView angleStat;
    private TextView status;
    private Button viewPaths;
    private Button playPath;
    private Button stopPlay;
    private Switch recordToggle;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ListView pathList;

    MQTTController mqttController = MQTTController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);

        status = findViewById(R.id.statusText);
        mqttController.updateTextView(status, "/smartcar/control/throttle");

        readMeScreen = findViewById(R.id.ReadMEScreen);
        manualControlScreen = findViewById(R.id.ManualScreen);
        drawControlScreen = findViewById(R.id.DrawScreen);
        speedStat = findViewById(R.id.speedStat);
        angleStat = findViewById(R.id.angleSTat);
        innerCircle = findViewById(R.id.innerCircle);
        outerCircle = findViewById(R.id.outerCircle);
        viewPaths = findViewById(R.id.viewPathsScreen);
        recordToggle = findViewById(R.id.recordToggle);
        innerCircle.setEnabled(true);

        readMeScreen.setOnClickListener(view -> openReadMEScreen());

        manualControlScreen.setOnClickListener(view -> openManualScreen());

        drawControlScreen.setOnClickListener(view -> openDrawScreen());

        viewPaths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open the pop up window
                createViewContactDialogue();
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

    public void createViewContactDialogue() {
        builder = new AlertDialog.Builder(this);
        final View popUpView = getLayoutInflater().inflate(R.layout.activity_view_saved_paths, null);
        playPath = (Button) popUpView.findViewById(R.id.playPath);
        stopPlay = (Button) popUpView.findViewById(R.id.stopPlay);
        builder.setView(popUpView);
        alertDialog = builder.create();
        alertDialog.show();

        stopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        pathList = (ListView) popUpView.findViewById(R.id.pathList);
        List savedPathList = new ArrayList();
        savedPathList.add("Path 1");
        savedPathList.add("Path 2");
        savedPathList.add("Path 3");
        savedPathList.add("Path 4");
        savedPathList.add("Path 5");
        savedPathList.add("Path 6");
        savedPathList.add("Path 7");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, savedPathList);
        pathList.setAdapter(arrayAdapter);
        onListItemClick(pathList, popUpView, 1, 1000027);
    }

    public void onListItemClick(ListView pathList, View v, int position, long id){

        //Set background of all items to white
        for (int i=0;i<pathList.getChildCount();i++){
            pathList.getChildAt(i).setBackgroundColor(Color.BLACK);
        }

        v.setBackgroundColor(Color.WHITE);
    }

    public void circleOnTouch(MotionEvent event){
        Drawable OC;
        Drawable IC;
        Resources res = getResources();
        OC = ResourcesCompat.getDrawable(res, R.drawable.outer_circle, null);
        IC = ResourcesCompat.getDrawable(res, R.drawable.inner_circle, null);
        if(!saved){
            centerX = (int)innerCircle.getX();
            centerY = (int)innerCircle.getY();
            saved = true;
        }

        outerRadius = OC.getMinimumWidth() / 2;

        int traversX = (int)(event.getX() + centerX - 90);
        int traversY = (int)(event.getY() + centerY - 90);

        traversX = traversX - outerRadius;
        traversY = traversY - outerRadius;


        double angle;

        angle = (Math.toDegrees(Math.atan2(((event.getY()-90) - outerRadius),((event.getX()-90) - outerRadius)) * -1));

        double joystickToPressedDistance = Math.sqrt(
                Math.pow(centerX - traversX, 2) +
                Math.pow(centerY - traversY, 2)
        );

        //thumbstick clipping
        if (joystickToPressedDistance > outerRadius) {
            innerCircle.setX(centerX + (float) Math.cos(Math.toRadians(angle)) * outerRadius);
            innerCircle.setY(centerY + (float) Math.sin(Math.toRadians(angle)) * outerRadius * -1);
        } else {
            innerCircle.setX(traversX);
            innerCircle.setY(traversY);
        }

        outerCircle.setX(centerX -outerRadius);
        outerCircle.setY(centerY -outerRadius);

        if (event.getAction() == MotionEvent.ACTION_UP) {

            innerCircle.setX(centerX);
            innerCircle.setY(centerY);
        }


        int carSpeed = carSpeed(event);
        double carAngle = carAngle(event);

        mqttController.publish("/smartcar/control/throttle", String.valueOf(carSpeed));
        mqttController.publish("/smartcar/control/steering", String.valueOf(carAngle));

    }

    public int carSpeed(MotionEvent event) {
        int speedTempX;
        int speedTempY;
        int traversX = (int)(event.getX() + centerX - 90);
        int traversY = (int)(event.getY() + centerY - 90);

        traversX = traversX - outerRadius;
        traversY = traversY - outerRadius;


        speedTempX = (int) traversX - centerX;
        speedTempY = (int) traversY - centerY;

        if (speedTempX < 0 ) {
            speedTempX *= -1;
        }
        if (speedTempY < 0) {
            speedTempY *= -1;
        }

        int speedTemp = (int) Math.sqrt((speedTempX*speedTempX)+(speedTempY*speedTempY));

        int startTemp = (int) Math.sqrt((centerX * centerX)+(centerY * centerY));

        if (speedTemp > startTemp) speedTemp = startTemp;

        if (speedTemp > outerRadius) speedTemp = outerRadius;

        int speedProc = (speedTemp * 100) / outerRadius;

        if (traversY > centerY) speedProc = speedProc * -1;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            speedProc = 0;
        }
        speedStat.setText("The speed percentage: " + speedProc);

        return speedProc;
    }

    public double carAngle(MotionEvent event) {
        double angle;
        angle = (Math.toDegrees(Math.atan2((event.getX()-90 - outerRadius), (event.getY()-90 - outerRadius) * -1)));
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
