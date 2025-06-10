package com.example.group9pomodoroapp1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.group9pomodoroapp1.utils.Constants;
import com.example.group9pomodoroapp1.utils.VolumeSeekBarUtils;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    private Spinner workDurationSpinner;
    private Spinner shortBreakDurationSpinner;
    private Spinner longBreakDurationSpinner;
    private Spinner longBreakAfterSpinner;
    private SeekBar tickingSeekBar;
    private SeekBar ringingSeekBar;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = getSharedPreferences("prefs", MODE_PRIVATE);

        workDurationSpinner = findViewById(R.id.work_duration_spinner);
        shortBreakDurationSpinner = findViewById(R.id.short_break_duration_spinner);
        longBreakDurationSpinner = findViewById(R.id.long_break_duration_spinner);
        longBreakAfterSpinner = findViewById(R.id.long_break_after_spinner);
        tickingSeekBar = findViewById(R.id.ticking_seek_bar);
        ringingSeekBar = findViewById(R.id.ringing_seek_bar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.work_duration_array, // Make sure this exists in res/values/strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        workDurationSpinner.setAdapter(adapter);
        shortBreakDurationSpinner.setAdapter(adapter);
        longBreakDurationSpinner.setAdapter(adapter);
        longBreakAfterSpinner.setAdapter(adapter);

        workDurationSpinner.setOnItemSelectedListener(this);
        shortBreakDurationSpinner.setOnItemSelectedListener(this);
        longBreakDurationSpinner.setOnItemSelectedListener(this);
        longBreakAfterSpinner.setOnItemSelectedListener(this);

        tickingSeekBar.setOnSeekBarChangeListener(this);
        ringingSeekBar.setOnSeekBarChangeListener(this);

        restoreSpinnerValues();
        restoreSeekBarValues();
    }

    private void restoreSpinnerValues() {
        workDurationSpinner.setSelection(preferences.getInt(Constants.WORK_DURATION_KEY, 1));
        shortBreakDurationSpinner.setSelection(preferences.getInt(Constants.SHORT_BREAK_DURATION_KEY, 1));
        longBreakDurationSpinner.setSelection(preferences.getInt(Constants.LONG_BREAK_DURATION_KEY, 1));
        longBreakAfterSpinner.setSelection(preferences.getInt(Constants.LONG_BREAK_AFTER_KEY, 2));
    }

    private void restoreSeekBarValues() {
        int tickingVolume = preferences.getInt(Constants.TICKING_VOLUME_LEVEL_KEY, VolumeSeekBarUtils.maxVolume - 1);
        int ringingVolume = preferences.getInt(Constants.RINGING_VOLUME_LEVEL_KEY, VolumeSeekBarUtils.maxVolume - 1);

        tickingSeekBar.setProgress(tickingVolume);
        ringingSeekBar.setProgress(ringingVolume);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SharedPreferences.Editor editor = preferences.edit();
        int viewId = parent.getId();

        if (viewId == R.id.work_duration_spinner) {
            editor.putInt(Constants.WORK_DURATION_KEY, position);
        } else if (viewId == R.id.short_break_duration_spinner) {
            editor.putInt(Constants.SHORT_BREAK_DURATION_KEY, position);
        } else if (viewId == R.id.long_break_duration_spinner) {
            editor.putInt(Constants.LONG_BREAK_DURATION_KEY, position);
        } else if (viewId == R.id.long_break_after_spinner) {
            editor.putInt(Constants.LONG_BREAK_AFTER_KEY, position);
        }

        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        SharedPreferences.Editor editor = preferences.edit();
        int viewId = seekBar.getId();

        if (viewId == R.id.ticking_seek_bar) {
            editor.putInt(Constants.TICKING_VOLUME_LEVEL_KEY, progress);
        } else if (viewId == R.id.ringing_seek_bar) {
            editor.putInt(Constants.RINGING_VOLUME_LEVEL_KEY, progress);
        }

        editor.apply();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
