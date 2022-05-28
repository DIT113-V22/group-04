package com.example.drawer;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.LinkedList;

public class ManualControl extends AppCompatActivity {

    //Used global variables
    private int outerRadius = 0;
    private boolean saved = false;
    private int centerX = 0;
    private int centerY = 0;

    private boolean timerStart = false;
    private boolean wasChecked = false;

    private Switch recordToggle;
    private Chronometer time;
    private Chronometer executeTimer;
    private ListView pathView;
    private Button readMeScreen;
    private Button manualControlScreen;
    private Button drawControlScreen;
    private TextView innerCircle;
    private View outerCircle;
    private TextView speedStat;
    private TextView angleStat;
    private TextView status;

    private Button viewPaths;

    private DBManager dbManager;

    private final LinkedList<Integer> carSpeedQueue = new LinkedList<>();
    private final LinkedList<Integer> carAngleQueue = new LinkedList<>();
    private final LinkedList<Integer> carTimerList = new LinkedList<>();

    private AlertDialog.Builder builderReplays;
    private AlertDialog.Builder playRecordings;
    private AlertDialog replays;
    private AlertDialog playRec;

    private AlertDialog.Builder builderSaved;
    private AlertDialog alertDialogSaved;
    private EditText saveName;
    private Button saveReplay;
    private Button discardReplay;
    private Button deletePath;
    private Button playPath;
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
        status = findViewById(R.id.statusText);
        mqttController.updateTextView(status, "/smartcar/control/throttle");
        time = findViewById(R.id.stopWatch);
        executeTimer = findViewById(R.id.executeWatch);
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
        saveReplay = findViewById(R.id.saveRecording);

        playPath = findViewById(R.id.playPath);
        readMeScreen.setOnClickListener(view -> openReadMEScreen());
        drawControlScreen.setOnClickListener(view -> openDrawScreen());

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
//                carStatus = new Pair(carSpeedQueue, carAngleQueue);
//                savedPathList.add(carSpeedQueue);
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
            //IS THIS NEEDED?(START)
            if (!timerStart) {
                time.setBase(SystemClock.elapsedRealtime());
                time.start();
                timerStart = true;
            }
            //IS THIS NEEDED?(END)

            carSpeedQueue.add(speed);
            carAngleQueue.add(angle);

            //Changes time to be saved as milliseconds
            carTimerList.add((int) (SystemClock.elapsedRealtime() - time.getBase()));
            //String timerJson = gson.toJson(carTimerList);
            //dbManager.addNewTimer(timerJson);

            //Saves that the record was toggled
            wasChecked = true;

        } else {
            //IS THIS NEEDED?(START)
            time.stop();
            time.setBase(SystemClock.elapsedRealtime());
            timerStart = false;
            //IS THIS NEEDED?(END)
        }
    }

    /**
     * End of recording pop-up. Allows user to give name and save or delete recording.
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
//            try {
//                JSONObject jsonObject = new JSONObject(carSpeedString);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        });

        //Discards the replay
        discardReplay.setOnClickListener(view -> alertDialogSaved.dismiss());
    }

    /**
     * Pop-up of recordings which can be selected and played.
     * Selected played in separate thread.
     *
     * @author Sejal Kanaskar
     */
    public void createViewContactDialogueReplays() {

        //Pop-Up setup
        builderReplays = new AlertDialog.Builder(this);
        final View popUpView = getLayoutInflater().inflate(R.layout.activity_view_saved_paths, null);
        deletePath = popUpView.findViewById(R.id.deletePath); //stops the play and closes the window
        builderReplays.setView(popUpView);
        replays = builderReplays.create();
        replays.show();

        playRecordings = new AlertDialog.Builder(this);
        //final View popUpView2 = getLayoutInflater().inflate(R.layout.activity_play_recording, null);
        //playRecordings.setView(popUpView2);
        playRec = playRecordings.create();


        //Deletes that recording
        deletePath.setOnClickListener(view -> {
            if (myItem.equals("")) {
                //toast
            } else {
                delete(view);
            }
        });

//        playPath.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                delete(view);
//            }
//        });

        //Gets the all the path information and path names stored in the database
        ArrayList<String> finalOutputList = dbManager.getAllPaths();

        //creates a pop up window which has a list view
        //in order to contain the array list of all the save paths
        pathView = popUpView.findViewById(R.id.pathList);
        pathView.setBackgroundColor(Color.parseColor("#c8c8c8"));
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, finalOutputList);

        pathView.setAdapter(arrayAdapter);
        onListItemClick(pathView, popUpView); // delete

        //When any list item is click, the respective saved recording is played.
        pathView.setOnItemClickListener((adapterView, view, i, l) -> {
            //Execution of selected save with previously saved time and command. All in separate thread.
            playRecordings.show();

            //System.out.println(arrayAdapter.getItem(i));
            if (dbManager.getAllPathNames().contains(arrayAdapter.getItem(i))) {
                myItem = arrayAdapter.getItem(i);

                //System.out.println(dbManager.getPathDetails(myItem));

                ArrayList<Integer> itemCarSpeed = dbManager.getPathDetails(myItem);
                ArrayList<Integer> itemCarAngle = dbManager.getAngleDetails(myItem);
                ArrayList<Integer> itemCarTimer = dbManager.getTimeDetails(myItem);

                ManualRecordingRun executeRecording = new ManualRecordingRun(itemCarTimer, itemCarAngle, itemCarSpeed, mqttController, executeTimer);
                new Thread(executeRecording).start();
                replays.dismiss();
            }
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
     * @param pathList
     * @param v
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
        Drawable OC;
        Resources res = getResources();
        OC = ResourcesCompat.getDrawable(res, R.drawable.outer_circle, null);

        //Retrieves the starting position of the Drawable Views.
        if (!saved) {
            centerX = (int) innerCircle.getX();
            centerY = (int) innerCircle.getY();
            saved = true;
        }

        outerRadius = OC.getMinimumWidth() / 2;

        //Gets actual position of travers within users touch.
        int traversX = (int) (event.getX() + centerX - 90);
        int traversY = (int) (event.getY() + centerY - 90);

        traversX = traversX - outerRadius;
        traversY = traversY - outerRadius;
        double angle;
        angle = (Math.toDegrees(Math.atan2(((event.getY() - 90) - outerRadius), ((event.getX() - 90) - outerRadius)) * -1));


        //Sets up clipping and actual moving of innerCircle to touch position.
        double joystickToPressedDistance = Math.sqrt(
                Math.pow(centerX - traversX, 2) +
                        Math.pow(centerY - traversY, 2)
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
        int carAngle = carAngle(event);

        //Starts timer as soon as recording toggle is turned on.
        if (recordToggle.isChecked()) {
            if (!timerStart) {
                time.setBase(SystemClock.elapsedRealtime());
                time.start();
                timerStart = true;
            }
        }

        if (carAngle < 5 && carAngle > -5) {
            carAngle = 0;
        }

        if ((System.currentTimeMillis() - lastTransmission) > 10) {
            //Publishes the car speed respective to the joystick position.
            mqttController.publish("/smartcar/control/throttle", String.valueOf(carSpeed));
            //Publishes the car angle respective to the joystick position.
            mqttController.publish("/smartcar/control/steering", String.valueOf(carAngle));
            lastTransmission = System.currentTimeMillis();
        }

        //Records and saves the car speed and angle.
        recordMovements(carSpeed, carAngle);

        //Clips innerCircle back to the center of outerCircle after user lets go of touch.
        if (event.getAction() == MotionEvent.ACTION_UP) {
            innerCircle.setX(centerX);
            innerCircle.setY(centerY);
        }
    }

    /**
     * Calculates speed percentage based on the joystick position.
     * The methods returns the speed of the car.
     *
     * @param event User touch/drag.
     * @return calculated speed.
     */
    public int carSpeed(MotionEvent event) {
        int speedTempX;
        int speedTempY;
        int traversX = (int) (event.getX() + centerX - 90);
        int traversY = (int) (event.getY() + centerY - 90);

        traversX = traversX - outerRadius;
        traversY = traversY - outerRadius;

        //Touch positions from the center position of outerCircle
        speedTempX = (int) traversX - centerX;
        speedTempY = (int) traversY - centerY;

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
     * Calculates angle of the car based on joystick movement.
     * The method returns the angle in degrees.
     *
     * @param event User touch/drag.
     * @return calculated angle.
     */
    public int carAngle(MotionEvent event) {
        int angle;

        //Math to get degrees based on a circle in a position.
        angle = (int) (Math.toDegrees(Math.atan2((event.getX() - 90 - outerRadius), (event.getY() - 90 - outerRadius) * -1)));

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
}
