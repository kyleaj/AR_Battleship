package com.cos426.ar_battleship;

import java.io.Serializable;

public class GameInfo implements Serializable {

    public static final int PIN_LENGTH = 5;

    public String Gamepin;
    public int numPlayers;
    public boolean amIPlayer1;
    public int player1Score;
    public int player2Score;

    public boolean readyToStart; // Set to true once second player joined in 2 player game

    public GameInfo(String gamepin, int player_num) {
        Gamepin = gamepin;
        numPlayers = player_num;
        player1Score = 0;
        player2Score = 0;
        readyToStart = false;
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
}
