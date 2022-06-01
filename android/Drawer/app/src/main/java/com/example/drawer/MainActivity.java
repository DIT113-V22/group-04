package com.example.drawer;

import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button pubBtn;
    private Button disBtn;
    private Button conBtn;
    private Button subBtn;
    private Button mainScreenButton;
    private Button manualControlScreenButton;
    private Button drawControlScreenButton;
    private ImageView carImg;
    private ImageView smokeImg;

    MQTTController mqttController = MQTTController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connection button
        conBtn = findViewById(R.id.conBtn);
        conBtn.setOnClickListener(view -> mqttController.connect());

        // Publish button
        pubBtn = findViewById(R.id.pubBtn);
        pubBtn.setOnClickListener(view ->
                mqttController.publish("/smartcar/control/throttle", "50"));

        // Disconnect button
        disBtn = findViewById(R.id.disBtn);
        disBtn.setOnClickListener(view -> mqttController.disconnect());

        // Subscription button
        subBtn = findViewById(R.id.subBtn);
        subBtn.setOnClickListener(view -> {
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

        mainScreenButton = findViewById(R.id.MainNavbarMain);
        manualControlScreenButton = findViewById(R.id.MainNavbarManual);
        drawControlScreenButton = findViewById(R.id.MainNavbarDraw);
        carImg = findViewById(R.id.imageViewCarAndPen);
        smokeImg = findViewById(R.id.imageViewSmokeParticle);

        Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        smokeImg.startAnimation(fadeOut);

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