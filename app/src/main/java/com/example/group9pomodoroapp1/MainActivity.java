package com.example.group9pomodoroapp1;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.group9pomodoroapp1.utils.CheckMarkUtils;
import com.example.group9pomodoroapp1.utils.Constants;
import com.example.group9pomodoroapp1.utils.StartTimerUtils;
import com.example.group9pomodoroapp1.utils.StopTimerUtils;
import com.example.group9pomodoroapp1.utils.Utils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static int currentSessionType;

    BroadcastReceiver stoppedBroadcastReceiver;
    BroadcastReceiver countDownReceiver;
    BroadcastReceiver completedBroadcastReceiver;
    BroadcastReceiver startBroadcastReceiver;

    ImageView settingsImageView;
    ToggleButton timerButton;
    TextView countDownTextView;
    ImageView finishImageView;
    EditText message;

    private long workDuration;
    private String workDurationString;
    private long shortBreakDuration;
    private String shortBreakDurationString;
    private long longBreakDuration;
    private String longBreakDurationString;
    private SharedPreferences preferences;
    private AlertDialog alertDialog;
    private boolean isAppVisible = true;
    private String currentCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isAppVisible = true;

        // Initialize views
        settingsImageView = findViewById(R.id.settings_imageview_main);
        timerButton = findViewById(R.id.timer_button_main);
        countDownTextView = findViewById(R.id.countdown_textview_main);
        finishImageView = findViewById(R.id.finish_imageview_main);
        message = findViewById(R.id.current_task_name_textview_main);

        preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        setOnClickListeners();

        determineViewState(isServiceRunning(CountDownTimerService.class));
        retrieveDurationValues();
        setInitialValuesOnScreen();

        // Broadcast receivers
        stoppedBroadcastReceiver = intent -> sessionCompleteAVFeedback(getApplicationContext());
        countDownReceiver = intent -> {
            if (intent.getStringExtra("countDown") != null) {
                currentCountDown = intent.getStringExtra("countDown");
                setTextCountDownTextView(currentCountDown);
            }
        };
        completedBroadcastReceiver = intent -> sessionCompleteAVFeedback(getApplicationContext());
        startBroadcastReceiver = intent -> sessionStartAVFeedback();

        registerLocalBroadcastReceivers();

        message.setText(preferences.getString("autoSave", ""));
        if (message.getText().toString().trim().isEmpty())
            message.setText("Task 1");

        message.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                preferences.edit().putInt(Constants.TASK_PROGRESS_COUNT_KEY, 0).apply();
                preferences.edit().putString("autoSave", s.toString()).apply();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setOnClickListeners() {
        settingsImageView.setOnClickListener(this);
        timerButton.setOnClickListener(this);
        finishImageView.setOnClickListener(this);
    }

    private void registerLocalBroadcastReceivers() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(stoppedBroadcastReceiver,
                new IntentFilter(Constants.STOP_ACTION_BROADCAST));
        manager.registerReceiver(countDownReceiver,
                new IntentFilter(Constants.COUNTDOWN_BROADCAST));
        manager.registerReceiver(completedBroadcastReceiver,
                new IntentFilter(Constants.COMPLETE_ACTION_BROADCAST));
        manager.registerReceiver(startBroadcastReceiver,
                new IntentFilter(Constants.START_ACTION_BROADCAST));
    }

    private void unregisterLocalBroadcastReceivers() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(stoppedBroadcastReceiver);
        manager.unregisterReceiver(countDownReceiver);
        manager.unregisterReceiver(completedBroadcastReceiver);
        manager.unregisterReceiver(startBroadcastReceiver);
    }

    private void retrieveDurationValues() {
        workDuration = Utils.getCurrentDurationPreferenceOf(preferences, this, Constants.WORK_SESSION);
        shortBreakDuration = Utils.getCurrentDurationPreferenceOf(preferences, this, Constants.SHORT_BREAK);
        longBreakDuration = Utils.getCurrentDurationPreferenceOf(preferences, this, Constants.LONG_BREAK);

        workDurationString = Utils.formatDuration(workDuration);
        shortBreakDurationString = Utils.formatDuration(shortBreakDuration);
        longBreakDurationString = Utils.formatDuration(longBreakDuration);
    }

    private void changeToggleButtonStateText(int sessionType) {
        timerButton.setChecked(isServiceRunning(CountDownTimerService.class));
        switch (sessionType) {
            case Constants.WORK_SESSION:
                countDownTextView.setText(workDurationString);
                break;
            case Constants.SHORT_BREAK:
                countDownTextView.setText(shortBreakDurationString);
                break;
            case Constants.LONG_BREAK:
                countDownTextView.setText(longBreakDurationString);
                break;
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        currentSessionType = Utils.retrieveCurrentSessionType(preferences, this);
        switch (v.getId()) {
            case R.id.settings_imageview_main:
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case R.id.timer_button_main:
                if (timerButton.isChecked()) {
                    switch (currentSessionType) {
                        case Constants.WORK_SESSION:
                            StartTimerUtils.startTimer(workDuration, this);
                            break;
                        case Constants.SHORT_BREAK:
                            StartTimerUtils.startTimer(shortBreakDuration, this);
                            break;
                        case Constants.LONG_BREAK:
                            StartTimerUtils.startTimer(longBreakDuration, this);
                            break;
                    }
                } else {
                    StopTimerUtils.sessionCancel(this, preferences);
                }
                preferences.edit().putString(Constants.TASK_MESSAGE, message.getText().toString()).apply();
                break;
            case R.id.finish_imageview_main:
                if (timerButton.isChecked()) {
                    StopTimerUtils.sessionComplete(this);
                }
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.WORK_DURATION_KEY) ||
                key.equals(Constants.SHORT_BREAK_DURATION_KEY) ||
                key.equals(Constants.LONG_BREAK_DURATION_KEY) ||
                key.equals(Constants.START_LONG_BREAK_AFTER_KEY)) {
            retrieveDurationValues();
            setInitialValuesOnScreen();
        } else if (key.equals(Constants.TASK_PROGRESS_COUNT_KEY)) {
            CheckMarkUtils.updateCheckMarkCount(this);
        }
    }

    // Helper Methods

    private void determineViewState(boolean isServiceRunning) {
        timerButton.setChecked(isServiceRunning);
        message.setEnabled(!isServiceRunning);
        message.setFocusable(!isServiceRunning);
        message.setClickable(!isServiceRunning);
    }

    private void setInitialValuesOnScreen() {
        currentSessionType = Utils.retrieveCurrentSessionType(preferences, this);
        changeToggleButtonStateText(currentSessionType);
    }

    private void sessionCompleteAVFeedback(Context context) {
        message.setClickable(true);
        message.setFocusable(true);
        message.setFocusableInTouchMode(true);
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        registerLocalBroadcastReceivers();
        changeToggleButtonStateText(currentSessionType);
        startTimer(context, preferences);
        timerButton.setChecked(isServiceRunning(CountDownTimerService.class));
    }

    private void sessionStartAVFeedback() {
        timerButton.setChecked(true);
    }

    private void setTextCountDownTextView(String countdown) {
        countDownTextView.setText(countdown);
    }

    private void startTimer(Context context, SharedPreferences preferences) {
        long duration = 0;
        switch (currentSessionType) {
            case Constants.WORK_SESSION:
                duration = Utils.getCurrentDurationPreferenceOf(preferences, context, Constants.WORK_SESSION);
                break;
            case Constants.SHORT_BREAK:
                duration = Utils.getCurrentDurationPreferenceOf(preferences, context, Constants.SHORT_BREAK);
                break;
            case Constants.LONG_BREAK:
                duration = Utils.getCurrentDurationPreferenceOf(preferences, context, Constants.LONG_BREAK);
                break;
        }
        StartTimerUtils.startTimer(duration, context);
    }
}