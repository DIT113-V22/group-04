package com.example.drawer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DrawControl extends AppCompatActivity {

    private Button readMeScreen;
    private Button manualControlScreen;
    private Button drawControlScreen;
    ImageView uploadedImage;
    Button runBttn;
    Button uploadBttn;
    TextInputEditText text;
    SeekBar seekBar;
    TextView speedView;

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
        speedView = findViewById(R.id.speed);
        uploadedImage = findViewById(R.id.uploadedImage);

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Uri imageUri = data.getData();

                            InputStream inputStream;
                            try {
                                inputStream = getContentResolver().openInputStream(imageUri);
                                Bitmap image = BitmapFactory.decodeStream(inputStream);
                                uploadedImage.setImageBitmap(image);
                                uploadedImage.setAlpha(75);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        uploadBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imagePickerIntent = new Intent(Intent.ACTION_PICK);
                File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String imageDirectoryPath = imageDirectory.getPath();
                Uri data = Uri.parse(imageDirectoryPath);
                imagePickerIntent.setDataAndType(data, "image/*" );
                someActivityResultLauncher.launch(imagePickerIntent);
            }

        });
        seekBar.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    String speedText = Integer.toString(i);
                    speedView.setText("Current speed:" + speedText);
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