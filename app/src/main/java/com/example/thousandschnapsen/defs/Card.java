package com.example.thousandschnapsen.defs;

import com.example.thousandschnapsen.enums.CARD_TYPE;
import com.example.thousandschnapsen.enums.COLOR;

import java.util.Objects;

public class Card {

    private COLOR color;
    private CARD_TYPE cardType;

    public Card(COLOR color, CARD_TYPE cardType) {
        this.color = color;
        this.cardType = cardType;
    }

    public Card(Integer i){
        setCardType(CARD_TYPE.values()[i%6]);
        setColor(COLOR.values()[i/6]);
    }
    public Integer toInt(){
        return getCardType().ordinal()+6*getColor().ordinal();
    }

    public COLOR getColor() {
        return color;
    }

    public void setColor(COLOR color) {
        this.color = color;
    }

    public CARD_TYPE getCardType() {
        return cardType;
    }

    public void setCardType(CARD_TYPE cardType) {
        this.cardType = cardType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return color == card.color &&
                cardType == card.cardType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, cardType);
    }

    @Override
    public String toString() {
        return "Card{" +
                "color=" + color +
                ", cardType=" + cardType +
                '}';
    }
}
