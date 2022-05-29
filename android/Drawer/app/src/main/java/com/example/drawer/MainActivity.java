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
    private Button introScreenBtn;
    private Button manualControlScreenBtn;
    private Button drawControlScreenBtn;
    private ImageView carImg;
    private ImageView smokeImg;

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
            System.out.println("SUB");
            mqttController.subscribe("/smartcar/report/startup");
            mqttController.subscribe("/smartcar/report/status");
            mqttController.subscribe("/smartcar/report/camera");
            mqttController.subscribe("/smartcar/report/obstacle");
            mqttController.subscribe("/smartcar/report/ultrasound");
            mqttController.subscribe("/smartcar/control/throttle");
        });

        carImg = findViewById(R.id.imageViewCarAndPen);
        smokeImg = findViewById(R.id.imageViewSmokeParticle);

        Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);

        smokeImg.startAnimation(fadeOut);

        introScreenBtn = findViewById(R.id.ReadMeScreen);
        manualControlScreenBtn = findViewById(R.id.ManualScreen);
        drawControlScreenBtn = findViewById(R.id.DrawScreen);

        introScreenBtn.setOnClickListener(view -> openReadMEScreen());
        manualControlScreenBtn.setOnClickListener(view -> openManualScreen());
        drawControlScreenBtn.setOnClickListener(view -> openDrawScreen());
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