package com.example.thousandschnapsen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbGameTurn extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "schnapsen.db";
    public static final String TABLE_NAME5 = "game_turn";
    public static final String COL_id_turn = "id_game_turn";
    public static final String COL_who_played = "who_plyed";
    public static final String game_not_played = "game_not_played"; /*bomba (po 60)*/
    public static final String COL_bided_res = "bided_result";


    public DbGameTurn(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME5 +" (id_game_turn INTEGER PRIMARY KEY AUTOINCREMENT," +
                "who_plyed INTEGER," +
                "game_not_played BOOLEAN," +
                "bided_result INTEGER," +
                "FOREIGN KEY (id_game_turn) REFERENCES game_turn_results (id_game_turn))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME5);
        onCreate(db);
    }
}
