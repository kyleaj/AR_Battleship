package com.cos426.ar_battleship;
// Basically a ship on someone's board
public class Ship {

    // Required by Firebase realtime database. Not to be used otherwise.
    boolean[][] locations;
    int partsAlive;
    boolean isAlive;

    public Ship(int size) {
        locations = new boolean[9][9];
        partsAlive = size;
        isAlive = true;
    }
    public boolean shoot(int x, int y){
        if(x >= 9 || x < 0) return false;
        if(y >= 9 || y < 0 ) return false;
        if(locations[x][y]){
            locations[x][y] = false;
            partsAlive--;
            if(partsAlive == 0) isAlive = false;
            return true;
        }
        return false;
    }
    public boolean isAlive(){
        return isAlive;
    }
    public boolean[][] getLocations(){
        return locations;
    }
}
