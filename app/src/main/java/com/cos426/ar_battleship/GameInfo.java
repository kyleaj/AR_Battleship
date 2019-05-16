package com.cos426.ar_battleship;

import android.content.Intent;
import android.content.Context;
import android.app.Activity;
import android.app.ActivityManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
    public static GameInfo gameInfo;

    public boolean readyToStart; // Set to true once second player joined in 2 player game

    public GameInfo(String gamepin, int player_num) {
        Gamepin = gamepin;
        numPlayers = player_num;
        player1Score = 0;
        player2Score = 0;
        readyToStart = false;
        currState = State.SetPlayArea;
        playerBoard = new Board();
        player2Board = new Board();
        player1Board = this.playerBoard;
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
            Log.d("BattleShipDemo","Game has been lost");
            Intent newIntent = new Intent(activity, LossActivity.class);
            activity.startActivity(newIntent);
            return;
        }
        if(checkWinState()){
            Log.d("BattleShipDemo","Game has been won");
            Intent newIntent = new Intent(activity, WinActivity.class);
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
        Log.d("BattleShipDemo",String.format("AI shot %d,%d",round/BOARD_SIZE, round%BOARD_SIZE));
        player1Board.shoot(round/BOARD_SIZE, round%BOARD_SIZE);
    }
    public String getStateString(){
        if(currState == null) return "null";
        if(currState == State.PlacingShipsP1) return "PlacingShipsP1";
        if(currState == State.PlacingShipsP2) return "PlacingShipsP2";
        if(currState == State.Player1Choosing) return "Player1Choosing";
        if(currState == State.SetPlayArea) return "PlacingShipsP1";
        if(currState == State.AdjustingBoard) return "PlacingShipsP2";
        if(currState == State.Player2Choosing)  return "Player2Choosing";
        if(currState == State.Debug)  return "Debug";
        return "idk";
    }
}
