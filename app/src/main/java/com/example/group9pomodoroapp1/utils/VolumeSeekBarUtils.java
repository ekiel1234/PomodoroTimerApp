package com.example.group9pomodoroapp1.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.widget.SeekBar;

import com.example.group9pomodoroapp1.R;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class VolumeSeekBarUtils {

    public static int maxVolume;
    public static float floatTickingVolumeLevel;
    public static float floatRingingVolumeLevel;

    public static SeekBar initializeSeekBar(Activity activity, SeekBar seekBar) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        int progress;
        if (seekBar.getId() == R.id.ticking_seek_bar) {
            progress = preferences.getInt(Constants.TICKING_VOLUME_LEVEL_KEY, maxVolume - 1);
        } else {
            progress = preferences.getInt(Constants.RINGING_VOLUME_LEVEL_KEY, maxVolume - 1);
        }

        seekBar.setMax(maxVolume);
        seekBar.setProgress(progress);

        return seekBar;
    }

    public static float convertToFloat(int value, int max) {
        return (float) (1 - (Math.log(max - value) / Math.log(max)));
    }
}