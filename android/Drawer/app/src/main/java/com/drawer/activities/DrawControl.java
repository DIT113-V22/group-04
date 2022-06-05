package com.drawer.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.drawer.R;
import com.drawer.canvas.CanvasGrid;
import com.drawer.connectivity.MQTTController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The back-end for the activity_draw_control.
 * Its main responsibility is to create all the different UI elements and handle
 * events (commands issued by the user through interaction) related to those elements.
 *
 * @author Ayaeis
 * @author Kev049
 * @author MortBA
 * @author YukiMina14
 */
public class DrawControl extends AppCompatActivity {

    private Button mainScreenButton;
    private Button manualControlScreenButton;
    private Button runBtn;
    private ImageButton uploadButton;
    private ImageButton downloadButton;
    private ImageButton clearButton;
    private EditText numberViewSpeed;
    private EditText numberViewCellLength;
    private SeekBar seekBar;
    private TextView pathLengthView;
    private TextView distanceTraveledView;
    private CanvasGrid pixelGrid;
    private MQTTController mqttController = MQTTController.getInstance();

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

        mainScreenButton.setOnClickListener(view -> openMainScreen());
        manualControlScreenButton.setOnClickListener(view -> openManualScreen());

        /*
         * This method converts the current view (drawing) to a bitmap
         * by calling another method. Then saves the bitmap to the image gallery.
         */
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

        /*
         * This method converts the image to a drawable and sets it as a background image.
         */
        ActivityResultLauncher<Intent> uploadImgLauncher = registerForActivityResult(
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

        /*
         * This method displays the gallery upon clicking the button
         * and lets the user choose an image to upload as a background picture.
         * The image is then launched to be set as a background.
         */
        uploadButton.setOnClickListener(view -> {
            Intent imagePickerIntent = new Intent(Intent.ACTION_PICK);
            File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String imageDirectoryPath = imageDirectory.getPath();
            Uri data = Uri.parse(imageDirectoryPath);
            imagePickerIntent.setDataAndType(data, "image/*");
            uploadImgLauncher.launch(imagePickerIntent);
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

        /*
         * This method acts as a listener to the seekbar, and  updates the grids every time
         * the progress of the seekbar is changed.
         */
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
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        numberViewCellLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

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
    }

    /**
     * Calculates the current path length and updates path length text field.
     */
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

    /**
     * Converts the current drawing to a bitmap.
     *
     * @param view the view where the drawing is
     *
     * @return bitmap of drawing
     */
    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}