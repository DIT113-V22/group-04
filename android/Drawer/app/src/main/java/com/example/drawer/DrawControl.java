package com.example.drawer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DrawControl extends AppCompatActivity {

    private Button readMeScreen;
    private Button manualControlScreen;
    private Button drawControlScreen;
    ImageView uploadedImage;
    Button runBtn;
    ImageButton uploadBtn;
    ImageButton downloadBtn;
    ImageButton clearBtn;
    EditText numberViewCellSize;
    SeekBar seekBar;
    TextView speedView;
    CanvasGrid pixelGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_control);

        readMeScreen = findViewById(R.id.ReadMEScreen);
        manualControlScreen = findViewById(R.id.ManualScreen);
        drawControlScreen = findViewById(R.id.DrawScreen);

        runBtn = findViewById(R.id.runButton);
        uploadBtn = findViewById(R.id.uploadBttn);
        downloadBtn = findViewById(R.id.downloadBttn);
        clearBtn = findViewById(R.id.clearBttn);

        numberViewCellSize = findViewById(R.id.numberViewCellSize);

        pixelGrid = findViewById(R.id.pixelGridA);
        pixelGrid.setCellLength(20);
        pixelGrid.setResizeMode(CanvasGrid.ResizeMode.FIT_CONTENT);

        seekBar = findViewById(R.id.seekbar);
        speedView = findViewById(R.id.speed);

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
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imagePickerIntent = new Intent(Intent.ACTION_PICK);
                File imageDirectory = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String imageDirectoryPath = imageDirectory.getPath();
                Uri data = Uri.parse(imageDirectoryPath);
                imagePickerIntent.setDataAndType(data, "image/*");
                someActivityResultLauncher.launch(imagePickerIntent);
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pixelGrid.clear();
            }
        });
        runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String speed = Integer.toString(seekBar.getProgress());
                pixelGrid.executePath();
                MQTTController.publish("/smartcar/control/throttle", speed);
            }
        });
        seekBar.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    String speedText = Integer.toString(i);
                    speedView.setText("Current speed:" + speedText);
                    speedView.setTextColor(Color.BLACK);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

        numberViewCellSize.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    int value;
                    value = Integer.parseInt(numberViewCellSize.getText().toString());

                    if (value > 4) {
                        pixelGrid.setCellLength(value);
                        speedView.setText("Current speed:" + seekBar.getProgress());
                        speedView.setTextColor(Color.BLACK);
                    } else {
                        throw new Exception();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    speedView.setText("Number must be\n       over > 4");
                    speedView.setTextColor(Color.RED);
                }
            }
        });

        readMeScreen.setOnClickListener(view -> openReadMEScreen());
        manualControlScreen.setOnClickListener(view -> openManualScreen());
        drawControlScreen.setOnClickListener(view -> openDrawScreen());
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
