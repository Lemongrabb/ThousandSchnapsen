package com.example.thousandschnapsen;

import com.example.thousandschnapsen.defs.Card;
import com.example.thousandschnapsen.defs.Const;
import com.example.thousandschnapsen.defs.MessageInfo;
import com.example.thousandschnapsen.enums.CARD_LOCATION_TYPE;
import com.example.thousandschnapsen.enums.COLOR;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

import static com.example.thousandschnapsen.defs.Const.NR;
import static com.example.thousandschnapsen.defs.Const.BID;

public class MessagesDispatcher extends Thread{
    GameSingleForm parentForm;
    Boolean stopServing=false;
    //QueueMessageBroadcaster qmr;


    MessageInfo lastMessage;

    Boolean firstTrickCache;
    Integer cardsInTrickCache; ;


    public MessagesDispatcher(GameSingleForm parentForm) {
        super();
        this.parentForm = parentForm;
    }


    @Override
    public void run() {
        while (!stopServing){
            Queue<MessageInfo> currentMessages = parentForm.getMessageBroadcaster().avaitingMessagesInQueue();
            while(!currentMessages.isEmpty()){
                MessageInfo m = currentMessages.peek();
                processMessage(m);
                m.setProcessed(parentForm.playerNr,true);
                currentMessages.remove();
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processMessage(MessageInfo m) {
        lastMessage=m;
        switch(m.getMessageType()){
            case CARDS_SHUFFLED:
                processShuffledCards(m);
                break;
            case FIRST_BID:
                enableFirstBid(m);
                break;
            case BID_RETURNED:
                processBidReturned(m);break;
            case BOMB:
                break;
            case BID:
                enableBid(m);
                break;
            case LAST_BID:
                endBidding(m);
                break;
            case DO_BOMB:
                actualizeResultsFromBomb(m);
                startNewPlay(m);
                break;
            case START_EXCHANGE:
                startExchange(m);
                break;
            case SEND_EXCHANGED_CARD:
                getExchangedCard(m);
                break;
            case START_TRICK:
                nextTrick(m);
                break;
            case CARD_ADDED:
                processCardOnTable(m);
                break;
            case DISPLAY_RESULTS:
                displayResults(m);
                break;
        }
    }

    private void displayResults(MessageInfo m) {
        //parentForm.gui.displayResults(m.getJsonParametres());
    }

    private void nextTrick(MessageInfo m) {
        cardsInTrickCache=0;
        firstTrickCache=false;
        parentForm.cards.get(CARD_LOCATION_TYPE.ON_TABLE_MY).clear();
        parentForm.cards.get(CARD_LOCATION_TYPE.ON_TABLE_B).clear();
        parentForm.cards.get(CARD_LOCATION_TYPE.ON_TABLE_C).clear();
        ArrayList<Integer> aL=new Gson().fromJson(m.getJsonParametres(),new TypeToken<ArrayList<Integer>>(){}.getType());
        Integer player=aL.get(0);

        if(aL.size()>1){
            Integer trump=aL.get(1);
            //parentForm.gui.showTrump(COLOR.values()[trump]);
        }

        if(parentForm.playerNr==player) {
            parentForm.putCardOntable(firstTrickCache);
        }

    }

    private void actualizeResultsFromBomb(MessageInfo m) {

    }

    private void processCardOnTable(MessageInfo m) {
        if(parentForm.playerNr==2){
            parentForm.playerNr=2;
        }
        cardsInTrickCache++;
        ArrayList <Integer> al=new Gson().fromJson(m.getJsonParametres(),new TypeToken<ArrayList<Integer>>(){}.getType());
        int playerNr=al.get(0);
        Card card=new Card(al.get(1));
        if(parentForm.isMaster){
            parentForm.actualTrick.put(playerNr,card);
            if(al.size()>2){
                COLOR trump = COLOR.values()[al.get(2)];
                parentForm.actualTrumpReportedInDeal.put(playerNr,trump);
                parentForm.actualTrump=trump;
            }
            if(cardsInTrickCache==3){
                actualizeResults();
            }
        }
        if(parentForm.playerNr!=playerNr) {
            ArrayList<Card> actualCards = parentForm.cards.get(parentForm.playerNrToLocation(playerNr));
            actualCards.remove(card);
            parentForm.cards.put(parentForm.playerNrToLocation(playerNr),actualCards);
            parentForm.cards.get(CARD_LOCATION_TYPE.values()[parentForm.playerNrToLocation(playerNr).ordinal() + 3]).add(card);
            //parentForm.gui.displayCards(parentForm.cards);
        }
        if (cardsInTrickCache < 3) {
            if (parentForm.playerNr == (playerNr+1)%3) {
                parentForm.putCardOntable(firstTrickCache);
            }
        }else {
            firstTrickCache=false;
        }

    }

    private void getExchangedCard(MessageInfo m) {
        firstTrickCache=true;
        ArrayList parameters=new Gson().fromJson(m.getJsonParametres(),new TypeToken<ArrayList>(){}.getType());
        int giver=((Double)parameters.get(3)).intValue();
        if(parentForm.playerNr!=giver) {
            if (parentForm.playerNr == ((Double) parameters.get(0)).intValue()) {
                Card card = new Card(((Double) parameters.get(1)).intValue());
                parentForm.cards.get(CARD_LOCATION_TYPE.MY_PLAYER).add(card);

                ArrayList<Card> actualCards = parentForm.cards.get(parentForm.playerNrToLocation(giver));
                actualCards.remove(card);
                parentForm.cards.put(parentForm.playerNrToLocation(giver),actualCards);

                parentForm.sortCardsByColorsAndTypes(parentForm.cards.get(CARD_LOCATION_TYPE.MY_PLAYER));
            } else {
                Card card = new Card(((Double) parameters.get(1)).intValue());
                CARD_LOCATION_TYPE giverLocation = parentForm.playerNrToLocation(giver);
                if(giverLocation==CARD_LOCATION_TYPE.PLAYER_B) {
                    parentForm.cards.get(CARD_LOCATION_TYPE.PLAYER_C).add(card);
                }
                else{
                    parentForm.cards.get(CARD_LOCATION_TYPE.PLAYER_B).add(card);
                }

                ArrayList<Card> actualCards = parentForm.cards.get(giverLocation);
                actualCards.remove(card);
                parentForm.cards.put(giverLocation,actualCards);
            }
            //parentForm.gui.displayCards(parentForm.cards,false);
        }
        if((Boolean) parameters.get(2)==true ){
            if(parentForm.playerNr==((Double)parameters.get(3)).intValue()) {
                parentForm.putCardOntable(firstTrickCache);
            }
        }
        cardsInTrickCache=0;
    }

    private void startExchange(MessageInfo m) {
        int player =new Gson().fromJson(m.getJsonParametres(),new TypeToken<Integer>(){}.getType());
        parentForm.getAdditionalCardsForPlayer(player);
        //parentForm.gui.displayCards(parentForm.cards,false);
        parentForm.exchangeCards(player);
    }

    private void startNewPlay(MessageInfo m) {
        parentForm.initDeal();
    }


    public MessageInfo getLastMessage() {
        return lastMessage;
    }

    private void endBidding(MessageInfo m) {
        HashMap<String,Integer> returnedBid=new Gson().fromJson(m.getJsonParametres(),new TypeToken<HashMap<String,Integer>>(){}.getType());
        Integer actualPlayer=returnedBid.get(NR);
        Integer lastBid=returnedBid.get(BID);
        //parentForm.gui.displayMessage(Const.BIDDING_FINISHED +actualPlayer+ Const.WITH_BID +lastBid);

        //parentForm.gui.setActionButtonsVisible(false);
        //parentForm.gui.displayCards(parentForm.cards,true);
        if(parentForm.isMaster){
            parentForm.initTrick(lastBid,actualPlayer,true);
        }
        if(lastBid== Const.INITIAL_VALUE){
            parentForm.checkBomb(actualPlayer);
        }
        else{
            parentForm.getAdditionalCardsForPlayer(actualPlayer);
            //parentForm.gui.displayCards(parentForm.cards,false);
            parentForm.exchangeCards(actualPlayer);
        }

    }


    private void processShuffledCards(MessageInfo m) {
        parentForm.convertNumbersToCards(new Gson().fromJson(m.getJsonParametres(),new TypeToken<ArrayList<Integer>>(){}.getType()));
        //parentForm.gui.displayCards(parentForm.cards,false);
    }

    private void enableFirstBid(MessageInfo m) {
        parentForm.enableActioButtonsForBid(new Gson().fromJson(m.getJsonParametres(),new TypeToken<Integer>(){}.getType()),100);
    }

    private void processBidReturned(MessageInfo m) {
        HashMap<String,Integer> returnedBid=new Gson().fromJson(m.getJsonParametres(),new TypeToken<HashMap<String,Integer>>(){}.getType());
        Integer actualPlayer=returnedBid.get(NR);
        Integer lastBid=returnedBid.get(BID);
        if(parentForm.isMaster) {
            parentForm.actualizeBidding(actualPlayer,lastBid);
        }
    }

    private void enableBid(MessageInfo m) {
        HashMap<String,Integer> returnedBid=new Gson().fromJson(m.getJsonParametres(),new TypeToken<HashMap<String,Integer>>(){}.getType());
        Integer actualPlayer=returnedBid.get(NR);
        Integer lastBid=returnedBid.get(BID);
        parentForm.enableActioButtonsForBid((++actualPlayer) % 3, lastBid);

    }


    private void actualizeResults() {
        parentForm.finishTrick();
    }
}
