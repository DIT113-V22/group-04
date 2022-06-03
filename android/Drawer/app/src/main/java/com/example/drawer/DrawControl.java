package com.example.drawer;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DrawControl extends AppCompatActivity {

    private Button mainScreenButton;
    private Button manualControlScreenButton;
    Button runBtn;
    ImageButton uploadButton;
    ImageButton downloadButton;
    ImageButton clearButton;
    EditText numberViewSpeed;
    EditText numberViewCellLength;
    SeekBar seekBar;
    TextView pathLengthView;
    TextView distanceTraveledView;
    CanvasGrid pixelGrid;
    MQTTController mqttController = MQTTController.getInstance();

    private Button viewPoints;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private ListView pathList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_control);
        mqttController.publish("/smartcar/control/obstacle", "0");
        mqttController.publish("/smartcar/control/auto", "1");

        mainScreenButton = findViewById(R.id.DrawNavbarMain);
        manualControlScreenButton = findViewById(R.id.DrawNavbarManual);

        runBtn = findViewById(R.id.runButton);
        uploadButton = findViewById(R.id.uploadButton);
        downloadButton = findViewById(R.id.downloadButton);
        clearButton = findViewById(R.id.clearButton);

        numberViewSpeed = findViewById(R.id.numberViewSpeed);
        numberViewCellLength = findViewById(R.id.numberViewCellLength);

        pixelGrid = findViewById(R.id.pixelGridA);
        pixelGrid.setCellLength(30);
        pixelGrid.setResizeMode(CanvasGrid.ResizeMode.FIT_CONTENT);

        seekBar = findViewById(R.id.seekbar);
        pathLengthView = findViewById(R.id.textViewPathLength);
        distanceTraveledView = findViewById(R.id.textViewDistanceTraveled);
        mqttController.updateTextView(distanceTraveledView, "/smartcar/report/odometer");

        // Navbar
        mainScreenButton.setOnClickListener(view -> openMainScreen());
        manualControlScreenButton.setOnClickListener(view -> openManualScreen());
        viewPoints = findViewById(R.id.viewPointsSaved);

        downloadButton.setOnClickListener(view -> {
            Bitmap bitmap = viewToBitmap(pixelGrid);
            OutputStream imageOutStream;

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
        });

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri imageUri = null;
                    if (data != null) {
                        imageUri = data.getData();
                    }

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
            });

        uploadButton.setOnClickListener(view -> {
            Intent imagePickerIntent = new Intent(Intent.ACTION_PICK);
            File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String imageDirectoryPath = imageDirectory.getPath();
            Uri data = Uri.parse(imageDirectoryPath);
            imagePickerIntent.setDataAndType(data, "image/*");
            someActivityResultLauncher.launch(imagePickerIntent);
        });

        clearButton.setOnClickListener(view -> pixelGrid.clear());
        runBtn.setOnClickListener(view -> {
            mqttController.publish("/smartcar/control/obstacle", "0");
            mqttController.publish("/smartcar/control/auto", "1");
            String speed = numberViewSpeed.getText().toString();
            if (speed.isEmpty()) {
                return;
            }
            pixelGrid.executePath(speed);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int value;
                value = seekBar.getProgress();

                if (value > 4) {
                    pixelGrid.setCellLength(value);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
                    if (!numberViewCellLength.getText().toString().isEmpty()) {
                        float value = Float.parseFloat(numberViewCellLength.getText().toString());
                        pixelGrid.setPathScale(value);
                        updatePathLength();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        pixelGrid.setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            updatePathLength();
            return false;
        });

        viewPoints.setOnClickListener(view -> {
            //open the pop up window
            createViewContactDialogue();
        });
    }

    public void createViewContactDialogue() {
        builder = new AlertDialog.Builder(this);
        final View popUpView = getLayoutInflater().inflate(R.layout.activity_draw_saves, null);
        builder.setView(popUpView);
        alertDialog = builder.create();
        alertDialog.show();

        pathList = popUpView.findViewById(R.id.pathListDraw);
        List<String> savedPathList = new ArrayList<>();

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, savedPathList);
        pathList.setAdapter(arrayAdapter);
        onListItemClick(pathList, popUpView);
    }

    public void onListItemClick(ListView pathList, View v) {
        //Set background of all items to white
        for (int i = 0; i < pathList.getChildCount(); i++) {
            pathList.getChildAt(i).setBackgroundColor(Color.BLACK);
        }

        v.setBackgroundColor(Color.WHITE);
    }

    private void updatePathLength() {
        double pathLength = pixelGrid.getVectorMap().calculateSize() * pixelGrid.getPathScale();
        pathLength = Math.floor(pathLength * 100) / 100;
        String pathLengthText = "Path length: " + pathLength + " m";
        pathLengthView.setText(pathLengthText);
    }

    public void openMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openManualScreen() {
        Intent intent = new Intent(this, ManualControl.class);
        startActivity(intent);
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}