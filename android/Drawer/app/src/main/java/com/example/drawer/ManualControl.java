package com.example.drawer;

import static java.lang.Thread.sleep;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.MalformedJsonException;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ManualControl extends AppCompatActivity {

    //Used global variables
    private int outerRadius = 0;
    private boolean saved = false;
    private int centerX = 0;
    private int centerY = 0;

    private boolean timerStart = false;
    private boolean wasChecked = false;

    //UI objects/views
    private Switch recordToggle; //switch to turn on and off the recordings
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

    private Gson gson = new Gson();
    private DBManager dbManager;
    private final String TAG = "myActivity";

    //Lists for saved recordings
    private LinkedList carSpeedQueue = new LinkedList();
    private List savedPathListName = new ArrayList(); //for setting a name to the path
    private ArrayList savedPathList = new ArrayList(); //the actual saved path
    private LinkedList carAngleQueue = new LinkedList();
    private LinkedList carTimerQueue= new LinkedList();
    private Pair<Integer, Double> carStatus;

    //POP-UP for playing recordings
    private AlertDialog.Builder builderReplays;
    private AlertDialog alertDialogReplays;
    private Button stopPlay;

    //POP-UP for saving replay and name.
    private AlertDialog.Builder builderSaved;
    private AlertDialog alertDialogSaved;
    private EditText saveName;
    private Button saveReplay;
    private Button discardReplay;

    MQTTController mqttController = MQTTController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_control);
        dbManager = new DBManager(this);

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

        readMeScreen.setOnClickListener(view -> openReadMEScreen());

        manualControlScreen.setOnClickListener(view -> openManualScreen());

        drawControlScreen.setOnClickListener(view -> openDrawScreen());

        viewPaths.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open the pop up window
                createViewContactDialogueReplays();
            }
        });

        outerCircle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                circleOnTouch(motionEvent);
                return false;
            }
        });

        recordToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onRecordEnd(b);
            }
        });
    }

    /**
     * On the end of recording stop timer and activate save pop-up.
     *
     * @param b toggled on or off.
     */
    public void onRecordEnd(boolean b){

        if (!b){
            if (recordToggle.isChecked()) {

                wasChecked = true;
            }else if(wasChecked){   // If toggle is off but was previously on
                carStatus = new Pair(carSpeedQueue, carAngleQueue);
                savedPathList.add(carStatus);
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
    public void recordMovements(int speed, int angle){
        if (recordToggle.isChecked()) {
            //IS THIS NEEDED?(START)
            if(!timerStart){
                time.setBase(SystemClock.elapsedRealtime());
                time.start();
                timerStart = true;
            }
            //IS THIS NEEDED?(END)

            carSpeedQueue.add(speed);
            carAngleQueue.add(angle);

            //Changes time to be saved as milliseconds
            carTimerQueue.add((int) (SystemClock.elapsedRealtime() - time.getBase()));

            //Saves that the record was toggled
            wasChecked = true;

        }else{
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

        //Saves replay with the name.
        // stores the name of the reply and the respective array list to the database.
        saveReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedPathListName.add(saveName.getText());
                alertDialogSaved.dismiss();
                String saveNameString = saveName.getText().toString();
                String json = gson.toJson(savedPathList);
                System.out.println(json + saveNameString);
                dbManager.addNewPath(saveNameString, json);
            }
        });

        //Discards the replay
        discardReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedPathList.remove(savedPathList.size() - 1);
                alertDialogSaved.dismiss();
            }
        });
    }

    //Type type = new TypeToken<ArrayList<String>>() {}.getType();
    //
    //ArrayList<String> finalOutputString = gson.fromJson(outputarray, type);

    /**
     * Pop-up of recordings which can be selected and played.
     * Selected played in separate thread.
     */
    public void createViewContactDialogueReplays() {
        //Pop-Up setup
        builderReplays = new AlertDialog.Builder(this);
        final View popUpView = getLayoutInflater().inflate(R.layout.activity_view_saved_paths, null);
        stopPlay = (Button) popUpView.findViewById(R.id.stopPlay); //stops the play and closes the window
        builderReplays.setView(popUpView);
        alertDialogReplays = builderReplays.create();
        alertDialogReplays.show();

        //Stops recording from playing
        stopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogReplays.dismiss();
            }
        });

        ArrayList<String> finalOutputList = dbManager.getAllCotacts();


        //Setup the list of saves on pop-up.
        pathView = (ListView) popUpView.findViewById(R.id.pathList);
        pathView.setBackgroundColor(Color.parseColor("#c8c8c8"));
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, finalOutputList);
        pathView.setAdapter(arrayAdapter);
        onListItemClick(pathView, popUpView, 1, 1000027);

        //Selection of a save
        pathView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Execution of selected save with previously saved time and command. All in separate thread.
                ManualRecordingRun executeRecording = new ManualRecordingRun(carTimerQueue, carSpeedQueue, carAngleQueue, mqttController, executeTimer);
                new Thread(executeRecording).start();
                alertDialogReplays.dismiss();
            }
        });
    }

    ///TODO: FILL IN DOCUMENTATION
    /**
     *
     * @param pathList
     * @param v
     * @param position
     * @param id
     */
    //Is int position and long id necessary if they are not being used?
    public void onListItemClick(ListView pathList, View v, int position, long id){

            //Set background of all items to white
            for (int i=0;i<pathList.getChildCount();i++){
                pathList.getChildAt(i).setBackgroundColor(Color.BLACK);
            }
            v.setBackgroundColor(Color.WHITE);
    }


    /**
     * When outerCircle is touched circleOnTouch is called.
     * Makes innerCircle follow the touch of user but clipping to outerCircle if drag outside.
     * Publishes speed and angle commands to car.
     *
     * @param event User touch/drag.
     */
    public void circleOnTouch(MotionEvent event) {
        Drawable OC;
        Resources res = getResources();
        OC = ResourcesCompat.getDrawable(res, R.drawable.outer_circle, null);

        //Retrieves the starting position of the Drawable Views.
        if(!saved){
            centerX = (int)innerCircle.getX();
            centerY = (int)innerCircle.getY();
            saved = true;
        }

        outerRadius = OC.getMinimumWidth() / 2;

        //Gets actual position of travers within users touch.
        int traversX = (int)(event.getX() + centerX - 90);
        int traversY = (int)(event.getY() + centerY - 90);

        traversX = traversX - outerRadius;
        traversY = traversY - outerRadius;


        double angle;

        angle = (Math.toDegrees(Math.atan2(((event.getY()-90) - outerRadius),((event.getX()-90) - outerRadius)) * -1));


        //Sets up clipping and actual moving of innerCircle to touch position.
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




        //Retrieves calculated speed and angle values
        int carSpeed = carSpeed(event);
        int carAngle = carAngle(event);


        //Starts timer if recording toggle is activated
        if (recordToggle.isChecked()) {
            if(!timerStart){
                time.setBase(SystemClock.elapsedRealtime());
                time.start();
                timerStart = true;
            }
        }

        //Publishes instructions of speed and angle to car
        mqttController.publish("/smartcar/control/throttle", String.valueOf(carSpeed));
        mqttController.publish("/smartcar/control/steering", String.valueOf(carAngle));

        //Records the movements.
        recordMovements(carSpeed, carAngle);

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
        int speedTempX;
        int speedTempY;
        int traversX = (int)(event.getX() + centerX - 90);
        int traversY = (int)(event.getY() + centerY - 90);

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

        //MORE MATH

        //Touch distance from the center of outerCircle
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


    /**
     * Calculate angle based on joystick position.
     *
     * @param event User touch/drag.
     * @return calculated angle.
     */
    public int carAngle(MotionEvent event) {
        int angle;

        //Math to get degrees based on a circle in a position.
        angle = (int)(Math.toDegrees(Math.atan2((event.getX()-90 - outerRadius), (event.getY()-90 - outerRadius) * -1)));

        //Switching where degrees are located where.
        if (angle >= 90){
            angle = 180 - angle;
        }else if(angle <= -90){
            angle = -180 - angle;
        }

        //Set angle to 0 if there is no touch.
        if (event.getAction() == MotionEvent.ACTION_UP) angle = 0;
        angleStat.setText("The angle is: " + angle);

        return angle;
    }


    /**
     * Sends to readMeScreen
     */
    public void openReadMEScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Sends to manualScreen
     */
    public void openManualScreen() {
        Intent intent = new Intent(this, ManualControl.class);
        startActivity(intent);
    }

    /**
     * sends to drawScreen
     */
    public void openDrawScreen() {
        Intent intent = new Intent(this, DrawControl.class);
        startActivity(intent);
    }
}
