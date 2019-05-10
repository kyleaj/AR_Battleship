package com.cos426.ar_battleship;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button startGame;
    private Button joinGame;
    private Button debug;

    private GameInfo info;
    private DatabaseReference mDatabase;
    private ValueEventListener player2joined; // Listen for player 2 to join


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        startGame = findViewById(R.id.start_button);
        joinGame = findViewById(R.id.join_button);

        debug = findViewById(R.id.debug_button);
        if (BuildConfig.DEBUG) {
            debug.setVisibility(View.INVISIBLE);
        } else {
            debug.setOnClickListener(button -> {
                Intent intent = new Intent(this, ARActivity.class);
                startActivity(intent);
            });
        }
    }

    public void onClickStartGame(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Number of Players: ")
                .setItems(new String[]{"1 Player", "2 Players"}, (dialog1, which) -> {
                    switch (which) {
                        case 0: // One player
                            Intent intent = new Intent(this, ARActivity.class);
                            info = new GameInfo(GameInfo.GenerateGamePin(), 1);
                            info.amIPlayer1 = true;
                            intent.putExtra(getString(R.string.pass_game), info);
                            startActivity(intent);
                            break;
                        case 1:
                            info = new GameInfo(GameInfo.GenerateGamePin(), 1);
                            info.amIPlayer1 = true;
                            ProgressDialog.show(this, "Game Pin: " + info.Gamepin,
                                    "Waiting for 2nd player to join...", true, true,
                                    removeFromDatabase);
                            sendGameToDatabase(info);
                    }
                });
        dialog.show();
    }

    private void sendGameToDatabase(GameInfo gameInfo) {
        if (mDatabase == null) {
            Log.wtf("BattleshipDemo", "DATABSE NULL!?!?!");
            return;
        }
        mDatabase.child("games").child(gameInfo.Gamepin).setValue(gameInfo);
        player2joined = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean ready = dataSnapshot.getValue(Boolean.class);
                if (ready == null) {
                    Log.wtf("BattleshipDemo", "Snapshot somehow null/conversion failed");
                } else {
                    if (ready) {
                        info.readyToStart = true;
                        Intent intent = new Intent(getApplicationContext(), ARActivity.class);
                        intent.putExtra(getString(R.string.pass_game), info);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("BattleshipDemo", "Error in player2 waiter.", databaseError.toException());
                // TODO: Do something about this...
            }
        };
        mDatabase.child("games").child(gameInfo.Gamepin).child("readyToStart").addValueEventListener(player2joined);
    }

    private DialogInterface.OnCancelListener removeFromDatabase = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            if (player2joined != null) {
                mDatabase.child("games").child(info.Gamepin).child("readyToStart").removeEventListener(player2joined);
                player2joined = null;
                mDatabase.child("games").child(info.Gamepin).removeValue();
            }
        }
    };
}
