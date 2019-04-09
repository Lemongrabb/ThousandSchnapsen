package com.example.thousandschnapsen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbGameTurnRes extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "schnapsen.db";
    public static final String TABLE_NAME5 = "game_turn_results";
    public static final String COL_id_turn_res = "id_game_turn_result";
    public static final String COL_id_turn = "id_game_turn";
    public static final String COL_id_nick = "id_nickname";
    public static final String COL_result = "result";
    public static final String COL_diamond = "diamond_report";
    public static final String COL_hearts = "heart_report";
    public static final String COL_spades = "spades_report";
    public static final String COL_clubs = "clubs_report";

    public DbGameTurnRes(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME5 +" (id_game_turn_result INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_game_turn INTEGER," +
                "id_nickname INTEGER," +
                "result INTEGER," +
                "diamonds_report BOOLEAN," +
                "hearts_report BOOLEAN," +
                "spades_report BOOLEAN," +
                "clubs_report BOOLEAN)" +
                "FOREIGN KEY (id_game_turn) REFERENCES game_turn (id_game_turn)"+
                "FOREIGN KEY (id_nickname) REFERENCES nickname (id_nicknames))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME5);
        onCreate(db);
    }
}
