package com.example.thousandschnapsen.enums;

public enum CARD_TYPE {
    ACE,TEN,KING,QUEEN,JACK,NINE;
    public Integer valueOf(){
        switch (this){
            case ACE:  return 11;
            case TEN:  return 10;
            case KING:  return 4;
            case QUEEN:  return 3;
            case JACK:  return 2;
            case NINE:  return 0;
            default: return 0;
        }
    }

    public String shortName(){
        switch (this){
            case ACE:  return "As";
            case TEN:  return "10";
            case KING: return  "Król";
            case QUEEN: return  "Dama";
            case JACK:  return "Walet";
            case NINE:  return "9";
            default: return "";
        }
    }

    public String shortCut(){
        switch (this){
            case ACE:  return "A";
            case TEN:  return "10";
            case KING: return  "K";
            case QUEEN: return  "Q";
            case JACK:  return "W";
            case NINE:  return "9";
            default: return "";
        }
    }

}
