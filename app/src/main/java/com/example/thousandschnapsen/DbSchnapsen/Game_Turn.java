package com.example.thousandschnapsen.DbSchnapsen;

import android.os.Bundle;
import java.util.Date;

public class Game_Turn {

    public static final String COL_ID_GAME_TURN = "id_game_turn";
    public static final String COL_WHO_PLYED = "who_plyed";
    public static final String COL_GAME_NOT_PLAYED = "game_not_played";
    public static final String COL_BIDED_RESULT = "bided_result";

    private Integer mid_game_turn;
    private Integer mwho_plyed;
    private String mgame_not_played;
    private Integer mbided_result;

    public Game_Turn() {
    }

    public Game_Turn(Integer id_game_turn, Integer who_plyed, String game_not_played, Integer bided_result) {
        this.mid_game_turn = id_game_turn;
        this.mwho_plyed = who_plyed;
        this.mgame_not_played = game_not_played;
        this.mbided_result = bided_result;
    }

    public Integer getid_game_turn() {
        return mid_game_turn;
    }

    public void setid_game_turn(Integer id_game_turn) {
        this.mid_game_turn = id_game_turn;
    }

    public Integer getwho_plyed() {
        return mwho_plyed;
    }

    public void setwho_plyed(Integer who_plyed) {
        this.mwho_plyed = who_plyed;
    }

    public String getgame_not_played() {
        return mgame_not_played;
    }

    public void setgame_not_played(String game_not_played) {
        this.mgame_not_played = game_not_played;
    }

    public Integer getbided_result() {
        return mbided_result;
    }

    public void setbided_result(Integer bided_result) {
        this.mbided_result = bided_result;
    }


    public Bundle toBundle() { 
        Bundle b = new Bundle();
        b.putInt(COL_ID_GAME_TURN, this.mid_game_turn);
        b.putInt(COL_WHO_PLYED, this.mwho_plyed);
        b.putString(COL_GAME_NOT_PLAYED, this.mgame_not_played);
        b.putInt(COL_BIDED_RESULT, this.mbided_result);
        return b;
    }

    public Game_Turn(Bundle b) {
        if (b != null) {
            this.mid_game_turn = b.getInt(COL_ID_GAME_TURN);
            this.mwho_plyed = b.getInt(COL_WHO_PLYED);
            this.mgame_not_played = b.getString(COL_GAME_NOT_PLAYED);
            this.mbided_result = b.getInt(COL_BIDED_RESULT);
        }
    }

    @Override
    public String toString() {
        return "Game_Turn{" +
            " mid_game_turn=" + mid_game_turn +
            ", mwho_plyed=" + mwho_plyed +
            ", mgame_not_played='" + mgame_not_played + '\'' +
            ", mbided_result=" + mbided_result +
            '}';
    }


}
