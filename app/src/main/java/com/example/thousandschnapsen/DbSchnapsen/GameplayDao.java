package com.example.thousandschnapsen.DbSchnapsen;

import java.util.ArrayList;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class GameplayDao extends DbManager {
    private static final String TAG = "GameplayDao";

    protected static SQLiteDatabase database;
    protected static DbManager mDbManager;
    protected static  String[] allColumns = DbSchema.Table_Gameplay.allColumns;


    protected GameplayDao() {
    }

    protected static void database_open() throws SQLException {
        mDbManager = DbManager.getsInstance();
        database = mDbManager.getDatabase();
    }

    protected static void database_close() {
        mDbManager = DbManager.getsInstance();
        mDbManager.close();
    }

    public static Gameplay loadRecordById(int mid_gameplay)  { 
        database_open();
        Cursor cursor = database.query(DbSchema.Table_Gameplay.TABLE_NAME,allColumns,  "id_gameplay = ?" , new String[] { String.valueOf(mid_gameplay) } , null, null, null,null);

        if (cursor != null)
            cursor.moveToFirst();

        Gameplay gameplay = new Gameplay();
        gameplay = cursorToGameplay(cursor);

        cursor.close();
        database_close();

        return gameplay;
    }

    public static ArrayList<Gameplay> loadAllRecords() {
        ArrayList<Gameplay> gameplayList = new ArrayList<Gameplay>();
        database_open();

        Cursor cursor = database.query(
                DbSchema.Table_Gameplay.TABLE_NAME,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Gameplay gameplay = cursorToGameplay(cursor);
            gameplayList.add(gameplay);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return gameplayList;
    }

    // Please always use the typed column names (Table_Gameplay) when passing arguments.
    // Example: Table_Gameplay.Column_Name
    public static ArrayList<Gameplay> loadAllRecords(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        ArrayList<Gameplay> gameplayList = new ArrayList<Gameplay>();
        database_open();

        if(TextUtils.isEmpty(selection)){
            selection = null;
            selectionArgs = null;
        }

        Cursor cursor = database.query(
                DbSchema.Table_Gameplay.TABLE_NAME,
                allColumns,
                selection==null ? null : selection,
                selectionArgs==null ? null : selectionArgs,
                groupBy==null ? null : groupBy,
                having==null ? null : having,
                orderBy==null ? null : orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Gameplay gameplay = cursorToGameplay(cursor);
            gameplayList.add(gameplay);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return gameplayList;
    }

    public static long insertRecord(Gameplay gameplay) {
        ContentValues values = new ContentValues();
        values = getGameplayValues(gameplay);
        database_open();
        long insertId = database.insert(DbSchema.Table_Gameplay.TABLE_NAME , null, values);
        database_close();
        return insertId;
    }

    public static int updateRecord(Gameplay gameplay) { 
        ContentValues values = new ContentValues();
        values = getGameplayValues(gameplay);
        database_open();
        String[] where = new String[] { String.valueOf(gameplay.getid_gameplay()) }; 
        int updatedId = database.update(DbSchema.Table_Gameplay.TABLE_NAME , values, DbSchema.Table_Gameplay.COL_ID_GAMEPLAY + " = ? ",where );
        database_close();
        return updatedId;
    }

    public static int deleteRecord(Gameplay gameplay) { 
        database_open();
        String[] where = new String[] { String.valueOf(gameplay.getid_gameplay()) }; 
        int deletedCount = database.delete(DbSchema.Table_Gameplay.TABLE_NAME , DbSchema.Table_Gameplay.COL_ID_GAMEPLAY + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteRecord(String id) {
        database_open();
        String[] where = new String[] { id }; 
        int deletedCount = database.delete(DbSchema.Table_Gameplay.TABLE_NAME , DbSchema.Table_Gameplay.COL_ID_GAMEPLAY + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteAllRecords() {
        database_open();
        int deletedCount = database.delete(DbSchema.Table_Gameplay.TABLE_NAME , null, null );
        database_close();
        return deletedCount;
    }

    protected static ContentValues getGameplayValues(Gameplay gameplay) {
        ContentValues values = new ContentValues();

        values.put(DbSchema.Table_Gameplay.COL_ID_GAMEPLAY, gameplay.getid_gameplay());
        values.put(DbSchema.Table_Gameplay.COL_DATE, gameplay.getdate());

        return values;
    }

    protected static Gameplay cursorToGameplay(Cursor cursor)  {
        Gameplay gameplay = new Gameplay();

        gameplay.setid_gameplay(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Gameplay.COL_ID_GAMEPLAY)));
        gameplay.setdate(cursor.getString(cursor.getColumnIndex(DbSchema.Table_Gameplay.COL_DATE)));

        return gameplay;
    }

    

}

