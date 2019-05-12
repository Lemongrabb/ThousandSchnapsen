package com.example.thousandschnapsen.DbSchnapsen;

import java.util.ArrayList;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class ScoreDao extends DbManager {
    private static final String TAG = "ScoreDao";

    protected static SQLiteDatabase database;
    protected static DbManager mDbManager;
    protected static  String[] allColumns = DbSchema.Table_Score.allColumns;


    protected ScoreDao() {
    }

    protected static void database_open() throws SQLException {
        mDbManager = DbManager.getsInstance();
        database = mDbManager.getDatabase();
    }

    protected static void database_close() {
        mDbManager = DbManager.getsInstance();
        mDbManager.close();
    }

    public static Score loadRecordById(int mid_scroe)  { 
        database_open();
        Cursor cursor = database.query(DbSchema.Table_Score.TABLE_NAME,allColumns,  "id_scroe = ?" , new String[] { String.valueOf(mid_scroe) } , null, null, null,null);

        if (cursor != null)
            cursor.moveToFirst();

        Score score = new Score();
        score = cursorToScore(cursor);

        cursor.close();
        database_close();

        return score;
    }

    public static ArrayList<Score> loadAllRecords() {
        ArrayList<Score> scoreList = new ArrayList<Score>();
        database_open();

        Cursor cursor = database.query(
                DbSchema.Table_Score.TABLE_NAME,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Score score = cursorToScore(cursor);
            scoreList.add(score);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return scoreList;
    }

    // Please always use the typed column names (Table_Score) when passing arguments.
    // Example: Table_Score.Column_Name
    public static ArrayList<Score> loadAllRecords(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        ArrayList<Score> scoreList = new ArrayList<Score>();
        database_open();

        if(TextUtils.isEmpty(selection)){
            selection = null;
            selectionArgs = null;
        }

        Cursor cursor = database.query(
                DbSchema.Table_Score.TABLE_NAME,
                allColumns,
                selection==null ? null : selection,
                selectionArgs==null ? null : selectionArgs,
                groupBy==null ? null : groupBy,
                having==null ? null : having,
                orderBy==null ? null : orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Score score = cursorToScore(cursor);
            scoreList.add(score);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return scoreList;
    }

    public static long insertRecord(Score score) {
        ContentValues values = new ContentValues();
        values = getScoreValues(score);
        database_open();
        long insertId = database.insert(DbSchema.Table_Score.TABLE_NAME , null, values);
        database_close();
        return insertId;
    }

    public static int updateRecord(Score score) { 
        ContentValues values = new ContentValues();
        values = getScoreValues(score);
        database_open();
        String[] where = new String[] { String.valueOf(score.getid_scroe()) }; 
        int updatedId = database.update(DbSchema.Table_Score.TABLE_NAME , values, DbSchema.Table_Score.COL_ID_SCROE + " = ? ",where );
        database_close();
        return updatedId;
    }

    public static int deleteRecord(Score score) { 
        database_open();
        String[] where = new String[] { String.valueOf(score.getid_scroe()) }; 
        int deletedCount = database.delete(DbSchema.Table_Score.TABLE_NAME , DbSchema.Table_Score.COL_ID_SCROE + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteRecord(String id) {
        database_open();
        String[] where = new String[] { id }; 
        int deletedCount = database.delete(DbSchema.Table_Score.TABLE_NAME , DbSchema.Table_Score.COL_ID_SCROE + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteAllRecords() {
        database_open();
        int deletedCount = database.delete(DbSchema.Table_Score.TABLE_NAME , null, null );
        database_close();
        return deletedCount;
    }

    protected static ContentValues getScoreValues(Score score) {
        ContentValues values = new ContentValues();

        values.put(DbSchema.Table_Score.COL_ID_SCROE, score.getid_scroe());
        values.put(DbSchema.Table_Score.COL_SCORE, score.getscore());

        return values;
    }

    protected static Score cursorToScore(Cursor cursor)  {
        Score score = new Score();

        score.setid_scroe(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Score.COL_ID_SCROE)));
        score.setscore(cursor.getString(cursor.getColumnIndex(DbSchema.Table_Score.COL_SCORE)));

        return score;
    }

    

}

