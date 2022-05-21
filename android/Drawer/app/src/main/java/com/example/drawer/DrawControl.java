package com.example.drawer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

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
    EditText numberViewCellLength;
    SeekBar seekBar;
    TextView speedView;
    TextView pathLengthView;
    CanvasGrid pixelGrid;
    MQTTController mqttController = MQTTController.getInstance();

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
        numberViewCellLength = findViewById(R.id.numberViewCellLength);

        pixelGrid = findViewById(R.id.pixelGridA);
        pixelGrid.setCellLength(30);
        pixelGrid.setResizeMode(CanvasGrid.ResizeMode.FIT_CONTENT);

        seekBar = findViewById(R.id.seekbar);
        speedView = findViewById(R.id.textViewSpeed);
        pathLengthView = findViewById(R.id.textViewPathLength);

        readMeScreen.setOnClickListener(view -> openReadMEScreen());
        manualControlScreen.setOnClickListener(view -> openManualScreen());
        drawControlScreen.setOnClickListener(view -> openDrawScreen());

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = viewToBitmap(pixelGrid);
                OutputStream imageOutStream = null;

                ContentValues contentValues = new ContentValues();

                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "drawing.png");
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                try {
                    imageOutStream = getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);
                    Toast toast = Toast.makeText(DrawControl.this,"Saved!", Toast.LENGTH_LONG);
                    toast.show();
                    imageOutStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
                                Drawable myDrawable = new BitmapDrawable(getResources(), image);
                                myDrawable.setAlpha(60);
                                pixelGrid.setBackground(myDrawable);
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
                File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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
                mqttController.publish("/smartcar/control/throttle", speed);
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
                        speedView.setText("Speed:" + seekBar.getProgress());
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
        numberViewCellLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    float value = Float.parseFloat(numberViewCellLength.getText().toString());

                    pixelGrid.setPathScale(value);
                    double pathLength = (pixelGrid.getVectorMap().calculateSize() * pixelGrid.getPathScale());
                    pathLength = Math.floor(pathLength * 100) / 100;
                    pathLengthView.setText("Path length: " +  pathLength);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        numberViewCellLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                pixelGrid.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        double pathLength = (pixelGrid.getVectorMap().calculateSize() * pixelGrid.getPathScale());
                        pathLength = Math.floor(pathLength * 100) / 100;
                        pathLengthView.setText("Path length: " + pathLength);
                        return false;
                    }
                });
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}
