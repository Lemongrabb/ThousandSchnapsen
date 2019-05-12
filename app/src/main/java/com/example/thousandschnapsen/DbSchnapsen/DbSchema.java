package com.example.thousandschnapsen.DbSchnapsen;

import android.provider.BaseColumns;

public class DbSchema {
    private static final String TAG = "DbSchema";

    public static final String DATABASE_NAME = "DbSchnapsen.db";
    public static final int DATABASE_VERSION = 1;
    public static final String SORT_ASC = " ASC";
    public static final String SORT_DESC = " DESC";
    public static final String[] ORDERS = {SORT_ASC,SORT_DESC};
    public static final int OFF = 0;
    public static final int ON = 1;

    public static final class Table_Gameplay implements BaseColumns  { 
        // Table Name
        public static final String TABLE_NAME = "gameplay";

        // Table Columns
        public static final String COL_ID_GAMEPLAY = "id_gameplay";
        public static final String COL_DATE = "date";

        // Create Table Statement
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS gameplay ( " + 
            COL_ID_GAMEPLAY + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  " + 
            COL_DATE + " DATETIME );";

        // Drop table statement
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS gameplay;";

        // Columns list array
        public static final String[] allColumns = {
            COL_ID_GAMEPLAY,
            COL_DATE };
    }

    public static final class Table_Game_Turn implements BaseColumns  { 
        // Table Name
        public static final String TABLE_NAME = "game_turn";

        // Table Columns
        public static final String COL_ID_GAME_TURN = "id_game_turn";
        public static final String COL_WHO_PLYED = "who_plyed";
        public static final String COL_GAME_NOT_PLAYED = "game_not_played";
        public static final String COL_BIDED_RESULT = "bided_result";

        // Create Table Statement
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS game_turn ( " + 
            COL_ID_GAME_TURN + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  " + 
            COL_WHO_PLYED + " INTEGER," + 
            COL_GAME_NOT_PLAYED + " BOOLEAN," + 
            COL_BIDED_RESULT + " INTEGER );";

        // Drop table statement
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS game_turn;";

        // Columns list array
        public static final String[] allColumns = {
            COL_ID_GAME_TURN,
            COL_WHO_PLYED,
            COL_GAME_NOT_PLAYED,
            COL_BIDED_RESULT };
    }

    public static final class Table_Game_Turn_Results implements BaseColumns  { 
        // Table Name
        public static final String TABLE_NAME = "game_turn_results";

        // Table Columns
        public static final String COL_ID_GAME_TURN_RESULT = "id_game_turn_result";
        public static final String COL_ID_GAME_TURN = "id_game_turn";
        public static final String COL_ID_NICKNAME = "id_nickname";
        public static final String COL_RESULT = "result";
        public static final String COL_DIAMONDS_REPORT = "diamonds_report";
        public static final String COL_HEARTS_REPORT = "hearts_report";
        public static final String COL_SPADES_REPORT = "spades_report";
        public static final String COL_CLUBS_REPORT = "clubs_report";

        // Create Table Statement
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS game_turn_results ( " + 
            COL_ID_GAME_TURN_RESULT + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  " + 
            COL_ID_GAME_TURN + " INTEGER," + 
            COL_ID_NICKNAME + " INTEGER," + 
            COL_RESULT + " INTEGER," + 
            COL_DIAMONDS_REPORT + " BOOLEAN," + 
            COL_HEARTS_REPORT + " BOOLEAN," + 
            COL_SPADES_REPORT + " BOOLEAN," + 
            COL_CLUBS_REPORT + " BOOLEAN );";

        // Drop table statement
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS game_turn_results;";

        // Columns list array
        public static final String[] allColumns = {
            COL_ID_GAME_TURN_RESULT,
            COL_ID_GAME_TURN,
            COL_ID_NICKNAME,
            COL_RESULT,
            COL_DIAMONDS_REPORT,
            COL_HEARTS_REPORT,
            COL_SPADES_REPORT,
            COL_CLUBS_REPORT };
    }

    public static final class Table_Nicknames implements BaseColumns  { 
        // Table Name
        public static final String TABLE_NAME = "nicknames";

        // Table Columns
        public static final String COL_ID_NICKNAMES = "id_nicknames";
        public static final String COL_PHONE_MODEL = "phone_model";
        public static final String COL_NICKNAME = "nickname";

        // Create Table Statement
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS nicknames ( " + 
            COL_ID_NICKNAMES + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  " + 
            COL_PHONE_MODEL + " TEXT," + 
            COL_NICKNAME + " TEXT );";

        // Drop table statement
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS nicknames;";

        // Columns list array
        public static final String[] allColumns = {
            COL_ID_NICKNAMES,
            COL_PHONE_MODEL,
            COL_NICKNAME };
    }

    public static final class Table_Ranking implements BaseColumns  { 
        // Table Name
        public static final String TABLE_NAME = "ranking";

        // Table Columns
        public static final String COL_ID_RANKING = "id_ranking";
        public static final String COL_ID_NICKNAMES = "id_nicknames";
        public static final String COL_ID_SCORE = "id_score";
        public static final String COL_IS_WIN = "is_win";
        public static final String COL_ID_GAMEPLAY = "id_gameplay";

        // Create Table Statement
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ranking ( " + 
            COL_ID_RANKING + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  " + 
            COL_ID_NICKNAMES + " INTEGER," + 
            COL_ID_SCORE + " INTEGER," + 
            COL_IS_WIN + " BOOLEAN," + 
            COL_ID_GAMEPLAY + " INTEGER );";

        // Drop table statement
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS ranking;";

        // Columns list array
        public static final String[] allColumns = {
            COL_ID_RANKING,
            COL_ID_NICKNAMES,
            COL_ID_SCORE,
            COL_IS_WIN,
            COL_ID_GAMEPLAY };
    }

    public static final class Table_Score implements BaseColumns  { 
        // Table Name
        public static final String TABLE_NAME = "score";

        // Table Columns
        public static final String COL_ID_SCORE = "id_score";
        public static final String COL_SCORE = "score";

        // Create Table Statement
        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS score ( " + 
            COL_ID_SCORE + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  " +
            COL_SCORE + " TEXT );";

        // Drop table statement
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS score;";

        // Columns list array
        public static final String[] allColumns = {
            COL_ID_SCORE,
            COL_SCORE };
    }

}
