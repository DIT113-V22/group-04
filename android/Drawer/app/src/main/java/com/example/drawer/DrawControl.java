package com.example.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DrawControl extends AppCompatActivity {

    private Button readMeScreen;
    private Button manualControlScreen;
    private Button drawControlScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_control);

        readMeScreen = findViewById(R.id.ReadMEScreen);
        manualControlScreen = findViewById(R.id.ManualScreen);
        drawControlScreen = findViewById(R.id.DrawScreen);

        readMeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReadMEScreen();
            }
        });

        manualControlScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openManualScreen();
            }
        });

        drawControlScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawScreen();
            }
        });
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
