package com.example.drawer;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private Button pubBtn;
    private Button disBtn;
    private Button conBtn;
    private Button subBtn;
    private Button readMeScreen;
    private Button manualControlScreen;
    private Button drawControlScreen;
    public TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MQTTController.init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conBtn = findViewById(R.id.conBtn);
        conBtn.setOnClickListener(view -> MQTTController.connect());

        pubBtn = findViewById(R.id.pubBtn);
        pubBtn.setOnClickListener(view -> MQTTController.publish("/smartcar/control/throttle", "50"));

        disBtn = findViewById(R.id.disBtn);
        disBtn.setOnClickListener(view -> MQTTController.disconnect());

        subBtn = findViewById(R.id.subBtn);
        subBtn.setOnClickListener(view -> MQTTController.subscribe("/smartcar/control/throttle"));
        
        text = findViewById(R.id.currentDistanceText);
        MQTTController.update(text, "/smartcar/ultrasound/front");

        readMeScreen = findViewById(R.id.ReadMeScreen);
        manualControlScreen = findViewById(R.id.ManualScreen);
        drawControlScreen = findViewById(R.id.DrawScreen);

        readMeScreen.setOnClickListener(view -> openReadMEScreen());

        manualControlScreen.setOnClickListener(view -> openManualScreen());

        drawControlScreen.setOnClickListener(view -> openDrawScreen());
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