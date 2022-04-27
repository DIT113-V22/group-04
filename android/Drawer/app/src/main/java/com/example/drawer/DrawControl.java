package com.example.drawer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class DrawControl extends AppCompatActivity {

    private Button readMeScreen;
    private Button manualControlScreen;
    private Button drawControlScreen;
    Button runBttn;
    Button uploadBttn;
    TextInputEditText text;
    SeekBar seekBar;
    TextView speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_control);

        readMeScreen = findViewById(R.id.ReadMEScreen);
        manualControlScreen = findViewById(R.id.ManualScreen);
        drawControlScreen = findViewById(R.id.DrawScreen);
        runBttn = findViewById(R.id.runButton);
        uploadBttn = findViewById(R.id.uploadButton);
        text = findViewById(R.id.textBox);
        seekBar = findViewById(R.id.seekbar);
        speed = findViewById(R.id.speed);


        seekBar.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

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
