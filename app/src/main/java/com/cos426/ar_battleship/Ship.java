package com.cos426.ar_battleship;
// Basically a ship on someone's board
public class Ship {

    // Required by Firebase realtime database. Not to be used otherwise.
    boolean[][] locations;
    boolean[] livingParts;
    int partsAlive;
    boolean isAlive;
    int startX;
    int startY;
    boolean xAxis;
    private final int BOARD_SIZE = GameInfo.BOARD_SIZE;

    public Ship(int size, int x, int y, boolean xAxis) {
        locations = new boolean[BOARD_SIZE][BOARD_SIZE];
        livingParts = new boolean[size];
        startX = x;
        startY = y;
        this.xAxis = xAxis;
        for(int i = 0; i < size; i++){
            livingParts[i] = true;
        }
        partsAlive = size;
        isAlive = true;
        if(xAxis){
            for(int i = 0; i<size; i++){
                locations[i][y] = true;
            }
        }else{
                for(int i = 0; i<size; i++){
                    locations[x][i] = true;
                }
            }
        }
    public boolean shoot(int x, int y){
        if(x >= BOARD_SIZE || x < 0) return false;
        if(y >= BOARD_SIZE || y < 0 ) return false;
        if(locations[x][y]){
            locations[x][y] = false;
            partsAlive--;
            if(partsAlive == 0) isAlive = false;
            if(this.xAxis){
                livingParts[x - startX] = false;
            }else{
                livingParts[y - startY] = false;
            }
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
    // query if a specific location has a ship part
    public boolean checkIfAliveAtLocation(int x, int y){
        if(xAxis) return livingParts[x];
        return livingParts[y];
    }
}
