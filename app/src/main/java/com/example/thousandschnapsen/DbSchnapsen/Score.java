package com.example.thousandschnapsen.DbSchnapsen;

import android.os.Bundle;
import java.util.Date;

public class Score {

    public static final String COL_ID_SCORE = "id_score";
    public static final String COL_SCORE = "score";

    private Integer mid_score;
    private String mscore;

    public Score() {
    }

    public Score(Integer id_score, String score) {
        this.mid_score = id_score;
        this.mscore = score;
    }

    public Integer getid_score() {
        return mid_score;
    }

    public void setid_score(Integer id_score) {
        this.mid_score = id_score;
    }

    public String getscore() {
        return mscore;
    }

    public void setscore(String score) {
        this.mscore = score;
    }


    public Bundle toBundle() { 
        Bundle b = new Bundle();
        b.putInt(COL_ID_SCORE, this.mid_score);
        b.putString(COL_SCORE, this.mscore);
        return b;
    }

    public Score(Bundle b) {
        if (b != null) {
            this.mid_score = b.getInt(COL_ID_SCORE);
            this.mscore = b.getString(COL_SCORE);
        }
    }

    @Override
    public String toString() {
        return "Score{" +
            " mid_score=" + mid_score +
            ", mscore='" + mscore + '\'' +
            '}';
    }


}
