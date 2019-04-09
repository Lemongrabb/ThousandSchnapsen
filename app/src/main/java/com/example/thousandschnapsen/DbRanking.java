package com.example.thousandschnapsen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbRanking extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "schnapsen.db";
    public static final String TABLE_NAME4 = "ranking";
    public static final String COL_id_gameplay = "id_ranking";
    public static final String COL_r_id_nickname = "id_nickname";
    public static final String COL_r_score = "id_score";
    public static final String COL_r_win = "id_win";
    public static final String COL_gameplay = "id_gameplay";


    public DbRanking(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME4 +" (id_ranking INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_nickname INTEGER," +
                "id_score INTEGER," +
                "id_gameplay INTEGER," +
                "is_win BOOLEAN," +
                "id_gameplay INTEGER," +
                "FOREIGN KEY (id_nicknames) REFERENCES nicknames (id_nicknames)"+
                "FOREIGN KEY (id_score) REFERENCES score (id_score)"+
                "FOREIGN KEY (id_gameplay) REFERENCES gameplay (id_gameplay))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME4);
        onCreate(db);
    }
}
