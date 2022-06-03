package com.example.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button connectButton;
    private Button subscribeButton;
    private Button publishButton;
    private Button disconnectButton;
    private Button manualControlScreenButton;
    private Button drawControlScreenButton;
    private ImageView carImg;
    private ImageView smokeImg;

    MQTTController mqttController = MQTTController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connection button
        connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(view -> mqttController.connect());

        // Publish button
        publishButton = findViewById(R.id.publishButton);
        publishButton.setOnClickListener(view ->
                mqttController.publish("/smartcar/control/throttle", "50"));

        // Disconnect button
        disconnectButton = findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(view -> mqttController.disconnect());

        // Subscription button
        subscribeButton = findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(view -> {
            System.out.println("SUB");
            mqttController.subscribe("/smartcar/report/startup");
            mqttController.subscribe("/smartcar/report/status");
            mqttController.subscribe("/smartcar/report/camera");
            mqttController.subscribe("/smartcar/report/obstacle");
            mqttController.subscribe("/smartcar/report/ultrasound");
            mqttController.subscribe("/smartcar/report/odometer");
            mqttController.subscribe("/smartcar/report/gyroscope");
            mqttController.subscribe("/smartcar/report/instructionComplete");
        });

        manualControlScreenButton = findViewById(R.id.MainNavbarManual);
        drawControlScreenButton = findViewById(R.id.MainNavbarDraw);
        carImg = findViewById(R.id.imageViewCarAndPen);
        smokeImg = findViewById(R.id.imageViewSmokeParticle);
        //smokeImg.startAnimation(fadeOut);

        manualControlScreenButton.setOnClickListener(view -> openManualScreen());
        drawControlScreenButton.setOnClickListener(view -> openDrawScreen());
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