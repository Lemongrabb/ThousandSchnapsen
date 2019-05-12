package com.example.thousandschnapsen.DbSchnapsen;

import android.os.Bundle;
import java.util.Date;

public class Game_Turn_Results {

    public static final String COL_ID_GAME_TURN_RESULT = "id_game_turn_result";
    public static final String COL_ID_GAME_TURN = "id_game_turn";
    public static final String COL_ID_NICKNAME = "id_nickname";
    public static final String COL_RESULT = "result";
    public static final String COL_DIAMONDS_REPORT = "diamonds_report";
    public static final String COL_HEARTS_REPORT = "hearts_report";
    public static final String COL_SPADES_REPORT = "spades_report";
    public static final String COL_CLUBS_REPORT = "clubs_report";

    private Integer mid_game_turn_result;
    private Integer mid_game_turn;
    private Integer mid_nickname;
    private Integer mresult;
    private String mdiamonds_report;
    private String mhearts_report;
    private String mspades_report;
    private String mclubs_report;

    public Game_Turn_Results() {
    }

    public Game_Turn_Results(Integer id_game_turn_result, Integer id_game_turn, Integer id_nickname, Integer result, String diamonds_report, String hearts_report, String spades_report, String clubs_report) {
        this.mid_game_turn_result = id_game_turn_result;
        this.mid_game_turn = id_game_turn;
        this.mid_nickname = id_nickname;
        this.mresult = result;
        this.mdiamonds_report = diamonds_report;
        this.mhearts_report = hearts_report;
        this.mspades_report = spades_report;
        this.mclubs_report = clubs_report;
    }

    public Integer getid_game_turn_result() {
        return mid_game_turn_result;
    }

    public void setid_game_turn_result(Integer id_game_turn_result) {
        this.mid_game_turn_result = id_game_turn_result;
    }

    public Integer getid_game_turn() {
        return mid_game_turn;
    }

    public void setid_game_turn(Integer id_game_turn) {
        this.mid_game_turn = id_game_turn;
    }

    public Integer getid_nickname() {
        return mid_nickname;
    }

    public void setid_nickname(Integer id_nickname) {
        this.mid_nickname = id_nickname;
    }

    public Integer getresult() {
        return mresult;
    }

    public void setresult(Integer result) {
        this.mresult = result;
    }

    public String getdiamonds_report() {
        return mdiamonds_report;
    }

    public void setdiamonds_report(String diamonds_report) {
        this.mdiamonds_report = diamonds_report;
    }

    public String gethearts_report() {
        return mhearts_report;
    }

    public void sethearts_report(String hearts_report) {
        this.mhearts_report = hearts_report;
    }

    public String getspades_report() {
        return mspades_report;
    }

    public void setspades_report(String spades_report) {
        this.mspades_report = spades_report;
    }

    public String getclubs_report() {
        return mclubs_report;
    }

    public void setclubs_report(String clubs_report) {
        this.mclubs_report = clubs_report;
    }


    public Bundle toBundle() { 
        Bundle b = new Bundle();
        b.putInt(COL_ID_GAME_TURN_RESULT, this.mid_game_turn_result);
        b.putInt(COL_ID_GAME_TURN, this.mid_game_turn);
        b.putInt(COL_ID_NICKNAME, this.mid_nickname);
        b.putInt(COL_RESULT, this.mresult);
        b.putString(COL_DIAMONDS_REPORT, this.mdiamonds_report);
        b.putString(COL_HEARTS_REPORT, this.mhearts_report);
        b.putString(COL_SPADES_REPORT, this.mspades_report);
        b.putString(COL_CLUBS_REPORT, this.mclubs_report);
        return b;
    }

    public Game_Turn_Results(Bundle b) {
        if (b != null) {
            this.mid_game_turn_result = b.getInt(COL_ID_GAME_TURN_RESULT);
            this.mid_game_turn = b.getInt(COL_ID_GAME_TURN);
            this.mid_nickname = b.getInt(COL_ID_NICKNAME);
            this.mresult = b.getInt(COL_RESULT);
            this.mdiamonds_report = b.getString(COL_DIAMONDS_REPORT);
            this.mhearts_report = b.getString(COL_HEARTS_REPORT);
            this.mspades_report = b.getString(COL_SPADES_REPORT);
            this.mclubs_report = b.getString(COL_CLUBS_REPORT);
        }
    }

    @Override
    public String toString() {
        return "Game_Turn_Results{" +
            " mid_game_turn_result=" + mid_game_turn_result +
            ", mid_game_turn=" + mid_game_turn +
            ", mid_nickname=" + mid_nickname +
            ", mresult=" + mresult +
            ", mdiamonds_report='" + mdiamonds_report + '\'' +
            ", mhearts_report='" + mhearts_report + '\'' +
            ", mspades_report='" + mspades_report + '\'' +
            ", mclubs_report='" + mclubs_report + '\'' +
            '}';
    }


}
