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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

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
    private Button viewPaths;
    private Button playPath;
    private Button stopPlay;
    private Switch recordToggle;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ListView pathView;
    private Queue newRecording = new Queue() {
        @Override
        public boolean add(Object o) {
            return false;
        }

        @Override
        public boolean offer(Object o) {
            return false;
        }

        @Override
        public Object remove() {
            return null;
        }

        @Nullable
        @Override
        public Object poll() {
            return null;
        }

        @Override
        public Object element() {
            return null;
        }

        @Nullable
        @Override
        public Object peek() {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(@Nullable Object o) {
            return false;
        }

        @NonNull
        @Override
        public Iterator iterator() {
            return null;
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @NonNull
        @Override
        public Object[] toArray(@NonNull Object[] objects) {
            return new Object[0];
        }

        @Override
        public boolean remove(@Nullable Object o) {
            return false;
        }

        @Override
        public boolean containsAll(@NonNull Collection collection) {
            return false;
        }

        @Override
        public boolean addAll(@NonNull Collection collection) {
            return false;
        }

        @Override
        public boolean removeAll(@NonNull Collection collection) {
            return false;
        }

        @Override
        public boolean retainAll(@NonNull Collection collection) {
            return false;
        }

        @Override
        public void clear() {

        }
    };
    private List savedPathList = new ArrayList();
    private int carSpeed =0;
    private double carAngle = 0.0;
    Pair<Integer, Double> carStatus;

    MQTTController mqttController = MQTTController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);
        innerCircle = findViewById(R.id.innerCircle);

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


        recordToggle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                recordMovements(motionEvent);
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

        pathView = (ListView) popUpView.findViewById(R.id.pathList);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, savedPathList);
        pathView.setAdapter(arrayAdapter);
        onListItemClick(pathView, popUpView, 1, 1000027);

        pathView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO fix the toast so it shows the current playing recording
                Toast.makeText(getApplicationContext(), "Playing recording" + pathView.getChildAt(0), Toast.LENGTH_SHORT).show();
                do {
                    System.out.println(newRecording.poll());
                }while(!(newRecording.isEmpty()));
//                for(int j = 0; j > savedPathList.size(); j++){
//                    mqttController.publish("/smartcar/control/throttle", String.valueOf(savedPathList.get(j)));
//                    mqttController.publish("/smartcar/control/steering", String.valueOf(savedPathList.get(j)));
//                }
            }
        });

    }

        public void onListItemClick(ListView pathList, View v, int position, long id){

            //Set background of all items to white
            for (int i=0;i<pathList.getChildCount();i++){
                pathList.getChildAt(i).setBackgroundColor(Color.BLACK);
            }

            v.setBackgroundColor(Color.WHITE);
        }

    public void circleOnTouch(MotionEvent event) {
        Drawable outerCircle;
        Drawable innerCircle;
        Resources res = getResources();
        outerCircle = ResourcesCompat.getDrawable(res, R.drawable.outer_circle, null);
        innerCircle = ResourcesCompat.getDrawable(res, R.drawable.inner_circle, null);

        outerRadius = outerCircle.getMinimumWidth() / 2;
        innerRadius = innerCircle.getMinimumWidth() / 2;

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
                Math.pow(centerScrX - event.getRawX(), 2)
                        + Math.pow(centerScrY - event.getRawY(), 2)
        );

        //thumbstick clipping
        if (joystickToPressedDistance > outerRadius) {
            this.innerCircle.setX(centerX + (float) Math.cos(Math.toRadians(angle)) * outerRadius);
            this.innerCircle.setY(centerY + (float) Math.sin(Math.toRadians(angle)) * outerRadius * -1);
        } else {
            this.innerCircle.setX(traversX);
            this.innerCircle.setY(traversY);
        }

        this.outerCircle.setX(centerX - outerRadius);
        this.outerCircle.setY(centerY - outerRadius);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            this.innerCircle.setX(centerX);
            this.innerCircle.setY(centerY);
        }


        carSpeed = carSpeed(event);
        carAngle = carAngle(event);

        mqttController.publish("/smartcar/control/throttle", String.valueOf(carSpeed));
        mqttController.publish("/smartcar/control/steering", String.valueOf(carAngle));

        recordMovements(event);
    }

    public int carSpeed(MotionEvent event) {
        int speedTempX;
        int speedTempY;

        speedTempX = (int) event.getRawX() - centerScrX;
        speedTempY = (int) event.getRawY() - centerScrY;

        if (speedTempX < 0) {
            speedTempX *= -1;
        }
        if (speedTempY < 0) {
            speedTempY *= -1;
        }

        int speedTemp = (int) Math.sqrt((speedTempX * speedTempX) + (speedTempY * speedTempY));

        int startTemp = (int) Math.sqrt((centerScrX * centerScrX) + (centerScrY * centerScrY));

        if (speedTemp > startTemp) {
            speedTemp = startTemp;
        }

        if (speedTemp > outerRadius) {
            speedTemp = outerRadius;
        }

        int speedProc = (speedTemp * 100) / outerRadius;

        if (event.getRawY() > centerScrY) {
            speedProc = speedProc * -1;
        }

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
        angle = (Math.toDegrees(Math.atan2((event.getRawX() - startX), (event.getRawY() - startY) * -1)));
        //angle = 180-angle;
        if (angle >= 90) {
            angle = 180 - angle;
        } else if (angle <= -90) {
            angle = -180 - angle;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            angle = 0;
        }
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

    public boolean recordMovements(MotionEvent event){
        if (recordToggle.isChecked()) {
            // Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_SHORT).show();
            do{
                carStatus = new Pair(carSpeed(event), carAngle(event));
                //TODO make this recurring so the every publish message is added
                newRecording.add(carStatus);
                System.out.println(newRecording.poll());
            }while(recordToggle.equals(true));

        }
        else{
            Toast.makeText(getApplicationContext(), "Recording saved", Toast.LENGTH_SHORT).show();
            savedPathList.add(newRecording);
        }

        return false;
    }

}
