package com.example.thousandschnapsen.enums;

public enum COLOR {
    HEARTS,DIAMONDS,CLUBS,SPADES;

    public String shortCut(){
        switch(this){
            case SPADES:return "\u2660";
            case HEARTS:return "\u2661";
            case DIAMONDS:return "\u2662";
            case CLUBS:return "\u2663";
            default: return "";
        }
    }

    public Integer reportedValue() {
        switch(this){
            case SPADES:return 40;
            case HEARTS:return 100;
            case DIAMONDS:return 80;
            case CLUBS:return 60;
            default: return 0;
        }
    }
}
