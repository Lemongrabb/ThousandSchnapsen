package com.example.thousandschnapsen.DbSchnapsen;

import java.util.ArrayList;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class Game_Turn_ResultsDao extends DbManager {
    private static final String TAG = "Game_Turn_ResultsDao";

    protected static SQLiteDatabase database;
    protected static DbManager mDbManager;
    protected static  String[] allColumns = DbSchema.Table_Game_Turn_Results.allColumns;


    protected Game_Turn_ResultsDao() {
    }

    protected static void database_open() throws SQLException {
        mDbManager = DbManager.getsInstance();
        database = mDbManager.getDatabase();
    }

    protected static void database_close() {
        mDbManager = DbManager.getsInstance();
        mDbManager.close();
    }

    public static Game_Turn_Results loadRecordById(int mid_game_turn_result)  { 
        database_open();
        Cursor cursor = database.query(DbSchema.Table_Game_Turn_Results.TABLE_NAME,allColumns,  "id_game_turn_result = ?" , new String[] { String.valueOf(mid_game_turn_result) } , null, null, null,null);

        if (cursor != null)
            cursor.moveToFirst();

        Game_Turn_Results game_turn_results = new Game_Turn_Results();
        game_turn_results = cursorToGame_Turn_Results(cursor);

        cursor.close();
        database_close();

        return game_turn_results;
    }

    public static ArrayList<Game_Turn_Results> loadAllRecords() {
        ArrayList<Game_Turn_Results> game_turn_resultsList = new ArrayList<Game_Turn_Results>();
        database_open();

        Cursor cursor = database.query(
                DbSchema.Table_Game_Turn_Results.TABLE_NAME,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Game_Turn_Results game_turn_results = cursorToGame_Turn_Results(cursor);
            game_turn_resultsList.add(game_turn_results);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return game_turn_resultsList;
    }

    // Please always use the typed column names (Table_Game_Turn_Results) when passing arguments.
    // Example: Table_Game_Turn_Results.Column_Name
    public static ArrayList<Game_Turn_Results> loadAllRecords(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        ArrayList<Game_Turn_Results> game_turn_resultsList = new ArrayList<Game_Turn_Results>();
        database_open();

        if(TextUtils.isEmpty(selection)){
            selection = null;
            selectionArgs = null;
        }

        Cursor cursor = database.query(
                DbSchema.Table_Game_Turn_Results.TABLE_NAME,
                allColumns,
                selection==null ? null : selection,
                selectionArgs==null ? null : selectionArgs,
                groupBy==null ? null : groupBy,
                having==null ? null : having,
                orderBy==null ? null : orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Game_Turn_Results game_turn_results = cursorToGame_Turn_Results(cursor);
            game_turn_resultsList.add(game_turn_results);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return game_turn_resultsList;
    }

    public static long insertRecord(Game_Turn_Results game_turn_results) {
        ContentValues values = new ContentValues();
        values = getGame_Turn_ResultsValues(game_turn_results);
        database_open();
        long insertId = database.insert(DbSchema.Table_Game_Turn_Results.TABLE_NAME , null, values);
        database_close();
        return insertId;
    }

    public static int updateRecord(Game_Turn_Results game_turn_results) { 
        ContentValues values = new ContentValues();
        values = getGame_Turn_ResultsValues(game_turn_results);
        database_open();
        String[] where = new String[] { String.valueOf(game_turn_results.getid_game_turn_result()) }; 
        int updatedId = database.update(DbSchema.Table_Game_Turn_Results.TABLE_NAME , values, DbSchema.Table_Game_Turn_Results.COL_ID_GAME_TURN_RESULT + " = ? ",where );
        database_close();
        return updatedId;
    }

    public static int deleteRecord(Game_Turn_Results game_turn_results) { 
        database_open();
        String[] where = new String[] { String.valueOf(game_turn_results.getid_game_turn_result()) }; 
        int deletedCount = database.delete(DbSchema.Table_Game_Turn_Results.TABLE_NAME , DbSchema.Table_Game_Turn_Results.COL_ID_GAME_TURN_RESULT + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteRecord(String id) {
        database_open();
        String[] where = new String[] { id }; 
        int deletedCount = database.delete(DbSchema.Table_Game_Turn_Results.TABLE_NAME , DbSchema.Table_Game_Turn_Results.COL_ID_GAME_TURN_RESULT + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteAllRecords() {
        database_open();
        int deletedCount = database.delete(DbSchema.Table_Game_Turn_Results.TABLE_NAME , null, null );
        database_close();
        return deletedCount;
    }

    protected static ContentValues getGame_Turn_ResultsValues(Game_Turn_Results game_turn_results) {
        ContentValues values = new ContentValues();

        values.put(DbSchema.Table_Game_Turn_Results.COL_ID_GAME_TURN_RESULT, game_turn_results.getid_game_turn_result());
        values.put(DbSchema.Table_Game_Turn_Results.COL_ID_GAME_TURN, game_turn_results.getid_game_turn());
        values.put(DbSchema.Table_Game_Turn_Results.COL_ID_NICKNAME, game_turn_results.getid_nickname());
        values.put(DbSchema.Table_Game_Turn_Results.COL_RESULT, game_turn_results.getresult());
        values.put(DbSchema.Table_Game_Turn_Results.COL_DIAMONDS_REPORT, game_turn_results.getdiamonds_report());
        values.put(DbSchema.Table_Game_Turn_Results.COL_HEARTS_REPORT, game_turn_results.gethearts_report());
        values.put(DbSchema.Table_Game_Turn_Results.COL_SPADES_REPORT, game_turn_results.getspades_report());
        values.put(DbSchema.Table_Game_Turn_Results.COL_CLUBS_REPORT, game_turn_results.getclubs_report());

        return values;
    }

    protected static Game_Turn_Results cursorToGame_Turn_Results(Cursor cursor)  {
        Game_Turn_Results game_turn_results = new Game_Turn_Results();

        game_turn_results.setid_game_turn_result(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Game_Turn_Results.COL_ID_GAME_TURN_RESULT)));
        game_turn_results.setid_game_turn(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Game_Turn_Results.COL_ID_GAME_TURN)));
        game_turn_results.setid_nickname(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Game_Turn_Results.COL_ID_NICKNAME)));
        game_turn_results.setresult(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Game_Turn_Results.COL_RESULT)));
        game_turn_results.setdiamonds_report(cursor.getString(cursor.getColumnIndex(DbSchema.Table_Game_Turn_Results.COL_DIAMONDS_REPORT)));
        game_turn_results.sethearts_report(cursor.getString(cursor.getColumnIndex(DbSchema.Table_Game_Turn_Results.COL_HEARTS_REPORT)));
        game_turn_results.setspades_report(cursor.getString(cursor.getColumnIndex(DbSchema.Table_Game_Turn_Results.COL_SPADES_REPORT)));
        game_turn_results.setclubs_report(cursor.getString(cursor.getColumnIndex(DbSchema.Table_Game_Turn_Results.COL_CLUBS_REPORT)));

        return game_turn_results;
    }

    

}

