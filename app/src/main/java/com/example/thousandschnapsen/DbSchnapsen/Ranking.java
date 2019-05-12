package com.example.thousandschnapsen.DbSchnapsen;

import android.os.Bundle;
import java.util.Date;

public class Ranking {

    public static final String COL_ID_RANKING = "id_ranking";
    public static final String COL_ID_NICKNAMES = "id_nicknames";
    public static final String COL_ID_SCORE = "id_score";
    public static final String COL_IS_WIN = "is_win";
    public static final String COL_ID_GAMEPLAY = "id_gameplay";

    private Integer mid_ranking;
    private Integer mid_nicknames;
    private Integer mid_score;
    private String mis_win;
    private Integer mid_gameplay;

    public Ranking() {
    }

    public Ranking(Integer id_ranking, Integer id_nicknames, Integer id_score, String is_win, Integer id_gameplay) {
        this.mid_ranking = id_ranking;
        this.mid_nicknames = id_nicknames;
        this.mid_score = id_score;
        this.mis_win = is_win;
        this.mid_gameplay = id_gameplay;
    }

    public Integer getid_ranking() {
        return mid_ranking;
    }

    public void setid_ranking(Integer id_ranking) {
        this.mid_ranking = id_ranking;
    }

    public Integer getid_nicknames() {
        return mid_nicknames;
    }

    public void setid_nicknames(Integer id_nicknames) {
        this.mid_nicknames = id_nicknames;
    }

    public Integer getid_score() {
        return mid_score;
    }

    public void setid_score(Integer id_score) {
        this.mid_score = id_score;
    }

    public String getis_win() {
        return mis_win;
    }

    public void setis_win(String is_win) {
        this.mis_win = is_win;
    }

    public Integer getid_gameplay() {
        return mid_gameplay;
    }

    public void setid_gameplay(Integer id_gameplay) {
        this.mid_gameplay = id_gameplay;
    }


    public Bundle toBundle() { 
        Bundle b = new Bundle();
        b.putInt(COL_ID_RANKING, this.mid_ranking);
        b.putInt(COL_ID_NICKNAMES, this.mid_nicknames);
        b.putInt(COL_ID_SCORE, this.mid_score);
        b.putString(COL_IS_WIN, this.mis_win);
        b.putInt(COL_ID_GAMEPLAY, this.mid_gameplay);
        return b;
    }

    public Ranking(Bundle b) {
        if (b != null) {
            this.mid_ranking = b.getInt(COL_ID_RANKING);
            this.mid_nicknames = b.getInt(COL_ID_NICKNAMES);
            this.mid_score = b.getInt(COL_ID_SCORE);
            this.mis_win = b.getString(COL_IS_WIN);
            this.mid_gameplay = b.getInt(COL_ID_GAMEPLAY);
        }
    }

    @Override
    public String toString() {
        return "Ranking{" +
            " mid_ranking=" + mid_ranking +
            ", mid_nicknames=" + mid_nicknames +
            ", mid_score=" + mid_score +
            ", mis_win='" + mis_win + '\'' +
            ", mid_gameplay=" + mid_gameplay +
            '}';
    }


}
