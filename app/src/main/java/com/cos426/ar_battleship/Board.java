package com.cos426.ar_battleship;

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
            for(int i = 0; i<size; i++){
                if(shipLocations[i][y] != null) return false;
            }
        }else{
            for(int i = 0; i<size; i++){
                if(shipLocations[x][i] != null) return false;
            }
        }

        if(xAxis){
            for(int i = 0; i<size; i++){
               shipLocations[i][y] = newShip;
            }
        }else{
            for(int i = 0; i<size; i++){
                shipLocations[x][i] = newShip;
            }
        }
        livingShips++;
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
}
