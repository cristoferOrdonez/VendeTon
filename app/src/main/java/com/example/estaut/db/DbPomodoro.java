package com.example.estaut.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.estaut.entities.TimerPomodoro;

public class DbPomodoro extends DbHelper {

    private Context context;

    public DbPomodoro(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long setTimerDefault(long timePomodoro, long timeBreak, double volume) {
        long id = 0;
        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("timeTimerPomodoroSettings", timePomodoro);
            values.put("timeTimerBreakPomodoroSettings", timeBreak);
            values.put("volumePomodoroSettings", volume);

            id = db.insert(TABLE_SETTINGS_POMODORO, null, values);

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;

    }

    public boolean updateSettingsPomodoro(long id, long timePomodoro, long timeBreak, double volume) {
        boolean correct;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("timeTimerPomodoroSettings", timePomodoro);
            values.put("timeTimerBreakPomodoroSettings", timeBreak);
            values.put("volumePomodoroSettings", volume);

            int rowsAffected = db.update(TABLE_SETTINGS_POMODORO, values, "idPomodoroSettings = ?", new String[]{String.valueOf(id)});

            correct = (rowsAffected > 0);

        } catch (Exception e) {
            e.printStackTrace();
            correct = false;
        } finally {
            db.close();
        }

        return correct;
    }

    public TimerPomodoro showSettingsTimerPomodoro(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        TimerPomodoro timerInformation = null;
        Cursor cursorTimerPomodoro;

        cursorTimerPomodoro = db.rawQuery("SELECT * FROM " + TABLE_SETTINGS_POMODORO + " WHERE idPomodoroSettings = ? LIMIT 1", new String[]{String.valueOf(id)});

        if (cursorTimerPomodoro.moveToFirst()) {
            timerInformation = new TimerPomodoro(cursorTimerPomodoro.getLong(1), cursorTimerPomodoro.getLong(2), cursorTimerPomodoro.getDouble(3));
        }
        cursorTimerPomodoro.close();

        db.close();
        return timerInformation;
    }

}
