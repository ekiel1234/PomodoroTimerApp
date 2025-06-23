package com.example.group9pomodoroapp1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.*;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.group9pomodoroapp1.adapter.TaskAdapter;
import com.example.group9pomodoroapp1.model.Task;
import com.example.group9pomodoroapp1.utils.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import android.widget.ImageButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ToggleButton timerButton;
    private ImageButton pauseButton;
    private TextView countDownTextView;
    private TaskAdapter taskAdapter;
    private final List<Task> taskList = new ArrayList<>();
    private SharedPreferences preferences;
    private long remainingTime = 0;
    private boolean isPaused = false;

    private TabLayout sessionTabs;
    private BroadcastReceiver stopReceiver;
    private BroadcastReceiver countdownReceiver;
    private BroadcastReceiver completeReceiver;
    private BroadcastReceiver startReceiver;

    private int taskCounter = 1;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("prefs", MODE_PRIVATE);

        countDownTextView = findViewById(R.id.countdown_textview_main);
        timerButton = findViewById(R.id.timer_button_main);
        pauseButton = findViewById(R.id.pause_button_main);
        ImageView settingsBtn = findViewById(R.id.settings_imageview_main);
        FloatingActionButton addTaskFab = findViewById(R.id.add_task_fab);
        ImageView distractionBtn = findViewById(R.id.distraction_button);
        RecyclerView recyclerView = findViewById(R.id.task_recycler_view);
        sessionTabs = findViewById(R.id.session_tab_layout);
        TextView historyButton = findViewById(R.id.task_history_button); // ✅ new line

        setupTabs();
        setupBroadcasts();

        // UI setup
        Utils.prepareSoundPool(this);
        updateTimerText();
        loadTasks();

        taskAdapter = new TaskAdapter(
                this,
                taskList,
                position -> {
                    // ✅ Save deleted task to deleted history
                    Task deletedTask = taskList.remove(position);
                    taskAdapter.notifyItemRemoved(position);
                    TaskStorageUtils.saveTasks(this, taskList);
                    TaskStorageUtils.addTaskToDeletedHistory(this, deletedTask); // ✅ new line
                },
                position -> {
                    Task doneTask = taskList.remove(position);
                    taskAdapter.notifyItemRemoved(position);
                    TaskStorageUtils.saveTasks(this, taskList);

                    // ✅ Save to completed task history
                    TaskStorageUtils.addTaskToHistory(this, doneTask);

                    new AlertDialog.Builder(this)
                            .setTitle("Great Job!")
                            .setMessage("You've completed the task \"" + doneTask.getName() + "\" successfully!")
                            .setPositiveButton("OK", null)
                            .show();
                }
        );




        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        timerButton.setOnClickListener(this);
        addTaskFab.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        distractionBtn.setOnClickListener(this);
        pauseButton.setOnClickListener(v -> {
            StopTimerUtils.pauseTimer(this);
            isPaused = true;
            timerButton.setChecked(false);
            pauseButton.setVisibility(View.GONE);
        });

        // ✅ Task History button opens history screen
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskHistoryActivity.class);
            startActivity(intent);
        });
    }




    private void setupTabs() {
        sessionTabs.addTab(sessionTabs.newTab().setText("Pomodoro"));
        sessionTabs.addTab(sessionTabs.newTab().setText("Short Break"));
        sessionTabs.addTab(sessionTabs.newTab().setText("Long Break"));

        sessionTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (timerButton.isChecked()) {
                    timerButton.setChecked(false);
                    StopTimerUtils.sessionComplete(MainActivity.this);
                }
                int type = tab.getPosition();
                Utils.updateCurrentSessionType(preferences, type);
                updateTimerText();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupBroadcasts() {
        stopReceiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent i) {
                updateTimerText();
                timerButton.setChecked(false);
                pauseButton.setVisibility(View.GONE);
                isPaused = false;
                remainingTime = 0;
            }
        };
        countdownReceiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent i) {
                String t = i.getStringExtra("countDown");
                if (t != null) {
                    countDownTextView.setText(t);
                    remainingTime = Utils.convertTimeToMillis(t);
                }
            }
        };
        completeReceiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent i) {
                handleTimerFinish();
            }
        };
        startReceiver = new BroadcastReceiver() {
            public void onReceive(Context c, Intent i) {
                timerButton.setChecked(true);
                pauseButton.setVisibility(View.VISIBLE);
            }
        };

        registerReceiver(stopReceiver, new IntentFilter(Constants.STOP_ACTION_BROADCAST), Context.RECEIVER_NOT_EXPORTED);
        registerReceiver(countdownReceiver, new IntentFilter(Constants.COUNTDOWN_BROADCAST), Context.RECEIVER_NOT_EXPORTED);
        registerReceiver(completeReceiver, new IntentFilter(Constants.COMPLETE_ACTION_BROADCAST), Context.RECEIVER_NOT_EXPORTED);
        registerReceiver(startReceiver, new IntentFilter(Constants.START_ACTION_BROADCAST), Context.RECEIVER_NOT_EXPORTED);
    }

    private void updateTimerText() {
        int session = Utils.retrieveCurrentSessionType(preferences);
        long duration = Utils.getCurrentDurationPreferenceOf(preferences, session);
        countDownTextView.setText(Utils.formatDuration(duration));
    }

    private void handleTimerFinish() {
        int type = Utils.retrieveCurrentSessionType(preferences);
        MediaPlayer mp = MediaPlayer.create(this, R.raw.bellringing);
        mp.setOnCompletionListener(MediaPlayer::release);
        mp.start();

        new AlertDialog.Builder(this)
                .setTitle("Session Complete")
                .setMessage(type == Constants.WORK_SESSION
                        ? "Time is up! Time to take a break!"
                        : "Break is over! Time to get back to work!")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> advanceSession())
                .show();

        // Only increase progress if it's a work session
        if (type == Constants.WORK_SESSION && !taskList.isEmpty()) {
            Task task = taskList.get(0);
            task.incrementProgress();
            taskAdapter.notifyItemChanged(0);
            TaskStorageUtils.saveTasks(this, taskList);
        }
    }

    private void advanceSession() {
        int current = Utils.retrieveCurrentSessionType(preferences);
        int completed = preferences.getInt(Constants.SESSION_COMPLETED_COUNT_KEY, 0);

        if (current == Constants.WORK_SESSION) {
            completed++;
            preferences.edit().putInt(Constants.SESSION_COMPLETED_COUNT_KEY, completed).apply();
            int after = preferences.getInt(Constants.LONG_BREAK_AFTER_KEY, 4);
            Utils.updateCurrentSessionType(preferences,
                    (completed % after == 0) ? Constants.LONG_BREAK : Constants.SHORT_BREAK);
        } else {
            Utils.updateCurrentSessionType(preferences, Constants.WORK_SESSION);
        }

        updateTimerText();
        TabLayout.Tab tabToSelect = sessionTabs.getTabAt(Utils.retrieveCurrentSessionType(preferences));
        if (tabToSelect != null) tabToSelect.select();
    }

    private void loadTasks() {
        List<Task> saved = TaskStorageUtils.loadTasks(this);
        if (saved != null) {
            taskList.clear();
            taskList.addAll(saved);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.timer_button_main) {
            if (timerButton.isChecked()) {
                pauseButton.setVisibility(View.VISIBLE);
                long time = isPaused && remainingTime > 0
                        ? remainingTime
                        : Utils.getCurrentDurationPreferenceOf(preferences,
                        Utils.retrieveCurrentSessionType(preferences));

                Intent startIntent = new Intent(this, CountDownTimerService.class);
                startIntent.setAction(isPaused ? Constants.ACTION_RESUME : Constants.ACTION_START);
                startIntent.putExtra("duration", time);
                startForegroundService(startIntent);

                isPaused = false;

            } else {
                showStopConfirmDialog();
            }

        } else if (id == R.id.pause_button_main) {
            if (isPaused) {
                Intent resumeIntent = new Intent(this, CountDownTimerService.class);
                resumeIntent.setAction(Constants.ACTION_RESUME);
                startService(resumeIntent);
                isPaused = false;
                timerButton.setChecked(true);
            } else {
                Intent pauseIntent = new Intent(this, CountDownTimerService.class);
                pauseIntent.setAction(Constants.ACTION_PAUSE);
                startService(pauseIntent);
                isPaused = true;
                timerButton.setChecked(false);
                pauseButton.setVisibility(View.VISIBLE);
            }

        } else if (id == R.id.settings_imageview_main) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.add_task_fab) {
            Utils.showAddTaskDialog(this, taskList, taskAdapter);
            TaskStorageUtils.saveTasks(this, taskList);

        } else if (id == R.id.distraction_button) {
            if (!timerButton.isChecked() && !isPaused) {
                new AlertDialog.Builder(this)
                        .setTitle("Can't Distract Yet")
                        .setMessage("You haven't started the timer.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }
            if (!taskList.isEmpty()) {
                taskList.get(0).incrementDistractionCount();
                TaskStorageUtils.saveTasks(this, taskList);
            }

            new AlertDialog.Builder(this)
                    .setTitle("Distraction Logged")
                    .setMessage("Stay focused and try again!")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }


    private void showStopConfirmDialog() {
        int type = Utils.retrieveCurrentSessionType(preferences);
        String msg = (type == Constants.WORK_SESSION)
                ? "Are you sure you want to stop the session? This Pomodoro will not count."
                : "Do you want to skip the break and go back to work?";

        new AlertDialog.Builder(this)
                .setTitle("Confirm Stop")
                .setMessage(msg)
                .setPositiveButton("Yes", (d, w) -> {
                    StopTimerUtils.sessionCancel(this); // just stops the timer
                    pauseButton.setVisibility(View.GONE);

                    int currentTab = sessionTabs.getSelectedTabPosition();
                    Utils.updateCurrentSessionType(preferences, currentTab);
                    updateTimerText();

                    TabLayout.Tab tab = sessionTabs.getTabAt(currentTab);
                    if (tab != null) tab.select();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    timerButton.setChecked(true); // ✅ keep it showing as "Stop"
                })
                .show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stopReceiver);
        unregisterReceiver(countdownReceiver);
        unregisterReceiver(completeReceiver);
        unregisterReceiver(startReceiver);
    }
}
