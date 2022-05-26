package com.example.drawer;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ManualControl extends AppCompatActivity {

    private Button mainScreenButton;
    private Button manualControlScreenButton;
    private Button drawControlScreenButton;
  
    //Used global variables
    private int outerRadius = 0;
    private boolean saved = false;
    private int centerX = 0;
    private int centerY = 0;
    private long previousSaveTime = 0;
    private long currentSaveTime = 0;

    private boolean timerStart = false;
    private boolean wasChecked = false;

    //UI objects/views
    private Switch recordToggle; //switch to turn on and off the recordings
    private Chronometer time;
    private Chronometer executeTimer;
    private ListView pathView;
    private TextView innerCircle;
    private View outerCircle;
    private TextView speedStat;
    private TextView angleStat;
    private Button viewPaths;

    private DBManager dbManager;

    private final LinkedList<Integer> carSpeedQueue = new LinkedList<>();
    private final LinkedList<Integer> carAngleQueue = new LinkedList<>();
    private final LinkedList<Long> carTimerList = new LinkedList<>();

    //POP-UP for playing recordings
    private AlertDialog.Builder builderReplays;
    private AlertDialog.Builder playRecordings;
    private AlertDialog replays;
    private AlertDialog playRec;

    //POP-UP for saving replay and name.
    private AlertDialog.Builder builderSaved;
    private AlertDialog alertDialogSaved;
    private EditText saveName;
    private Button saveReplay;
    private Button discardReplay;
    private Button deletePath;

    private Button playPath;
    private ImageView camera;
    private ArrayAdapter<String> arrayAdapter;
    private String myItem = "";
    private long lastTransmission = System.currentTimeMillis();

    MQTTController mqttController = MQTTController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);
        dbManager = new DBManager(this);
        //dbManager.deleteAll();
        innerCircle = findViewById(R.id.innerCircle);
        mqttController.publish("/smartcar/control/obstacle", "0");
        mqttController.publish("/smartcar/control/auto", "0");

        mainScreenButton = findViewById(R.id.ManualNavbarMain);
        manualControlScreenButton = findViewById(R.id.ManualNavbarManual);
        drawControlScreenButton = findViewById(R.id.ManualNavbarDraw);
        time = findViewById(R.id.stopWatch);
        executeTimer = findViewById(R.id.executeWatch);
        
        speedStat = findViewById(R.id.speedStat);
        angleStat = findViewById(R.id.angleStat);
        innerCircle = findViewById(R.id.innerCircle);
        outerCircle = findViewById(R.id.outerCircle);

        mainScreenButton.setOnClickListener(view -> openReadMEScreen());

        drawControlScreenButton.setOnClickListener(view -> openDrawScreen());
      
        viewPaths = findViewById(R.id.viewPathsScreen);
        recordToggle = findViewById(R.id.recordToggle);
        innerCircle.setEnabled(true);
        saveReplay = findViewById(R.id.saveRecording);
        camera = findViewById(R.id.camera);
        mqttController.updateCamera(camera);
        playPath = findViewById(R.id.playPath);

        testPoints();

        viewPaths.setOnClickListener(view -> {
            //open the pop up window
            createViewContactDialogueReplays();
        });

        outerCircle.setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            circleOnTouch(motionEvent);
            return false;
        });

        recordToggle.setOnCheckedChangeListener((compoundButton, buttonToggle) -> onRecordEnd(buttonToggle));
    }

    /**
     * On the end of recording stop timer and activate save pop-up.
     *
     * @param buttonToggle toggled on or off.
     */
    public void onRecordEnd(boolean buttonToggle) {
        if (!buttonToggle) {
            if (recordToggle.isChecked()) {
                wasChecked = true;
            } else if (wasChecked) {   // If toggle is off but was previously on
                endOfRecordingPopUpOptions();
                wasChecked = false;
                time.stop();
                time.setBase(SystemClock.elapsedRealtime());
                timerStart = false;
            }
        }
    }

    /**
     * Records commands by inserting movement instructions along with recording time into arraylists.
     *
     * @param speed individual speed command extracted from joystick.
     * @param angle individual angle command extracted from joystick.
     */
    public void recordMovements(int speed, int angle) {
        if (recordToggle.isChecked()) {
            currentSaveTime = System.currentTimeMillis();

            carSpeedQueue.add(speed);
            carAngleQueue.add(angle);

            //Changes time to be saved as milliseconds
            carTimerList.add(currentSaveTime - previousSaveTime);

            //Saves that the record was toggled
            wasChecked = true;
        }
    }

    /**
     * This method is used when the record toggle is turned off.
     * Allows the user to give name to the recorded path and a choice save or delete recording.
     * On save clicked, the details of path are stored in a database.
     * On discard clicked, the recording is discarded.
     */
    public void endOfRecordingPopUpOptions() {

        //Pop-Up setup
        builderSaved = new AlertDialog.Builder(this);
        final View popUpView = getLayoutInflater().inflate(R.layout.record_path_save, null);
        saveName = popUpView.findViewById(R.id.savedName);
        saveReplay = popUpView.findViewById(R.id.saveRecording);
        discardReplay = popUpView.findViewById(R.id.discardRecording);
        builderSaved.setView(popUpView);
        alertDialogSaved = builderSaved.create();
        alertDialogSaved.show();

        playRecordings = new AlertDialog.Builder(this);


        //Saves replay with the name.
        //stores the name of the reply and the respective array list to the database.
        saveReplay.setOnClickListener(view -> {
            alertDialogSaved.dismiss();
            String saveNameString = saveName.getText().toString();
            String carSpeedString = carSpeedQueue.toString();
            String carAngleString = carAngleQueue.toString();
            String carTimerString = carTimerList.toString();
            System.out.println(carSpeedString + carAngleString + saveNameString + carTimerString);
            dbManager.addNewPath(saveNameString, carSpeedString, carAngleString, carTimerString);
            carAngleQueue.clear();
            carTimerList.clear();
            carSpeedQueue.clear();
        });

        //Discards the replay
        discardReplay.setOnClickListener(view -> alertDialogSaved.dismiss());
    }

    /**
     * Pop-up of recordings which can be selected and played.
     * Selected played in separate thread.
     */
    public void createViewContactDialogueReplays() {

        //Pop-Up setup
        builderReplays = new AlertDialog.Builder(this);
        final View popUpView = getLayoutInflater().inflate(R.layout.activity_view_saved_paths, null);
        deletePath = popUpView.findViewById(R.id.deletePath); //stops the play and closes the window
        playPath = popUpView.findViewById(R.id.playPath);

        builderReplays.setView(popUpView);
        replays = builderReplays.create();
        replays.show();

        deletePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Gets the all the path information and path names stored in the database
        ArrayList<String> finalOutputList = dbManager.getAllPaths();

        //creates a pop up window which has a list view
        //in order to contain the array list of all the save paths
        pathView = popUpView.findViewById(R.id.pathList);
        pathView.setBackgroundColor(Color.parseColor("#111111"));
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalOutputList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.parseColor("#222222"));

                return view;
            }
        };

        pathView.setAdapter(arrayAdapter);

        //When any list item is click, the respective saved recording is played.
        pathView.setOnItemClickListener((adapterView, view, i, l) -> {
            //Execution of selected save with previously saved time and command. All in separate thread.
            //playRecordings.show();
            for (int j = 0; j < pathView.getChildCount(); j++) {
                pathView.getChildAt(j).setBackgroundColor(Color.parseColor("#222222"));
            }
            pathView.getChildAt(i).setBackgroundColor(Color.parseColor("#8685ef"));

            if (dbManager.getAllPathNames().contains(arrayAdapter.getItem(i))) {
                myItem = arrayAdapter.getItem(i);

                //System.out.println(dbManager.getPathDetails(myItem));
                ArrayList<Integer> itemCarSpeed = dbManager.getPathDetails(myItem);
                ArrayList<Integer> itemCarAngle = dbManager.getAngleDetails(myItem);
                ArrayList<Long> itemCarTimer = dbManager.getTimeDetails(myItem);

                ManualRecordingRun executeRecording = new ManualRecordingRun(itemCarTimer, itemCarAngle,
                        itemCarSpeed, mqttController, executeTimer);
                playPath.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(executeRecording).start();
                    }
                });
            }

            //delete the selected item
            deletePath.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbManager.deleteSpecific(myItem);
                    replays.dismiss();
                }
            });
        });
    }

    public <E> void delete(View v) {
        ListView listview1 = new ListView(this);
        ArrayList<E> datalist = new ArrayList<>();

        final int position = listview1.getPositionForView((View) v.getParent());
        datalist.remove(position);
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * The method sets a background color to all the list items.
     *
     * @param pathList Overarching listview for all recordings
     * @param v Pop up view that shows all the saved recordings
     * @author Sejal Kanaskar
     */
    public void onListItemClick(ListView pathList, View v) { //delete
        //Set background of all items to white
        for (int i = 0; i < pathList.getChildCount(); i++) {
            pathList.getChildAt(i).setBackgroundColor(Color.BLACK);
        }
        v.setBackgroundColor(Color.BLACK);
    }

    /**
     * When outerCircle is touched circleOnTouch is called.
     * Makes innerCircle follow the touch of user but clipping to outerCircle if drag outside.
     * Publishes speed and angle commands to car.
     *
     * @param event User touch/drag.
     * @author Burak Askan
     */
    public void circleOnTouch(MotionEvent event) {
        Drawable oc;
        Resources res = getResources();
        oc = ResourcesCompat.getDrawable(res, R.drawable.outer_circle, null);


        //Retrieves the starting position of the Drawable Views.
        if (!saved) {
            centerX = (int) innerCircle.getX();
            centerY = (int) innerCircle.getY();
            saved = true;
        }

        outerRadius = oc.getMinimumWidth() / 2;

        //Gets actual position of travers within users touch.
        int traversX = (int) (event.getX() + centerX - 90);
        int traversY = (int) (event.getY() + centerY - 90);

        traversX = traversX - outerRadius;
        traversY = traversY - outerRadius;
        double angle;
        angle = (Math.toDegrees(Math.atan2(((event.getY() - 90) - outerRadius),
                ((event.getX() - 90) - outerRadius)) * -1));


        //Sets up clipping and actual moving of innerCircle to touch position.
        double joystickToPressedDistance = Math.sqrt(
            Math.pow(centerX - traversX, 2) + Math.pow(centerY - traversY, 2)
        );

        //thumb-stick clipping
        if (joystickToPressedDistance > outerRadius) {
            innerCircle.setX(centerX + (float) Math.cos(Math.toRadians(angle)) * outerRadius);
            innerCircle.setY(centerY + (float) Math.sin(Math.toRadians(angle)) * outerRadius * -1);
        } else {
            innerCircle.setX(traversX);
            innerCircle.setY(traversY);
        }

        outerCircle.setX(centerX - outerRadius);
        outerCircle.setY(centerY - outerRadius);

        //Retrieves calculated speed and angle values
        int carSpeed = carSpeed(event);
        double carAngle = carAngle(event);


        //Starts timer if recording toggle is activated
        if (recordToggle.isChecked()) {
            if (!timerStart) {
                time.setBase(SystemClock.elapsedRealtime());
                time.start();
                timerStart = true;
                previousSaveTime = System.currentTimeMillis();
            }
        }

        if (carAngle < 3 && carAngle > -3) {
            carAngle = 0;
        }

        if (((System.currentTimeMillis() - lastTransmission) > 10) || (carAngle == 0 || carSpeed == 0)) {
            //Publishes the car speed respective to the joystick position.
            mqttController.publish("/smartcar/control/throttle", String.valueOf(carSpeed));
            //Publishes the car angle respective to the joystick position.
            mqttController.publish("/smartcar/control/steering", String.valueOf(carAngle));
            lastTransmission = System.currentTimeMillis();

            //Records and saves the car speed and angle.
            recordMovements(carSpeed, (int)carAngle);
        }

        //Clips innerCircle back to the center of outerCircle after user lets go of touch.
        if (event.getAction() == MotionEvent.ACTION_UP) {
            innerCircle.setX(centerX);
            innerCircle.setY(centerY);
        }
    }

    /**
     * Calculate speed percentage based on joystick position.
     *
     * @param event User touch/drag.
     * @return calculated speed.
     */
    public int carSpeed(MotionEvent event) {
        int traversX = (int) (event.getX() + centerX - 90 - outerRadius);
        int traversY = (int) (event.getY() + centerY - 90 - outerRadius);

        //Touch positions from the center position of outerCircle
        int speedTempX = (int) traversX - centerX;
        int speedTempY = (int) traversY - centerY;

        //Turn all negative numbers to positives
        if (speedTempX < 0) {
            speedTempX *= -1;
        }
        if (speedTempY < 0) {
            speedTempY *= -1;
        }

        //Touch distance from the center of outerCircle
        int speedTemp = (int) Math.sqrt((speedTempX * speedTempX) + (speedTempY * speedTempY));

        int startTemp = (int) Math.sqrt((centerX * centerX) + (centerY * centerY));

        if (speedTemp > startTemp) {
            speedTemp = startTemp;
        }
        if (speedTemp > outerRadius) {
            speedTemp = outerRadius;
        }

        int speedProc = (speedTemp * 100) / outerRadius;

        if (traversY > centerY) {
            speedProc = speedProc * -1;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            speedProc = 0;
        }
        speedStat.setText("The speed percentage: " + speedProc);

        return speedProc;
    }

    /**
     * Calculate angle based on joystick position.
     *
     * @param event User touch/drag.
     * @return calculated angle.
     */
    public int carAngle(MotionEvent event) {
        int angle;

        //Math to get degrees based on a circle in a position.
        angle = (int) (Math.toDegrees(Math.atan2((event.getX() - 90 - outerRadius),
                (event.getY() - 90 - outerRadius) * -1)));

        //Switching where degrees are located where.
        if (angle >= 90) {
            angle = 180 - angle;
        } else if (angle <= -90) {
            angle = -180 - angle;
        }

        //Set angle to 0 if there is no touch.
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
        Log.d("rrr", height + " " + width);

        return new Pair<>(width, height);
    }

    /**
     * Opens ReadME  screen (home screen).
     */
    public void openReadMEScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Opens draw control screen.
     */
    public void openDrawScreen() {
        Intent intent = new Intent(this, DrawControl.class);
        startActivity(intent);
    }

    public void testPoints() {
        Queue<Point> pointQueue = new LinkedList<>();
        pointQueue.add(new Point(4,3));
        pointQueue.add(new Point(2,8));
        pointQueue.add(new Point(6,0));
        pointQueue.add(new Point(7,2));
        pointQueue.add(new Point(9,5));
        pointQueue.add(new Point(1,2));


        String pointQueueString = dbManager.pointToString(pointQueue);
        System.out.println(pointQueueString);
        Queue<Point> pointQueueNew = new LinkedList<>();
        pointQueueNew = dbManager.stringToPoint(pointQueueString);
        System.out.println(pointQueueNew);
    }
}
