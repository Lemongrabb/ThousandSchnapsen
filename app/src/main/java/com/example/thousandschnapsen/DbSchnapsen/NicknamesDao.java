package com.example.thousandschnapsen.DbSchnapsen;

import java.util.ArrayList;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class NicknamesDao extends DbManager {
    private static final String TAG = "NicknamesDao";

    protected static SQLiteDatabase database;
    protected static DbManager mDbManager;
    protected static  String[] allColumns = DbSchema.Table_Nicknames.allColumns;


    protected NicknamesDao() {
    }

    protected static void database_open() throws SQLException {
        mDbManager = DbManager.getsInstance();
        database = mDbManager.getDatabase();
    }

    protected static void database_close() {
        mDbManager = DbManager.getsInstance();
        mDbManager.close();
    }

    public static Nicknames loadRecordById(int mid_nicknames)  { 
        database_open();
        Cursor cursor = database.query(DbSchema.Table_Nicknames.TABLE_NAME,allColumns,  "id_nicknames = ?" , new String[] { String.valueOf(mid_nicknames) } , null, null, null,null);

        if (cursor != null)
            cursor.moveToFirst();

        Nicknames nicknames = new Nicknames();
        nicknames = cursorToNicknames(cursor);

        cursor.close();
        database_close();

        return nicknames;
    }

    public static ArrayList<Nicknames> loadAllRecords() {
        ArrayList<Nicknames> nicknamesList = new ArrayList<Nicknames>();
        database_open();

        Cursor cursor = database.query(
                DbSchema.Table_Nicknames.TABLE_NAME,
                allColumns,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Nicknames nicknames = cursorToNicknames(cursor);
            nicknamesList.add(nicknames);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return nicknamesList;
    }

    // Please always use the typed column names (Table_Nicknames) when passing arguments.
    // Example: Table_Nicknames.Column_Name
    public static ArrayList<Nicknames> loadAllRecords(String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        ArrayList<Nicknames> nicknamesList = new ArrayList<Nicknames>();
        database_open();

        if(TextUtils.isEmpty(selection)){
            selection = null;
            selectionArgs = null;
        }

        Cursor cursor = database.query(
                DbSchema.Table_Nicknames.TABLE_NAME,
                allColumns,
                selection==null ? null : selection,
                selectionArgs==null ? null : selectionArgs,
                groupBy==null ? null : groupBy,
                having==null ? null : having,
                orderBy==null ? null : orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Nicknames nicknames = cursorToNicknames(cursor);
            nicknamesList.add(nicknames);
            cursor.moveToNext();
        }
        cursor.close();
        database_close();
        return nicknamesList;
    }

    public static long insertRecord(Nicknames nicknames) {
        ContentValues values = new ContentValues();
        values = getNicknamesValues(nicknames);
        database_open();
        long insertId = database.insert(DbSchema.Table_Nicknames.TABLE_NAME , null, values);
        database_close();
        return insertId;
    }

    public static int updateRecord(Nicknames nicknames) { 
        ContentValues values = new ContentValues();
        values = getNicknamesValues(nicknames);
        database_open();
        String[] where = new String[] { String.valueOf(nicknames.getid_nicknames()) }; 
        int updatedId = database.update(DbSchema.Table_Nicknames.TABLE_NAME , values, DbSchema.Table_Nicknames.COL_ID_NICKNAMES + " = ? ",where );
        database_close();
        return updatedId;
    }

    public static int deleteRecord(Nicknames nicknames) { 
        database_open();
        String[] where = new String[] { String.valueOf(nicknames.getid_nicknames()) }; 
        int deletedCount = database.delete(DbSchema.Table_Nicknames.TABLE_NAME , DbSchema.Table_Nicknames.COL_ID_NICKNAMES + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteRecord(String id) {
        database_open();
        String[] where = new String[] { id }; 
        int deletedCount = database.delete(DbSchema.Table_Nicknames.TABLE_NAME , DbSchema.Table_Nicknames.COL_ID_NICKNAMES + " = ? ",where );
        database_close();
        return deletedCount;
    }

    public static int deleteAllRecords() {
        database_open();
        int deletedCount = database.delete(DbSchema.Table_Nicknames.TABLE_NAME , null, null );
        database_close();
        return deletedCount;
    }

    protected static ContentValues getNicknamesValues(Nicknames nicknames) {
        ContentValues values = new ContentValues();

        values.put(DbSchema.Table_Nicknames.COL_ID_NICKNAMES, nicknames.getid_nicknames());
        values.put(DbSchema.Table_Nicknames.COL_PHONE_MODEL, nicknames.getphone_model());
        values.put(DbSchema.Table_Nicknames.COL_NICKNAME, nicknames.getnickname());

        return values;
    }

    protected static Nicknames cursorToNicknames(Cursor cursor)  {
        Nicknames nicknames = new Nicknames();

        nicknames.setid_nicknames(cursor.getInt(cursor.getColumnIndex(DbSchema.Table_Nicknames.COL_ID_NICKNAMES)));
        nicknames.setphone_model(cursor.getString(cursor.getColumnIndex(DbSchema.Table_Nicknames.COL_PHONE_MODEL)));
        nicknames.setnickname(cursor.getString(cursor.getColumnIndex(DbSchema.Table_Nicknames.COL_NICKNAME)));

        return nicknames;
    }

    

}

