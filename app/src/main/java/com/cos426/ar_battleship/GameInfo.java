package com.cos426.ar_battleship;

import android.content.Intent;
import android.content.Context;
import android.app.Activity;
import android.app.ActivityManager;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

public class GameInfo implements Serializable {

    public enum State{PlacingShipsP1, PlacingShipsP2, SetPlayArea, AdjustingBoard, Player1Choosing, Player2Choosing, Debug};

    public static final int PIN_LENGTH = 5;
    public static final int BOARD_SIZE = 7;
    public String Gamepin;
    public int numPlayers;
    public boolean amIPlayer1;
    public int player1Score;
    public int player2Score;
    public State currState;
    public Board playerBoard;
    public Board player1Board;
    public Board player2Board;
    private int round;

    public boolean readyToStart; // Set to true once second player joined in 2 player game

    public GameInfo(String gamepin, int player_num) {
        Gamepin = gamepin;
        numPlayers = player_num;
        player1Score = 0;
        player2Score = 0;
        readyToStart = false;
        currState = State.SetPlayArea;
        playerBoard = new Board();
    }

    // Required by Firebase Realtime Database. Not otherwise to be used(!)
    public GameInfo() { }

    public static String GenerateGamePin() {
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < PIN_LENGTH; i++) {
            int letter = (int)(Math.random() * 26);
            pin.append((char)(letter + 'A'));
        }
        return pin.toString();
    }
    public void checkEndGame(Activity activity){
        if(checkLoseState()){
            Intent newIntent = new Intent(activity, WinActivity.class);
            activity.startActivity(newIntent);
            return;
        }
        if(checkWinState()){
            Intent newIntent = new Intent(activity, LossActivity.class);
            activity.startActivity(newIntent);
            return;
        }
    }
    private boolean checkWinState(){
        if(this.amIPlayer1){
            return (this.player2Board.livingShips == 0);
        }else{
            return (this.player1Board.livingShips == 0);
        }
    }
    private boolean checkLoseState(){
        return (this.playerBoard.livingShips == 0);
    }
    public void incrementRound(){round++;}
    public void haveAIShoot(){
        player1Board.shoot(round/BOARD_SIZE, round%BOARD_SIZE);
    }
}
