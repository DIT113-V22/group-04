package com.example.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button pubBtn;
    private Button disBtn;
    private Button conBtn;
    private Button subBtn;
    private Button mainScreenButton;
    private Button manualControlScreenButton;
    private Button drawControlScreenButton;

    MQTTController mqttController = MQTTController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conBtn = findViewById(R.id.conBtn);
        conBtn.setOnClickListener(view -> mqttController.connect());

        pubBtn = findViewById(R.id.pubBtn);
        pubBtn.setOnClickListener(view ->
                mqttController.publish("/smartcar/control/throttle", "50"));

        disBtn = findViewById(R.id.disBtn);
        disBtn.setOnClickListener(view -> mqttController.disconnect());

        subBtn = findViewById(R.id.subBtn);
        subBtn.setOnClickListener(view -> {
            mqttController.subscribe("/smartcar/report/startup");
            mqttController.subscribe("/smartcar/report/status");
            mqttController.subscribe("/smartcar/report/camera");
            mqttController.subscribe("/smartcar/report/obstacle");
            mqttController.subscribe("/smartcar/report/ultrasound");
            mqttController.subscribe("/smartcar/report/odometer");
            mqttController.subscribe("/smartcar/control/throttle");
            mqttController.subscribe("/smartcar/odometer/destinationbool");
        });

        mainScreenButton = findViewById(R.id.MainNavbarMain);
        manualControlScreenButton = findViewById(R.id.MainNavbarManual);
        drawControlScreenButton = findViewById(R.id.MainNavbarDraw);

        mainScreenButton.setOnClickListener(view -> openReadMEScreen());
        manualControlScreenButton.setOnClickListener(view -> openManualScreen());
        drawControlScreenButton.setOnClickListener(view -> openDrawScreen());
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