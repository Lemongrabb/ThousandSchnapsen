package com.example.thousandschnapsen.DbSchnapsen;

import java.util.ArrayList;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class RankingDao extends DbManager {
    private static final String TAG = "RankingDao";

    protected static SQLiteDatabase database;
    protected static DbManager mDbManager;
    protected static  String[] allColumns = DbSchema.Table_Ranking.allColumns;


    protected RankingDao() {
    }

    protected static void database_open() throws SQLException {
        mDbManager = DbManager.getsInstance();
        database = mDbManager.getDatabase();
    }

    protected static void database_close() {
        mDbManager = DbManager.getsInstance();
        mDbManager.close();
    }

    public static Ranking loadRecordById(int mid_ranking)  { 
        database_open();
        Cursor cursor = database.query(DbSchema.Table_Ranking.TABLE_NAME,allColumns,  "id_ranking = ?" , new String[] { String.valueOf(mid_ranking) } , null, null, null,null);

        if (cursor != null)
            cursor.moveToFirst();

        Ranking ranking = new Ranking();
        ranking = cursorToRanking(cursor);

        cursor.close();
        database_close();

        return ranking;
    }

    public static ArrayList<Ranking> loadAllRecords() {
        ArrayList<Ranking> rankingList = new ArrayList<Ranking>();
        database_open();

        Cursor cursor = database.query(
                DbSchema.Table_Ranking.TABLE_NAME,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Ranking ranking = cursorToRanking(cursor);
            rankingList.add(ranking);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return rankingList;
    }

    // Please always use the typed column names (Table_Ranking) when passing arguments.
    // Example: Table_Ranking.Column_Name
    public static ArrayList<Ranking> loadAllRecords(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        ArrayList<Ranking> rankingList = new ArrayList<Ranking>();
        database_open();

        if(TextUtils.isEmpty(selection)){
            selection = null;
            selectionArgs = null;
        }

        Cursor cursor = database.query(
                DbSchema.Table_Ranking.TABLE_NAME,
                allColumns,
                selection==null ? null : selection,
                selectionArgs==null ? null : selectionArgs,
                groupBy==null ? null : groupBy,
                having==null ? null : having,
                orderBy==null ? null : orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Ranking ranking = cursorToRanking(cursor);
            rankingList.add(ranking);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return rankingList;
    }

    public static long insertRecord(Ranking ranking) {
        ContentValues values = new ContentValues();
        values = getRankingValues(ranking);
        database_open();
        long insertId = database.insert(DbSchema.Table_Ranking.TABLE_NAME , null, values);
        database_close();
        return insertId;
    }

    public static int updateRecord(Ranking ranking) { 
        ContentValues values = new ContentValues();
        values = getRankingValues(ranking);
        database_open();
        String[] where = new String[] { String.valueOf(ranking.getid_ranking()) }; 
        int updatedId = database.update(DbSchema.Table_Ranking.TABLE_NAME , values, DbSchema.Table_Ranking.COL_ID_RANKING + " = ? ",where );
        database_close();
        return updatedId;
    }

    public static int deleteRecord(Ranking ranking) { 
        database_open();
        String[] where = new String[] { String.valueOf(ranking.getid_ranking()) }; 
        int deletedCount = database.delete(DbSchema.Table_Ranking.TABLE_NAME , DbSchema.Table_Ranking.COL_ID_RANKING + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteRecord(String id) {
        database_open();
        String[] where = new String[] { id }; 
        int deletedCount = database.delete(DbSchema.Table_Ranking.TABLE_NAME , DbSchema.Table_Ranking.COL_ID_RANKING + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteAllRecords() {
        database_open();
        int deletedCount = database.delete(DbSchema.Table_Ranking.TABLE_NAME , null, null );
        database_close();
        return deletedCount;
    }

    protected static ContentValues getRankingValues(Ranking ranking) {
        ContentValues values = new ContentValues();

        values.put(DbSchema.Table_Ranking.COL_ID_RANKING, ranking.getid_ranking());
        values.put(DbSchema.Table_Ranking.COL_ID_NICKNAMES, ranking.getid_nicknames());
        values.put(DbSchema.Table_Ranking.COL_ID_SCORE, ranking.getid_score());
        values.put(DbSchema.Table_Ranking.COL_IS_WIN, ranking.getis_win());
        values.put(DbSchema.Table_Ranking.COL_ID_GAMEPLAY, ranking.getid_gameplay());

        return values;
    }

    protected static Ranking cursorToRanking(Cursor cursor)  {
        Ranking ranking = new Ranking();

        ranking.setid_ranking(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Ranking.COL_ID_RANKING)));
        ranking.setid_nicknames(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Ranking.COL_ID_NICKNAMES)));
        ranking.setid_score(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Ranking.COL_ID_SCORE)));
        ranking.setis_win(cursor.getString(cursor.getColumnIndex(DbSchema.Table_Ranking.COL_IS_WIN)));
        ranking.setid_gameplay(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Ranking.COL_ID_GAMEPLAY)));

        return ranking;
    }

    

}

