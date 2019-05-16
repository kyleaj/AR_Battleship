package com.cos426.ar_battleship;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PlacementActivity extends Activity implements OnClickListener {
   /**
    private ArrayList<ToggleButton> mButtons = new ArrayList<>();
    /** Called when the activity is first created. */
   private GameInfo gameInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.placement_activity);
        Button btn = (Button) findViewById(R.id.shipConfirmation);
        Intent intent = getIntent();
        gameInfo = GameInfo.gameInfo;
        Board playerBoard = gameInfo.playerBoard;
        gameInfo.currState = GameInfo.State.PlacingShipsP1;
        EditText x1 = (EditText) findViewById(R.id.carrierX);
        EditText x2 = (EditText) findViewById(R.id.battleShipX);
        EditText x3 = (EditText) findViewById(R.id.cruiserX);
        EditText x4 = (EditText) findViewById(R.id.subX);
        EditText x5 = (EditText) findViewById(R.id.destroyerX);
        EditText y1 = (EditText) findViewById(R.id.carrierY);
        EditText y2 = (EditText) findViewById(R.id.battleShipY);
        EditText y3 = (EditText) findViewById(R.id.cruiserY);
        EditText y4 = (EditText) findViewById(R.id.subY);
        EditText y5 = (EditText) findViewById(R.id.destoryerY);
        Checkable xAligned1 = (Checkable) findViewById(R.id.xAlignedCarrier);
        Checkable xAligned2 = (Checkable) findViewById(R.id.xAlignedBattleship);
        Checkable xAligned3 = (Checkable) findViewById(R.id.xAlignedCruiser);
        Checkable xAligned4 = (Checkable) findViewById(R.id.xAlignedSub);
        Checkable xAligned5 = (Checkable) findViewById(R.id.xAlignedDestroyer);
        x1.setText("0");
        x2.setText("1");
        x3.setText("2");
        x4.setText("3");
        x5.setText("4");
        y1.setText("0");
        y2.setText("1");
        y3.setText("2");
        y4.setText("3");
        y5.setText("4");

        btn.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {


                if(TextUtils.isEmpty(x1.getText())){
                    x1.setError("X coordinate for Battleship is required");
                }else if(TextUtils.isEmpty(x2.getText())){
                    x2.setError("X coordinate for Cruiser is required");
                }else if(TextUtils.isEmpty(x3.getText())){
                    x3.setError("X coordinate for Submarine is required");
                }else if(TextUtils.isEmpty(x4.getText())){
                    x4.setError("X coordinate for Destroyer is required");
                }else if(TextUtils.isEmpty(x5.getText())){
                    x5.setError("X coordinate for Carrier is required");
                }else if(TextUtils.isEmpty(y1.getText())){
                    y2.setError("Y coordinate for Carrier is required");
                }else if(TextUtils.isEmpty(y2.getText())){
                    y3.setError("Y coordinate for Battleship is required");
                }else if(TextUtils.isEmpty(y3.getText())){
                    y3.setError("Y coordinate for Cruiser is required");
                }else if(TextUtils.isEmpty(y4.getText())){
                    y4.setError("Y coordinate for Submarine is required");
                }else if(TextUtils.isEmpty(y5.getText())){
                    y5.setError("Y coordinate for Destroyer is required");
                }else {
                    boolean ret = true;
                    boolean  temp = true;
                    temp = playerBoard.addShip(Integer.parseInt(x1.getText().toString())
                            ,Integer.parseInt(y1.getText().toString()),4,
                            xAligned1.isChecked());
                    temp = ret && temp;
                    ret = playerBoard.addShip(Integer.parseInt(x2.getText().toString())
                            ,Integer.parseInt(y2.getText().toString()),3,
                            xAligned2.isChecked());
                    temp = ret && temp;
                    ret = playerBoard.addShip(Integer.parseInt(x3.getText().toString())
                            ,Integer.parseInt(y3.getText().toString()),2,
                            xAligned3.isChecked());
                    temp = ret && temp;
                    ret = playerBoard.addShip(Integer.parseInt(x4.getText().toString())
                            ,Integer.parseInt(y4.getText().toString()),2,
                            xAligned4.isChecked());
                    temp = ret && temp;
                    ret = playerBoard.addShip(Integer.parseInt(x5.getText().toString())
                            ,Integer.parseInt(y5.getText().toString()),1,
                            xAligned5.isChecked());
                    temp = ret && temp;
                    if(!temp){
                        playerBoard.resetBoard();
                        Toast.makeText(getBaseContext()," ship placement was not valid", Toast.LENGTH_SHORT).show();
                    }else{
                        gameInfo.player2Board.makeDummyBoard();
                        Intent intent = new Intent(getApplicationContext(), ARActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
    }

}
