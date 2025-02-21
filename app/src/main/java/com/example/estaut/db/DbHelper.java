package com.example.estaut.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    //atributos
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data.db";
    protected static final String TABLE_SETTINGS_POMODORO = "t_settings_pomodoro";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_SETTINGS_POMODORO + "(" +
                "idPomodoroSettings INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "timeTimerPomodoroSettings BIGINT NOT NULL," +
                "timeTimerBreakPomodoroSettings BIGINT NOT NULL," +
                "volumePomodoroSettings REAL NOT NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_SETTINGS_POMODORO);

    }
}
