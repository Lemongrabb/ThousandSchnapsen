package com.example.thousandschnapsen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbGameplay extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "schnapsen.db";
    public static final String TABLE_NAME3 = "gameplay";
    public static final String COL_id_gameplay = "id_gameplay";
    public static final String COL_gameplay = "date";


    public DbGameplay(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME3 +" (id_gameplay INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date DATETIME)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME3);
        onCreate(db);
    }
}
