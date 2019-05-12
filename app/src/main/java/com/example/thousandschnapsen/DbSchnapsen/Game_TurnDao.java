package com.example.thousandschnapsen.DbSchnapsen;

import java.util.ArrayList;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class Game_TurnDao extends DbManager {
    private static final String TAG = "Game_TurnDao";

    protected static SQLiteDatabase database;
    protected static DbManager mDbManager;
    protected static  String[] allColumns = DbSchema.Table_Game_Turn.allColumns;


    protected Game_TurnDao() {
    }

    protected static void database_open() throws SQLException {
        mDbManager = DbManager.getsInstance();
        database = mDbManager.getDatabase();
    }

    protected static void database_close() {
        mDbManager = DbManager.getsInstance();
        mDbManager.close();
    }

    public static Game_Turn loadRecordById(int mid_game_turn)  { 
        database_open();
        Cursor cursor = database.query(DbSchema.Table_Game_Turn.TABLE_NAME,allColumns,  "id_game_turn = ?" , new String[] { String.valueOf(mid_game_turn) } , null, null, null,null);

        if (cursor != null)
            cursor.moveToFirst();

        Game_Turn game_turn = new Game_Turn();
        game_turn = cursorToGame_Turn(cursor);

        cursor.close();
        database_close();

        return game_turn;
    }

    public static ArrayList<Game_Turn> loadAllRecords() {
        ArrayList<Game_Turn> game_turnList = new ArrayList<Game_Turn>();
        database_open();

        Cursor cursor = database.query(
                DbSchema.Table_Game_Turn.TABLE_NAME,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Game_Turn game_turn = cursorToGame_Turn(cursor);
            game_turnList.add(game_turn);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return game_turnList;
    }

    // Please always use the typed column names (Table_Game_Turn) when passing arguments.
    // Example: Table_Game_Turn.Column_Name
    public static ArrayList<Game_Turn> loadAllRecords(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        ArrayList<Game_Turn> game_turnList = new ArrayList<Game_Turn>();
        database_open();

        if(TextUtils.isEmpty(selection)){
            selection = null;
            selectionArgs = null;
        }

        Cursor cursor = database.query(
                DbSchema.Table_Game_Turn.TABLE_NAME,
                allColumns,
                selection==null ? null : selection,
                selectionArgs==null ? null : selectionArgs,
                groupBy==null ? null : groupBy,
                having==null ? null : having,
                orderBy==null ? null : orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Game_Turn game_turn = cursorToGame_Turn(cursor);
            game_turnList.add(game_turn);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return game_turnList;
    }

    public static long insertRecord(Game_Turn game_turn) {
        ContentValues values = new ContentValues();
        values = getGame_TurnValues(game_turn);
        database_open();
        long insertId = database.insert(DbSchema.Table_Game_Turn.TABLE_NAME , null, values);
        database_close();
        return insertId;
    }

    public static int updateRecord(Game_Turn game_turn) { 
        ContentValues values = new ContentValues();
        values = getGame_TurnValues(game_turn);
        database_open();
        String[] where = new String[] { String.valueOf(game_turn.getid_game_turn()) }; 
        int updatedId = database.update(DbSchema.Table_Game_Turn.TABLE_NAME , values, DbSchema.Table_Game_Turn.COL_ID_GAME_TURN + " = ? ",where );
        database_close();
        return updatedId;
    }

    public static int deleteRecord(Game_Turn game_turn) { 
        database_open();
        String[] where = new String[] { String.valueOf(game_turn.getid_game_turn()) }; 
        int deletedCount = database.delete(DbSchema.Table_Game_Turn.TABLE_NAME , DbSchema.Table_Game_Turn.COL_ID_GAME_TURN + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteRecord(String id) {
        database_open();
        String[] where = new String[] { id }; 
        int deletedCount = database.delete(DbSchema.Table_Game_Turn.TABLE_NAME , DbSchema.Table_Game_Turn.COL_ID_GAME_TURN + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteAllRecords() {
        database_open();
        int deletedCount = database.delete(DbSchema.Table_Game_Turn.TABLE_NAME , null, null );
        database_close();
        return deletedCount;
    }

    protected static ContentValues getGame_TurnValues(Game_Turn game_turn) {
        ContentValues values = new ContentValues();

        values.put(DbSchema.Table_Game_Turn.COL_ID_GAME_TURN, game_turn.getid_game_turn());
        values.put(DbSchema.Table_Game_Turn.COL_WHO_PLYED, game_turn.getwho_plyed());
        values.put(DbSchema.Table_Game_Turn.COL_GAME_NOT_PLAYED, game_turn.getgame_not_played());
        values.put(DbSchema.Table_Game_Turn.COL_BIDED_RESULT, game_turn.getbided_result());

        return values;
    }

    protected static Game_Turn cursorToGame_Turn(Cursor cursor)  {
        Game_Turn game_turn = new Game_Turn();

        game_turn.setid_game_turn(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Game_Turn.COL_ID_GAME_TURN)));
        game_turn.setwho_plyed(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Game_Turn.COL_WHO_PLYED)));
        game_turn.setgame_not_played(cursor.getString(cursor.getColumnIndex(DbSchema.Table_Game_Turn.COL_GAME_NOT_PLAYED)));
        game_turn.setbided_result(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Game_Turn.COL_BIDED_RESULT)));

        return game_turn;
    }

    

}

