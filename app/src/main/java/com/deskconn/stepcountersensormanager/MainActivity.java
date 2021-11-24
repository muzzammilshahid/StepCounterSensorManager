package com.deskconn.stepcountersensormanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView textViewStepCounter;
    private SensorManager sensorManager;
    private Sensor sensorStepCounter;
    private boolean isCounterSensorPresent;
    private Button resetButton;
    int stepCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textViewStepCounter = findViewById(R.id.steps);

        resetButton = findViewById(R.id.reset_button);

        textViewStepCounter.setText(String.valueOf(getStepsFromSharedPreferences("steps")));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        stepCount = getStepsFromSharedPreferences("steps");

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            sensorStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        } else {
            textViewStepCounter.setText(R.string.sensor_not_present);
            isCounterSensorPresent = false;
        }

        resetButton.setOnClickListener(view -> {
            stepCount = 0;
            saveStepToSharedPreferences("steps", stepCount);
            textViewStepCounter.setText(String.valueOf(getStepsFromSharedPreferences("steps")));
        });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == sensorStepCounter) {
            saveStepToSharedPreferences("steps", stepCount++);
            textViewStepCounter.setText(String.valueOf(getStepsFromSharedPreferences("steps")));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
            sensorManager.registerListener(this, sensorStepCounter, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
            sensorManager.unregisterListener(this, sensorStepCounter);
    }

    public SharedPreferences getPreferenceManager() {
        return getSharedPreferences("shared_prefs", MODE_PRIVATE);
    }

    public void saveStepToSharedPreferences(String key, int value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putInt(key, value).apply();
    }


    public int getStepsFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getInt(key, 0);
    }
}