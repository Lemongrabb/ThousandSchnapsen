package com.example.thousandschnapsen.DbSchnapsen;

import android.os.Bundle;
import java.util.Date;

public class Score {

    public static final String COL_ID_SCROE = "id_scroe";
    public static final String COL_SCORE = "score";

    private Integer mid_scroe;
    private String mscore;

    public Score() {
    }

    public Score(Integer id_scroe, String score) {
        this.mid_scroe = id_scroe;
        this.mscore = score;
    }

    public Integer getid_scroe() {
        return mid_scroe;
    }

    public void setid_scroe(Integer id_scroe) {
        this.mid_scroe = id_scroe;
    }

    public String getscore() {
        return mscore;
    }

    public void setscore(String score) {
        this.mscore = score;
    }


    public Bundle toBundle() { 
        Bundle b = new Bundle();
        b.putInt(COL_ID_SCROE, this.mid_scroe);
        b.putString(COL_SCORE, this.mscore);
        return b;
    }

    public Score(Bundle b) {
        if (b != null) {
            this.mid_scroe = b.getInt(COL_ID_SCROE);
            this.mscore = b.getString(COL_SCORE);
        }
    }

    @Override
    public String toString() {
        return "Score{" +
            " mid_scroe=" + mid_scroe +
            ", mscore='" + mscore + '\'' +
            '}';
    }


}
