package com.example.thousandschnapsen.DbSchnapsen;

import android.os.Bundle;
import java.util.Date;

public class Nicknames {

    public static final String COL_ID_NICKNAMES = "id_nicknames";
    public static final String COL_PHONE_MODEL = "phone_model";
    public static final String COL_NICKNAME = "nickname";

    private Integer mid_nicknames;
    private String mphone_model;
    private String mnickname;

    public Nicknames() {
    }

    public Nicknames(Integer id_nicknames, String phone_model, String nickname) {
        this.mid_nicknames = id_nicknames;
        this.mphone_model = phone_model;
        this.mnickname = nickname;
    }

    public Integer getid_nicknames() {
        return mid_nicknames;
    }

    public void setid_nicknames(Integer id_nicknames) {
        this.mid_nicknames = id_nicknames;
    }

    public String getphone_model() {
        return mphone_model;
    }

    public void setphone_model(String phone_model) {
        this.mphone_model = phone_model;
    }

    public String getnickname() {
        return mnickname;
    }

    public void setnickname(String nickname) {
        this.mnickname = nickname;
    }


    public Bundle toBundle() { 
        Bundle b = new Bundle();
        b.putInt(COL_ID_NICKNAMES, this.mid_nicknames);
        b.putString(COL_PHONE_MODEL, this.mphone_model);
        b.putString(COL_NICKNAME, this.mnickname);
        return b;
    }

    public Nicknames(Bundle b) {
        if (b != null) {
            this.mid_nicknames = b.getInt(COL_ID_NICKNAMES);
            this.mphone_model = b.getString(COL_PHONE_MODEL);
            this.mnickname = b.getString(COL_NICKNAME);
        }
    }

    @Override
    public String toString() {
        return "Nicknames{" +
            " mid_nicknames=" + mid_nicknames +
            ", mphone_model='" + mphone_model + '\'' +
            ", mnickname='" + mnickname + '\'' +
            '}';
    }


}
