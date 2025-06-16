package com.example.group9pomodoroapp1;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group9pomodoroapp1.adapter.TaskAdapter;
import com.example.group9pomodoroapp1.model.Task;
import com.example.group9pomodoroapp1.utils.Constants;
import com.example.group9pomodoroapp1.utils.StartTimerUtils;
import com.example.group9pomodoroapp1.utils.StopTimerUtils;
import com.example.group9pomodoroapp1.utils.TaskStorageUtils;
import com.example.group9pomodoroapp1.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ToggleButton timerButton;
    private TextView countDownTextView;
    private Button pauseButton;
    private TaskAdapter taskAdapter;
    private final List<Task> taskList = new ArrayList<>();

    private SharedPreferences preferences;
    private long workDuration, shortBreakDuration, longBreakDuration;
    private boolean isPaused = false;
    private long remainingTime = 0;
    private TabLayout sessionTabs;

    private BroadcastReceiver stoppedBroadcastReceiver;
    private BroadcastReceiver countDownReceiver;
    private BroadcastReceiver completedBroadcastReceiver;
    private BroadcastReceiver startBroadcastReceiver;

    private void showSessionEndDialog() {
        int currentSession = Utils.retrieveCurrentSessionType(preferences);
        String message = (currentSession == Constants.WORK_SESSION)
                ? "Work session done! Time for a break!"
                : "Break over! Time to focus again!";

        new AlertDialog.Builder(this)
                .setTitle("Session Complete")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {})
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("prefs", MODE_PRIVATE);

        // ⏱️ Timer UI
        countDownTextView = findViewById(R.id.countdown_textview_main);
        timerButton = findViewById(R.id.timer_button_main);
        pauseButton = findViewById(R.id.pause_button_main);
        ImageView settingsImageView = findViewById(R.id.settings_imageview_main);
        FloatingActionButton addTaskButton = findViewById(R.id.add_task_fab);
        ImageView distractionButton = findViewById(R.id.distraction_button);
        RecyclerView recyclerView = findViewById(R.id.task_recycler_view);
        sessionTabs = findViewById(R.id.session_tab_layout);

        retrieveDurationValues();
        setInitialValuesOnScreen();
        Utils.prepareSoundPool(this);
        loadTasksFromStorage();

        // Pause button logic
        pauseButton.setOnClickListener(v -> {
            StopTimerUtils.pauseTimer(this);
            isPaused = true;
            timerButton.setChecked(false);
        });

        // RecyclerView setup
        taskAdapter = new TaskAdapter(this, taskList, position -> {
            taskList.remove(position);
            taskAdapter.notifyItemRemoved(position);
            TaskStorageUtils.saveTasks(this, taskList);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        // Tab setup
        sessionTabs.addTab(sessionTabs.newTab().setText("Pomodoro"));
        sessionTabs.addTab(sessionTabs.newTab().setText("Short Break"));
        sessionTabs.addTab(sessionTabs.newTab().setText("Long Break"));

        sessionTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (timerButton.isChecked()) {
                    timerButton.setChecked(false);
                    StopTimerUtils.sessionComplete(MainActivity.this);
                }
                int position = tab.getPosition();
                if (position == 0) {
                    Utils.updateCurrentSessionType(preferences, Constants.WORK_SESSION);
                } else if (position == 1) {
                    Utils.updateCurrentSessionType(preferences, Constants.SHORT_BREAK);
                } else if (position == 2) {
                    Utils.updateCurrentSessionType(preferences, Constants.LONG_BREAK);
                }
                retrieveDurationValues();
                setInitialValuesOnScreen();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Broadcast receivers
        registerReceiver(stoppedBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setInitialValuesOnScreen();
                timerButton.setChecked(false);
                isPaused = false;
                remainingTime = 0;
            }
        }, new IntentFilter(Constants.STOP_ACTION_BROADCAST), Context.RECEIVER_NOT_EXPORTED);

        registerReceiver(countDownReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String countDownValue = intent.getStringExtra("countDown");
                if (countDownValue != null) {
                    countDownTextView.setText(countDownValue);
                    remainingTime = Utils.convertTimeToMillis(countDownValue);
                }
            }
        }, new IntentFilter(Constants.COUNTDOWN_BROADCAST), Context.RECEIVER_NOT_EXPORTED);

        registerReceiver(completedBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleTimerFinish();
            }
        }, new IntentFilter(Constants.COMPLETE_ACTION_BROADCAST), Context.RECEIVER_NOT_EXPORTED);

        registerReceiver(startBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timerButton.setChecked(true);
            }
        }, new IntentFilter(Constants.START_ACTION_BROADCAST), Context.RECEIVER_NOT_EXPORTED);

        settingsImageView.setOnClickListener(this);
        timerButton.setOnClickListener(this);
        addTaskButton.setOnClickListener(this);
        distractionButton.setOnClickListener(this);
    }

    private void handleTimerFinish() {
        int sessionType = Utils.retrieveCurrentSessionType(preferences);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.bellringing);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();

        String message = (sessionType == Constants.WORK_SESSION)
                ? "Time is up! Time to take a break!"
                : "Time is up! Time to go back to work!";

        new AlertDialog.Builder(this)
                .setTitle("Session Complete")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> advanceSession())
                .show();

        if (sessionType == Constants.WORK_SESSION && !taskList.isEmpty()) {
            Task currentTask = taskList.get(0);
            currentTask.incrementProgress();
            if (currentTask.getProgress() >= currentTask.getEstimatedPomodoros()) {
                taskList.remove(0);
                taskAdapter.notifyItemRemoved(0);
            } else {
                taskAdapter.notifyItemChanged(0);
            }
            TaskStorageUtils.saveTasks(this, taskList);
        }
    }

    private void advanceSession() {
        int currentType = Utils.retrieveCurrentSessionType(preferences);
        int nextType;

        if (currentType == Constants.WORK_SESSION) {
            int completed = preferences.getInt(Constants.SESSION_COMPLETED_COUNT_KEY, 0) + 1;
            preferences.edit().putInt(Constants.SESSION_COMPLETED_COUNT_KEY, completed).apply();
            int longBreakAfter = preferences.getInt(Constants.LONG_BREAK_AFTER_KEY, 4);
            nextType = (completed % longBreakAfter == 0)
                    ? Constants.LONG_BREAK
                    : Constants.SHORT_BREAK;
        } else {
            nextType = Constants.WORK_SESSION;
        }

        Utils.updateCurrentSessionType(preferences, nextType);
        setInitialValuesOnScreen();

        if (sessionTabs != null) {
            TabLayout.Tab tabToSelect = sessionTabs.getTabAt(
                    nextType == Constants.WORK_SESSION ? 0 :
                            nextType == Constants.SHORT_BREAK ? 1 : 2
            );
            if (tabToSelect != null) tabToSelect.select();
        }
    }

    private void retrieveDurationValues() {
        workDuration = Utils.getCurrentDurationPreferenceOf(preferences, Constants.WORK_SESSION);
        shortBreakDuration = Utils.getCurrentDurationPreferenceOf(preferences, Constants.SHORT_BREAK);
        longBreakDuration = Utils.getCurrentDurationPreferenceOf(preferences, Constants.LONG_BREAK);
    }

    private void setInitialValuesOnScreen() {
        int sessionType = Utils.retrieveCurrentSessionType(preferences);
        long duration = sessionType == Constants.WORK_SESSION ? workDuration
                : sessionType == Constants.SHORT_BREAK ? shortBreakDuration
                : longBreakDuration;
        countDownTextView.setText(Utils.formatDuration(duration));
    }

    private void loadTasksFromStorage() {
        List<Task> savedTasks = TaskStorageUtils.loadTasks(this);
        if (savedTasks != null) {
            taskList.clear();
            taskList.addAll(savedTasks);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.timer_button_main) {
            if (timerButton.isChecked()) {
                if (isPaused && remainingTime > 0) {
                    StartTimerUtils.startTimer(remainingTime, this);
                    isPaused = false;
                } else {
                    int sessionType = Utils.retrieveCurrentSessionType(preferences);
                    long duration = sessionType == Constants.WORK_SESSION ? workDuration
                            : sessionType == Constants.SHORT_BREAK ? shortBreakDuration
                            : longBreakDuration;
                    StartTimerUtils.startTimer(duration, this);
                }
            } else {
                StopTimerUtils.pauseTimer(this);
                isPaused = true;
            }
        } else if (id == R.id.settings_imageview_main) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.add_task_fab) {
            Utils.showAddTaskDialog(this, taskList, taskAdapter);
            TaskStorageUtils.saveTasks(this, taskList);
        } else if (id == R.id.distraction_button) {
            Utils.handleDistraction(preferences, this, taskList, taskAdapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(countDownReceiver, new IntentFilter(Constants.COUNTDOWN_BROADCAST), Context.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(countDownReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stoppedBroadcastReceiver);
        unregisterReceiver(countDownReceiver);
        unregisterReceiver(completedBroadcastReceiver);
        unregisterReceiver(startBroadcastReceiver);
    }
}
