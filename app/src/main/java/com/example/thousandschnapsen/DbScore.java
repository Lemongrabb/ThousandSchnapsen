package com.example.thousandschnapsen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbScore extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "schnapsen.db";
    public static final String TABLE_NAME2 = "score";
    public static final String COL_id_score = "id_score";
    public static final String COL_score = "score";


    public DbScore(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME2 +" (id_scroe INTEGER PRIMARY KEY AUTOINCREMENT," +
                "score TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2);
        onCreate(db);
    }
}
