package com.cos426.ar_battleship;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
// Holds the game state for a player
public class Board implements Serializable {
    boolean[][] shots_fired;
    boolean[][] hits;
    Ship[][] shipLocations;
    int livingShips;
    ArrayList<Ship> ships;
    private final int BOARD_SIZE = GameInfo.BOARD_SIZE;
    public Board(){
        shots_fired = new boolean[BOARD_SIZE][BOARD_SIZE];
        hits = new boolean[BOARD_SIZE][BOARD_SIZE];
        shipLocations = new Ship[BOARD_SIZE][BOARD_SIZE];
        livingShips = 0;
        ships = new ArrayList<Ship>();
    }

    public boolean shoot(int x, int y){
        if(x >= BOARD_SIZE || x < 0) return false;
        shots_fired[x][y] = true;
        if(shipLocations[x][y] != null){
            Ship shotShip = shipLocations[x][y];
            shipLocations[x][y] = null;
            shotShip.shoot(x,y);
            hits[x][y] = true;
            if(!shotShip.isAlive()) livingShips--;
            return true;
        }
        return false;
    }
//    Not exactly sure how to represent the list of
//    coordinates for the ship, But is supposed to add
//    the ship to the board and return false if it is not possible to
    public boolean addShip(int x, int y, int size, boolean xAxis){
        Ship newShip = new Ship(size,x,y,xAxis);
        if(xAxis){
            if(x < 0 || x + size >= BOARD_SIZE) return false;
            for(int i = x; i<x+size; i++){
                if(shipLocations[i][y] != null) return false;
            }
        }else{
            if(y < 0 || y + size >= BOARD_SIZE) return false;
            for(int i = y; i<size + y; i++){
                if(shipLocations[x][i] != null) return false;
            }
        }

        if(xAxis){
            for(int i = x; i<x+size; i++){
               shipLocations[i][y] = newShip;
            }
        }else{
            for(int i = y; i<y+size; i++){
                shipLocations[x][i] = newShip;
            }
        }
        livingShips++;
        Log.d("BattleShipDemo",String.format("living ships is now %d",livingShips));
        ships.add(newShip);
        return true;
    }
    public void resetBoard(){
        shots_fired = new boolean[BOARD_SIZE][BOARD_SIZE];
        hits = new boolean[BOARD_SIZE][BOARD_SIZE];
        shipLocations = new Ship[BOARD_SIZE][BOARD_SIZE];
        livingShips = 0;
        ships = new ArrayList<Ship>();
    }
    public void makeDummyBoard(){
        this.resetBoard();
        this.addShip(4,4,2,true);
        this.addShip(2,2,3,true);
    }
    public boolean checkForShip(int x, int y){
        if(shipLocations[x][y] == null) return false;
        return true;
    }
}
