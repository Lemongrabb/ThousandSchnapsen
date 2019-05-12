package com.example.thousandschnapsen.DbSchnapsen;

import android.os.Bundle;
import java.util.Date;

public class Gameplay {

    public static final String COL_ID_GAMEPLAY = "id_gameplay";
    public static final String COL_DATE = "date";

    private Integer mid_gameplay;
    private String mdate;

    public Gameplay() {
    }

    public Gameplay(Integer id_gameplay, String date) {
        this.mid_gameplay = id_gameplay;
        this.mdate = date;
    }

    public Integer getid_gameplay() {
        return mid_gameplay;
    }

    public void setid_gameplay(Integer id_gameplay) {
        this.mid_gameplay = id_gameplay;
    }

    public String getdate() {
        return mdate;
    }

    public void setdate(String date) {
        this.mdate = date;
    }


    public Bundle toBundle() { 
        Bundle b = new Bundle();
        b.putInt(COL_ID_GAMEPLAY, this.mid_gameplay);
        b.putString(COL_DATE, this.mdate);
        return b;
    }

    public Gameplay(Bundle b) {
        if (b != null) {
            this.mid_gameplay = b.getInt(COL_ID_GAMEPLAY);
            this.mdate = b.getString(COL_DATE);
        }
    }

    @Override
    public String toString() {
        return "Gameplay{" +
            " mid_gameplay=" + mid_gameplay +
            ", mdate='" + mdate + '\'' +
            '}';
    }


}
