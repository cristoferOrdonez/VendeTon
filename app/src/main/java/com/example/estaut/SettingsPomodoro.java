package com.example.estaut;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.estaut.db.DbPomodoro;
import com.example.estaut.entities.TimerPomodoro;

import java.util.Locale;

public class SettingsPomodoro extends AppCompatActivity {

    //atributos
    private static final int MIN_VALUE_NUMBER_PICKER_MINUTES_POMODORO = 5;
    private static final int MAX_VALUE_NUMBER_PICKER_MINUTES_POMODORO = 60;
    private static final int MIN_VALUE_NUMBER_PICKER_MINUTES_POMODORO_BREAK = 1;
    private static final int MAX_VALUE_NUMBER_PICKER_MINUTES_POMODORO_BREAK = 60;
    private static final int MIN_VALUE_NUMBER_PICKER_SECONDS_POMODORO_AND_BREAK = 0;
    private static final int MAX_VALUE_NUMBER_PICKER_SECONDS_POMODORO_AND_BREAK = 59;
    private static final boolean ACTION_OF_SAVE = true;
    private static final boolean ACTION_OF_RESTORE = false;
    private static final String MESSAGE_CONFIRMATION_EXIT = "Do you want to exit without saving?";
    private static final String MESSAGE_CONFIRMATION_SAVE = "Do you want to save the settings?";
    private static final String MESSAGE_CONFIRMATION_RESTORE = "Do you want to restore the settings?";
    private static final String INDICATION_TO_CHANGE_TIME_POMODORO = "Select a time for the Pomodoro.";
    private static final String INDICATION_TO_CHANGE_TIME_POMODORO_BREAK = "Select a time for the Break.";
    private ImageButton buttonBack, buttonSave;
    private LinearLayout timePomodoroButton, timePomodoroBreakButton;
    private TextView textViewTimePomodoroName, textViewTimePomodoro, textViewTimePomodoroBreakName, textViewTimePomodoroBreak, textViewVolumeName;
    private View divider1, divider2, divider3;
    private long timePomodoro, timePomodoroBreak;
    private double volumeAlarm;
    private SeekBar seekBarVolume;
    private Button buttonRestoreSettings;
    private MediaPlayer soundExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_pomodoro);

        soundExample = MediaPlayer.create(this, R.raw.ping_pomodoro);

        seekBarVolume = findViewById(R.id.seekBarVolume);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if(soundExample.isPlaying()) {
                    soundExample.pause();
                    soundExample.seekTo(0);
                }

                double volume = (double)seekBar.getProgress() / 100;
                soundExample.setVolume((float)volume, (float)volume);
                soundExample.start();
                volumeAlarm = volume;

            }
        });

        textViewTimePomodoroName = findViewById(R.id.textViewPomodoroName);
        textViewTimePomodoroBreakName = findViewById(R.id.textViewPomodoroBreakName);

        textViewTimePomodoro = findViewById(R.id.textViewPomodoroTime);
        textViewTimePomodoroBreak = findViewById(R.id.textViewPomodoroBreakTime);
        textViewVolumeName = findViewById(R.id.textViewVolume);

        setPreconfiguration();
        updateTextViewTime(true);
        updateTextViewTime(false);
        seekBarVolume.setProgress((int)(volumeAlarm * 100));

        timePomodoroButton = findViewById(R.id.timePomodoroButton);
        timePomodoroButton.setOnClickListener(i -> showDialogTime(INDICATION_TO_CHANGE_TIME_POMODORO, MIN_VALUE_NUMBER_PICKER_MINUTES_POMODORO, MAX_VALUE_NUMBER_PICKER_MINUTES_POMODORO, true));

        timePomodoroBreakButton = findViewById(R.id.timeBreakPomodoroButton);
        timePomodoroBreakButton.setOnClickListener(i -> showDialogTime(INDICATION_TO_CHANGE_TIME_POMODORO_BREAK, MIN_VALUE_NUMBER_PICKER_MINUTES_POMODORO_BREAK, MAX_VALUE_NUMBER_PICKER_MINUTES_POMODORO_BREAK, false));

        buttonBack = findViewById(R.id.imageButtonBackSettingsPomodoro);
        buttonBack.setOnClickListener(i -> showConfirmationDialog(MESSAGE_CONFIRMATION_EXIT, null));

        buttonSave = findViewById(R.id.imageButtonSaveSettingsPomodoro);
        buttonSave.setOnClickListener(i -> showConfirmationDialog(MESSAGE_CONFIRMATION_SAVE, ACTION_OF_SAVE));

        buttonRestoreSettings = findViewById(R.id.buttonRestoreSettings);
        buttonRestoreSettings.setOnClickListener(i -> showConfirmationDialog(MESSAGE_CONFIRMATION_RESTORE, ACTION_OF_RESTORE));

        divider1 = findViewById(R.id.divider1);
        divider2 = findViewById(R.id.divider2);
        divider3 = findViewById(R.id.divider3);

        setLayoutParams();

    }

    private void setPreconfiguration(){

        DbPomodoro dbPomodoro = new DbPomodoro(this);

        TimerPomodoro settingsInformation = dbPomodoro.showSettingsTimerPomodoro(Pomodoro.ID_INFORMATION_TIMER);
        timePomodoro = settingsInformation.getTimePomodoro();
        timePomodoroBreak = settingsInformation.getTimeBreak();
        volumeAlarm = settingsInformation.getVolume();

    }

    private void backToPomodoro(){
        startActivity(new Intent(this, Pomodoro.class));
        finish();
    }

    private void showConfirmationDialog(String message, Boolean actionAccept){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alert_dialog_confirmation_pomodo_settings, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());

        dialog.getWindow().setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        dialog.getWindow().setLayout((int)(screenWidth * 0.92), ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView textViewIndication = view.findViewById(R.id.textViewIndication);
        textViewIndication.setText(message);

        LinearLayout.LayoutParams newLayoutTextViewIndication = (LinearLayout.LayoutParams) textViewIndication.getLayoutParams();
        newLayoutTextViewIndication.height = screenWidth / 18;
        int marginTextViewIndication = screenWidth / 20;
        newLayoutTextViewIndication.setMargins(marginTextViewIndication, marginTextViewIndication, marginTextViewIndication, marginTextViewIndication);


        Button buttonCancel = view.findViewById(R.id.buttonCancelAlertDialogPomodoroOrBreak);
        Button buttonAccept = view.findViewById(R.id.buttonAcceptAlertDialogPomodoroOrBreak);
        LinearLayout.LayoutParams newLayoutButton = (LinearLayout.LayoutParams) buttonAccept.getLayoutParams();
        newLayoutButton.height = screenWidth / 7;
        int marginButton = marginTextViewIndication * 2;
        newLayoutButton.setMargins(marginButton, 0, marginButton, marginButton / 3);
        buttonAccept.setLayoutParams(newLayoutButton);
        buttonCancel.setLayoutParams(newLayoutButton);
        buttonAccept.setPadding(0, newLayoutButton.height / 3, 0, newLayoutButton.height / 3);
        buttonCancel.setPadding(0, newLayoutButton.height / 3, 0, newLayoutButton.height / 3);

        buttonCancel.setOnClickListener(i -> dialog.dismiss());
        buttonAccept.setOnClickListener(i -> {

            if(actionAccept == null);
            else if(actionAccept)
                saveSettings();
            else
                restoreSettings();

            backToPomodoro();
            dialog.dismiss();

        });

    }

    private void showDialogTime(String indication, int minValueMinutes, int maxValueMinutes, boolean pomodoroOrBreak){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);

        long time;

        if(pomodoroOrBreak)
            time = timePomodoro;
        else
            time = timePomodoroBreak;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.alert_dialog_time_pomodoro_and_break, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.gravity = Gravity.BOTTOM;

        dialog.getWindow().setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        dialog.getWindow().setLayout((int)(screenWidth * 0.92), ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView min = view.findViewById(R.id.textViewMinutes);
        TextView sec = view.findViewById(R.id.textViewSeconds);
        TextView textViewIndication = view.findViewById(R.id.textViewIndication);
        textViewIndication.setText(indication);

        LinearLayout.LayoutParams newLayoutTextViewIndication = (LinearLayout.LayoutParams) textViewIndication.getLayoutParams();
        newLayoutTextViewIndication.height = screenWidth / 18;
        int marginTextViewIndication = screenWidth / 20;
        newLayoutTextViewIndication.setMargins(marginTextViewIndication, marginTextViewIndication, marginTextViewIndication, marginTextViewIndication);

        NumberPicker numberPickerMinutes = view.findViewById(R.id.numberPickerMinutes);
        NumberPicker numberPickerSeconds = view.findViewById(R.id.numberPickerSeconds);
        Button buttonCancel = view.findViewById(R.id.buttonCancelAlertDialogPomodoroOrBreak);
        Button buttonAccept = view.findViewById(R.id.buttonAcceptAlertDialogPomodoroOrBreak);

        LinearLayout.LayoutParams newLayoutNumberPickerMinutes = (LinearLayout.LayoutParams) numberPickerMinutes.getLayoutParams();
        LinearLayout.LayoutParams newLayoutNumberPickerSeconds = (LinearLayout.LayoutParams) numberPickerSeconds.getLayoutParams();
        newLayoutNumberPickerMinutes.height = screenWidth / 3;
        newLayoutNumberPickerSeconds.height = screenWidth / 3;

        int marginNumberPicker = marginTextViewIndication * 2;
        newLayoutNumberPickerMinutes.setMargins(marginNumberPicker, 0, 0, 0);

        numberPickerMinutes.setLayoutParams(newLayoutNumberPickerMinutes);

        newLayoutNumberPickerSeconds.setMargins(0, 0, marginNumberPicker, 0);

        numberPickerSeconds.setLayoutParams(newLayoutNumberPickerSeconds);

        LinearLayout.LayoutParams newLayoutMinSec = (LinearLayout.LayoutParams) min.getLayoutParams();
        newLayoutMinSec.height = newLayoutTextViewIndication.height;
        min.setLayoutParams(newLayoutMinSec);
        min.setPadding(marginNumberPicker, 0, 0, 0);
        sec.setLayoutParams(newLayoutMinSec);
        sec.setPadding(0, 0, marginNumberPicker, 0);

        LinearLayout.LayoutParams newLayoutButton = (LinearLayout.LayoutParams) buttonAccept.getLayoutParams();
        newLayoutButton.height = screenWidth / 7;
        int marginButton = marginNumberPicker;
        newLayoutButton.setMargins(marginButton, marginButton, marginButton, marginButton / 3);
        buttonAccept.setLayoutParams(newLayoutButton);
        buttonCancel.setLayoutParams(newLayoutButton);
        buttonAccept.setPadding(0, newLayoutButton.height / 3, 0, newLayoutButton.height / 3);
        buttonCancel.setPadding(0, newLayoutButton.height / 3, 0, newLayoutButton.height / 3);


        numberPickerMinutes.setMinValue(minValueMinutes);
        numberPickerMinutes.setMaxValue(maxValueMinutes);
        numberPickerMinutes.setFormatter(i -> String.format("%02d", i));

        numberPickerSeconds.setMinValue(MIN_VALUE_NUMBER_PICKER_SECONDS_POMODORO_AND_BREAK);
        numberPickerSeconds.setMaxValue(MAX_VALUE_NUMBER_PICKER_SECONDS_POMODORO_AND_BREAK);
        numberPickerSeconds.setFormatter(i -> String.format("%02d", i));

        numberPickerMinutes.setValue((int)(time / 60000));
        numberPickerSeconds.setValue((int)((time / 1000) % 60));

        if(numberPickerMinutes.getValue() == 60)
            numberPickerSeconds.setMaxValue(MIN_VALUE_NUMBER_PICKER_SECONDS_POMODORO_AND_BREAK);

        numberPickerMinutes.setOnValueChangedListener((a, b, c) -> {
            if(numberPickerMinutes.getValue() == maxValueMinutes)
                numberPickerSeconds.setMaxValue(MIN_VALUE_NUMBER_PICKER_SECONDS_POMODORO_AND_BREAK);
            else if(numberPickerSeconds.getMaxValue() == MIN_VALUE_NUMBER_PICKER_SECONDS_POMODORO_AND_BREAK)
                numberPickerSeconds.setMaxValue(MAX_VALUE_NUMBER_PICKER_SECONDS_POMODORO_AND_BREAK);
        });

        buttonCancel.setOnClickListener(i -> dialog.dismiss());
        buttonAccept.setOnClickListener(i -> {
            if(pomodoroOrBreak)
                timePomodoro = numberPickerMinutes.getValue() * 60000 + numberPickerSeconds.getValue() * 1000;
            else
                timePomodoroBreak = numberPickerMinutes.getValue() * 60000 + numberPickerSeconds.getValue() * 1000;
            dialog.dismiss();
            updateTextViewTime(pomodoroOrBreak);
        });

    }

    private void saveSettings(){

        DbPomodoro dbPomodoro = new DbPomodoro(this);
        dbPomodoro.updateSettingsPomodoro(Pomodoro.ID_INFORMATION_TIMER, timePomodoro, timePomodoroBreak, volumeAlarm);
        backToPomodoro();

    }

    private void restoreSettings(){

        DbPomodoro dbPomodoro = new DbPomodoro(this);
        dbPomodoro.updateSettingsPomodoro(Pomodoro.ID_INFORMATION_TIMER, Pomodoro.POINT_OF_START_TIMER_DEFAULT, Pomodoro.TIME_BREAK_TIMER_DEFAULT, Pomodoro.VOLUME_ALARMS_DEFAULT);
        backToPomodoro();

    }

    private void updateTextViewTime(boolean pomodoroOrBreak){

        long time;
        TextView text;

        if(pomodoroOrBreak) {
            time = timePomodoro;
            text = textViewTimePomodoro;
        } else {
            time = timePomodoroBreak;
            text = textViewTimePomodoroBreak;
        }

        int minutes = (int) (time / 1000) / 60;
        int seconds = (int) (time / 1000) % 60;

        String textTimeFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        text.setText(textTimeFormated);

    }

    private void setLayoutParams(){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);

        //LayoutParams ButtonBack
        ConstraintLayout.LayoutParams newLayoutButtonBack = (ConstraintLayout.LayoutParams) buttonBack.getLayoutParams();
        newLayoutButtonBack.width = screenWidth / 10;
        newLayoutButtonBack.height = screenWidth / 10;
        int marginButtonBack = screenWidth / 30;
        newLayoutButtonBack.setMargins(marginButtonBack, (int)(marginButtonBack * 1.7), 0, (int)(marginButtonBack * 1.7));
        buttonBack.setLayoutParams(newLayoutButtonBack);

        //LayoutParams ButtonSettings
        ConstraintLayout.LayoutParams newLayoutButtonSave = (ConstraintLayout.LayoutParams) buttonSave.getLayoutParams();
        newLayoutButtonSave.width = screenWidth / 10;
        newLayoutButtonSave.height = screenWidth / 10;
        int marginButtonSave = screenWidth / 30;
        newLayoutButtonSave.setMargins(0, (int)(marginButtonSave * 1.7), marginButtonSave, (int)(marginButtonSave * 1.7));
        buttonSave.setLayoutParams(newLayoutButtonSave);

        //LayoutParams textViewTimeName
        LinearLayout.LayoutParams newLayoutTextViewTimeName = (LinearLayout.LayoutParams) textViewTimePomodoroBreakName.getLayoutParams();
        newLayoutTextViewTimeName.width = screenWidth / 2;
        newLayoutTextViewTimeName.height = screenWidth / 16;
        int marginTextViewTimeName = screenWidth / 18;
        newLayoutTextViewTimeName.setMargins(marginTextViewTimeName, marginTextViewTimeName, marginTextViewTimeName, marginTextViewTimeName);

        textViewTimePomodoroName.setLayoutParams(newLayoutTextViewTimeName);
        textViewTimePomodoroBreakName.setLayoutParams(newLayoutTextViewTimeName);

        //LayoutParams textViewTime
        LinearLayout.LayoutParams newLayoutTextViewTime = (LinearLayout.LayoutParams) textViewTimePomodoro.getLayoutParams();
        newLayoutTextViewTime.width = screenWidth / 2;
        newLayoutTextViewTime.height = screenWidth / 20;
        int marginTextViewTime = screenWidth / 7;
        newLayoutTextViewTime.setMargins(0, 0, marginTextViewTime, 0);
        textViewTimePomodoro.setLayoutParams(newLayoutTextViewTime);
        textViewTimePomodoroBreak.setLayoutParams(newLayoutTextViewTime);

        //LayoutParams textViewVolumeName
        LinearLayout.LayoutParams newLayoutTextViewVolumeName = (LinearLayout.LayoutParams) textViewVolumeName.getLayoutParams();
        newLayoutTextViewVolumeName.height = screenWidth / 16;
        newLayoutTextViewVolumeName.width = screenWidth / 4;
        int marginTextViewVolumeName = marginTextViewTimeName;
        newLayoutTextViewVolumeName.setMargins(marginTextViewVolumeName, marginTextViewVolumeName, marginTextViewVolumeName, marginTextViewVolumeName);
        textViewVolumeName.setLayoutParams(newLayoutTextViewVolumeName);

        //LayoutParams seekBarVolume
        LinearLayout.LayoutParams newLayoutSeekBarVolume = (LinearLayout.LayoutParams) seekBarVolume.getLayoutParams();
        newLayoutSeekBarVolume.height = newLayoutTextViewVolumeName.height;
        newLayoutSeekBarVolume.width = screenWidth - newLayoutTextViewVolumeName.width;
        int marginSeekBarVolume = marginTextViewVolumeName;
        newLayoutSeekBarVolume.setMargins(0, 0, marginSeekBarVolume, 0);
        seekBarVolume.setLayoutParams(newLayoutSeekBarVolume);

        //LayoutParams dividers
        LinearLayout.LayoutParams newLayoutDividers = (LinearLayout.LayoutParams) divider1.getLayoutParams();
        int marginDivider = screenWidth / 16;
        newLayoutDividers.setMargins(marginDivider, 0, marginDivider, 0);
        divider1.setLayoutParams(newLayoutDividers);
        divider2.setLayoutParams(newLayoutDividers);
        divider3.setLayoutParams(newLayoutDividers);

        //LayoutParams buttonRestoreSettings
        LinearLayout.LayoutParams newLayoutButtonRestoreSettings = (LinearLayout.LayoutParams) buttonRestoreSettings.getLayoutParams();
        newLayoutButtonRestoreSettings.height = (int)(screenWidth * 0.12);
        newLayoutButtonRestoreSettings.width = (int)(screenWidth * 0.6);
        int marginButtonRestoreSettings = newLayoutButtonRestoreSettings.height;
        int paddingButtonRestoreSettings = newLayoutButtonRestoreSettings.height / 4;
        newLayoutButtonRestoreSettings.setMargins(0, marginButtonRestoreSettings, 0, 0);
        buttonRestoreSettings.setLayoutParams(newLayoutButtonRestoreSettings);
        buttonRestoreSettings.setPadding(paddingButtonRestoreSettings, paddingButtonRestoreSettings, paddingButtonRestoreSettings, paddingButtonRestoreSettings);

    }

}