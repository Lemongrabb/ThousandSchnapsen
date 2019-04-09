package com.example.thousandschnapsen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbNicknames extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "schnapsen.db";
    public static final String TABLE_NAME1 = "nicknames";
    public static final String COL_id_nicknames = "id_nicknames";
    public static final String COL_nickname = "nickname";
    public static final String COL_model = "model";


    public DbNicknames(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME1 +" (id_nicknames INTEGER PRIMARY KEY AUTOINCREMENT," +
                "phone_model TEXT," +
                "nickname TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME1);
        onCreate(db);
    }
}
