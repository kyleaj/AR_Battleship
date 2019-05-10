package com.cos426.ar_battleship;

import java.util.ArrayList;
// Holds the game state for a player
public class Board {
    boolean[][] shots_fired;
    boolean[][] hits;
    Ship[][] shipLocations;
    int livingShips;
    ArrayList<Ship> ships;
    public Board(){
        shots_fired = new boolean[9][9];
        hits = new boolean[9][9];
        shipLocations = new Ship[9][9];
        livingShips = 0;
        ships = new ArrayList<Ship>();
    }

    public boolean shoot(int x, int y){
        if(x >= 9 || x < 0) return false;
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
    public boolean addShip(){
        return false;
    }
}
