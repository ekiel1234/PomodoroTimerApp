package com.example.group9pomodoroapp1;

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

    private BroadcastReceiver stoppedBroadcastReceiver;
    private BroadcastReceiver countDownReceiver;
    private BroadcastReceiver completedBroadcastReceiver;
    private BroadcastReceiver startBroadcastReceiver;

    private ImageView settingsImageView;
    private ToggleButton timerButton;
    private TextView countDownTextView;
    private ImageView finishImageView;
    private EditText message;

    private long workDuration;
    private long shortBreakDuration;
    private long longBreakDuration;
    private SharedPreferences preferences;
    private AlertDialog alertDialog;
    private String currentCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("prefs", MODE_PRIVATE);
        Utils.updateCurrentSessionType(preferences, this, Constants.WORK_SESSION); // Force start with WORK_SESSION

        settingsImageView = findViewById(R.id.settings_imageview_main);
        timerButton = findViewById(R.id.timer_button_main);
        countDownTextView = findViewById(R.id.countdown_textview_main);
        finishImageView = findViewById(R.id.finish_imageview_main);
        message = findViewById(R.id.current_task_name_textview_main);

        setOnClickListeners();
        retrieveDurationValues();
        setInitialValuesOnScreen();

        stoppedBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sessionCompleteAVFeedback(context);
            }
        };

        countDownReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String countDownValue = intent.getStringExtra("countDown");
                if (countDownValue != null) {
                    currentCountDown = countDownValue;
                    countDownTextView.setText(currentCountDown);
                }
            }
        };

        completedBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sessionCompleteAVFeedback(context);
            }
        };

        startBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timerButton.setChecked(true);
            }
        };

        registerLocalBroadcastReceivers();

        message.setText(preferences.getString(Constants.TASK_MESSAGE, getString(R.string.default_task_message)));
        message.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                preferences.edit()
                        .putInt(Constants.TASK_PROGRESS_COUNT_KEY, 0)
                        .putString(Constants.TASK_MESSAGE, s.toString())
                        .apply();
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

    private void retrieveDurationValues() {
        workDuration = Utils.getCurrentDurationPreferenceOf(preferences, this, Constants.WORK_SESSION);
        shortBreakDuration = Utils.getCurrentDurationPreferenceOf(preferences, this, Constants.SHORT_BREAK);
        longBreakDuration = Utils.getCurrentDurationPreferenceOf(preferences, this, Constants.LONG_BREAK);
    }

    private void setInitialValuesOnScreen() {
        currentSessionType = Utils.retrieveCurrentSessionType(preferences, this);
        switch (currentSessionType) {
            case Constants.WORK_SESSION:
                countDownTextView.setText(Utils.formatDuration(workDuration));
                break;
            case Constants.SHORT_BREAK:
                countDownTextView.setText(Utils.formatDuration(shortBreakDuration));
                break;
            case Constants.LONG_BREAK:
                countDownTextView.setText(Utils.formatDuration(longBreakDuration));
                break;
        }
    }

    private void registerLocalBroadcastReceivers() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(stoppedBroadcastReceiver, new IntentFilter(Constants.STOP_ACTION_BROADCAST));
        manager.registerReceiver(countDownReceiver, new IntentFilter(Constants.COUNTDOWN_BROADCAST));
        manager.registerReceiver(completedBroadcastReceiver, new IntentFilter(Constants.COMPLETE_ACTION_BROADCAST));
        manager.registerReceiver(startBroadcastReceiver, new IntentFilter(Constants.START_ACTION_BROADCAST));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        currentSessionType = Utils.retrieveCurrentSessionType(preferences, this);

        if (id == R.id.settings_imageview_main) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.timer_button_main) {
            if (timerButton.isChecked()) {
                long duration = 0;
                switch (currentSessionType) {
                    case Constants.WORK_SESSION:
                        duration = workDuration;
                        break;
                    case Constants.SHORT_BREAK:
                        duration = shortBreakDuration;
                        break;
                    case Constants.LONG_BREAK:
                        duration = longBreakDuration;
                        break;
                }
                StartTimerUtils.startTimer(duration, this);
            }
            preferences.edit().putString(Constants.TASK_MESSAGE, message.getText().toString()).apply();
        } else if (id == R.id.finish_imageview_main) {
            if (timerButton.isChecked()) {
                StopTimerUtils.sessionComplete(this);
            }
        }
    }

    private void sessionCompleteAVFeedback(Context context) {
        message.setEnabled(true);
        message.setFocusable(true);
        message.setFocusableInTouchMode(true);
        if (alertDialog != null) alertDialog.dismiss();

        if (currentSessionType == Constants.WORK_SESSION) {
            int nextBreakType = Utils.getTypeOfBreak(preferences, context);
            Utils.updateCurrentSessionType(preferences, context, nextBreakType);
        } else {
            Utils.updateCurrentSessionType(preferences, context, Constants.WORK_SESSION);
        }

        currentSessionType = Utils.retrieveCurrentSessionType(preferences, context);
        setInitialValuesOnScreen();

        long nextDuration = Utils.getCurrentDurationPreferenceOf(preferences, context, currentSessionType);
        StartTimerUtils.startTimer(nextDuration, context);
        timerButton.setChecked(true);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == null) return;
        if (key.equals(Constants.WORK_DURATION_KEY) ||
                key.equals(Constants.SHORT_BREAK_DURATION_KEY) ||
                key.equals(Constants.LONG_BREAK_DURATION_KEY) ||
                key.equals(Constants.LONG_BREAK_AFTER_KEY)) {
            retrieveDurationValues();
            setInitialValuesOnScreen();
        } else if (key.equals(Constants.TASK_PROGRESS_COUNT_KEY)) {
            CheckMarkUtils.updateCheckMarkCount(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(stoppedBroadcastReceiver);
        manager.unregisterReceiver(countDownReceiver);
        manager.unregisterReceiver(completedBroadcastReceiver);
        manager.unregisterReceiver(startBroadcastReceiver);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
