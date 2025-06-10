package com.example.group9pomodoroapp1.utils;

public class Constants {
    public static final int WORK_SESSION = 0;
    public static final int SHORT_BREAK = 1;
    public static final int LONG_BREAK = 2;

    // Notification
    public static final String NOTIFICATION_CHANNEL_ID = "pomodoro_notification_channel";
    public static final int TASK_INFORMATION_NOTIFICATION_ID = 10;

    // Broadcasts
    public static final String COUNTDOWN_BROADCAST = "com.group9.countdown";
    public static final String STOP_ACTION_BROADCAST = "com.group9.stop.action";
    public static final String COMPLETE_ACTION_BROADCAST = "com.group9.complete.action";
    public static final String START_ACTION_BROADCAST = "com.group9.start.action";

    // Intent Actions
    public static final String INTENT_NAME_ACTION = "action";
    public static final String ACTION_START_TIMER = "start";
    public static final String ACTION_COMPLETE_TIMER = "complete";
    public static final String ACTION_CANCEL_TIMER = "cancel";
    public static final String ACTION_START_SHORT_BREAK = "short";
    public static final String ACTION_START_LONG_BREAK = "long";

    public static final long TIME_INTERVAL = 1000;

    // SharedPreferences Keys
    public static final String WORK_DURATION_KEY = "WORK_DURATION";
    public static final String SHORT_BREAK_DURATION_KEY = "SHORT_BREAK_DURATION";
    public static final String LONG_BREAK_DURATION_KEY = "LONG_BREAK_DURATION";
    public static final String LONG_BREAK_AFTER_KEY = "LONG_BREAK_AFTER_DURATION";
    public static final String SESSION_COMPLETED_COUNT_KEY = "SESSION_COMPLETED_COUNT";
    public static final String TASK_PROGRESS_COUNT_KEY = "TASK_PROGRESS_COUNT";
    public static final String TICKING_VOLUME_LEVEL_KEY = "TICKING_VOLUME_LEVEL";
    public static final String RINGING_VOLUME_LEVEL_KEY = "RINGING_VOLUME_LEVEL";
    public static final String TASK_MESSAGE = "TASK_MESSAGE";
}