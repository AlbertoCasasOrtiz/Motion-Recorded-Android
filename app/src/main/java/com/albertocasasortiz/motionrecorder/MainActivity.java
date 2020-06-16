package com.albertocasasortiz.motionrecorder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.albertocasasortiz.motionrecorder.auxfunctions.Msg;
import com.albertocasasortiz.motionrecorder.auxfunctions.SensorsInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Store information recorded by sensors.
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // Manager for sensor reading.
    private SensorManager sensorManager;
    // Class to store sensor info.
    private SensorsInfo sensorsInfo;
    // Delay of sensor readings.
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;

    // Boolean that indicates if the sensors are reading data.
    private boolean recordingData;

    // Button for start sensor readings.
    private Button buttonStart;
    // Button for stop sensor readings.
    private Button buttonStop;
    // EditText with user id.
    private EditText editTextUserID;

    // Context in which the task is being executed
    private Context context;

    // Toast to show messages.
    private Toast toast;

    /**
     * OnCreate method. Instantiates the TTS with an initial message.
     * @param savedInstanceState A saved instance of the activity to load when the activity is destroyed and opened again.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize context object.
        this.context = this;

        // Initialize toast.
        this.toast = new Toast(this);

        // Initialize UI Components.
        this.editTextUserID = findViewById(R.id.editTextIdUser);
        this.buttonStart = findViewById(R.id.buttonStartRecord);
        this.buttonStop = findViewById(R.id.buttonStopRecord);
        this.buttonStop.setActivated(false);

        // Initialize sensors info and sensor manager.
        this.sensorsInfo = new SensorsInfo();
        this.sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);

        // Initialize booleans and buttons.
        this.recordingData = false;
        buttonStop.setEnabled(false);
        buttonStart.setEnabled(true);

        // Set listeners for buttons.
        this.setListeners();
    }

    /**
     * Set listeners for buttons.
     */
    private void setListeners(){
        // Set listener for start button.
        this.buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start capture data.
                registerSensorListeners();
                // Disable start button and enable stop button when pushed.
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                recordingData = true;
            }
        });
        // Set listener for stop button.
        this.buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop capture data.
                unregisterSensorListeners();
                // Disable stop button and enable start button when pushed.
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                recordingData = false;
            }
        });
    }

    /**
     * Register listener for every sensor we want.
     */
    private void registerSensorListeners(){
        if(!this.recordingData){
            this.recordingData = true;
            if ( this.sensorManager != null) {
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SENSOR_DELAY);
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SENSOR_DELAY);
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SENSOR_DELAY);
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SENSOR_DELAY);
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SENSOR_DELAY);
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SENSOR_DELAY);
                Msg.showToast(context, toast, "Recording sensor data.", Toast.LENGTH_SHORT);
            } else {
                Msg.showToast(context, toast, "SensorManager not instantiated.", Toast.LENGTH_SHORT);
            }
        } else {
            Msg.showToast(context, toast, "Already recording data.", Toast.LENGTH_SHORT);
        }
    }

    /**
     * Unregister sensors that are recording data.
     */
    private void unregisterSensorListeners(){
        if(this.recordingData) {
            this.recordingData = false;
            // Don't receive any more updates from either sensor.
            sensorManager.unregisterListener(this);
            Msg.showToast(context, toast, "Stop recording data.", Toast.LENGTH_SHORT);

            // Save data in external storage.
            if (isExternalStorageWritable()) {
                // Tell the user that the app is saving data.
                Msg.showToast(context, toast, "Saving data in file.", Toast.LENGTH_SHORT);
                // Get date and time to set the name.
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CANADA);
                Date date = new Date();
                // Construct fileName.
                String fileName = dateFormat.format(date) + "_" + editTextUserID.getText().toString();

                // List of string to store the data.
                List<String> dataFromSensors = new LinkedList<>();
                // Add each row to file.
                for(int i = sensorsInfo.getLowestArraySize() - 1; i >= 0; i--){
                    dataFromSensors.add(this.sensorsInfo.getLine(i));
                }
                // Set header of the file at the end since the array will be reversed.
                dataFromSensors.add(this.sensorsInfo.getHeader());
                // Since each row was added in reverse order, reverse array.
                Collections.reverse(dataFromSensors);
                // Save data in external storage.
                // String with the format of the saved file.
                String fileFormat = ".csv";
                saveDataInExternalStorage(context, fileName + fileFormat, dataFromSensors);
            } else {
                // Show error if external storage is not writable.
                Msg.showToast(context, toast, "Cannot write in external storage.", Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * Check if external storage is available for write data.
     * @return True if external storage is available.
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Save data contained in a String in the external storage.
     * @param context  Context of the activity.
     * @param fileName Name of the file.
     * @param body     Body of message (As array of strings).
     */
    @SuppressWarnings( "deprecation" )
    private void saveDataInExternalStorage(Context context, String fileName, List<String> body) {
        try {
            // Set folder where save data.
            // String with the path of the files.
            String pathFolder = "IMU-Data";
            //TODO The function getExternalStorageDirectory() is deprecated.
            File root = new File(Environment.getExternalStorageDirectory(), pathFolder);
            // Create directory if not exists.
            boolean created = true;
            if (!root.exists()) {
                created = root.mkdir();
            }
            if(root.exists() && created) {
                // If directory created...
                // Create file object.
                File gpxfile = new File(root, fileName);
                // Create file writer.
                FileWriter writer = new FileWriter(gpxfile);
                // Write data.
                for (int i = 0; i < body.size(); i++) {
                    writer.append(body.get(i));
                    writer.append("\n");
                }
                writer.flush();
                writer.close();
                Msg.showToast(context, toast, "Data saved in file: " + fileName, Toast.LENGTH_SHORT);
                this.sensorsInfo.clear();
            } else {
                Msg.showToast(context, toast, "Could not create directory..", Toast.LENGTH_SHORT);
            }
        } catch (IOException e) {
            Msg.showToast(context, toast, "Error saving data in file.", Toast.LENGTH_SHORT);
        }
    }

    /**
     * On application paused, restart booleans and stop recording data.
     */
    @Override
    protected void onPause() {
        super.onPause();
        buttonStop.setActivated(false);
        buttonStart.setActivated(true);
        recordingData = false;
        unregisterSensorListeners();
    }

    /**
     * Save values read by the sensors.
     * @param event Event containing information about the sensor and the captured data.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            sensorsInfo.addAccelerometerReading(event.values.clone());
        } else if (event.sensor.getType()==Sensor.TYPE_GRAVITY){
            sensorsInfo.addGravityReading(event.values.clone());
        } else if (event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            sensorsInfo.addGyroscopeReading(event.values.clone());
        } else if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION) {
            sensorsInfo.addLinearAccelerationReading(event.values.clone());
        } else if(event.sensor.getType()==Sensor.TYPE_GAME_ROTATION_VECTOR){
            sensorsInfo.addGameRotationVectorReading(event.values.clone());
        } else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            sensorsInfo.addMagneticFieldReading(event.values.clone());
        }
    }

    /**
     * Not used.
     * @param sensor Not used.
     * @param accuracy Not used.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
