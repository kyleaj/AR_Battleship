package com.cos426.ar_battleship;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import com.google.ar.sceneform.rendering.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableDefinition;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.schemas.sceneform.MaterialDef;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class ARActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private static final double MIN_OPENGL_VERSION = 3.0;

    // Gmae layout setup renderables
    private ModelRenderable frame;
    private ViewRenderable confirmFire;
    private FireNode confirmFireNode;

    // Setting up the playboard views
    private ImageView reticle;
    private TextView instructions;
    private Button ar_button;

    // Game board variables
    private AnchorNode boardAnchor;
    private Node boardNode;

    public GameInfo gameInfo;

    // Keep track of interactions
    public SphereNode lastTouched; // The last node touched


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        reticle = findViewById(R.id.reticle);
        instructions = findViewById(R.id.instructions);
        ar_button = findViewById(R.id.done_setting_button);

        Intent intent = getIntent();
        gameInfo = (GameInfo)intent.getSerializableExtra(getString(R.string.pass_game));
        gameInfo.currState = GameInfo.State.SetPlayArea;

        CompletableFuture<ModelRenderable> frameBuilder = ModelRenderable.builder().setSource(this, R.raw.pic_frame).build();
//        LinearLayout fire_it = new LinearLayout(this);
//        Button fireButton = new Button(this);
//        fireButton.setText("Fire?");
//        fireButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                takeAShot();
//            }
//        });

        CompletableFuture<ViewRenderable> fireBuilder = ViewRenderable.builder().setView(this, R.layout.confirm_fire).build();

        CompletableFuture.allOf(
                frameBuilder, fireBuilder
        ).handle((result, throwable) -> {
            if(throwable != null) {
                Log.wtf("BattleshipDemo", "Can't load renderables!");
                return null;
            }

            try {
                frame = frameBuilder.get();
                confirmFire = fireBuilder.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            ((Button)confirmFire.getView().findViewById(R.id.confirm_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takeAShot();
                }
            });

            // Now set everything up
            instructions.setText("Move the device so the reticule points towards the center of the play area, then tap anywhere on the screen.");
            reticle.setVisibility(View.VISIBLE);
            arFragment.setOnTapArPlaneListener(this::setPlayArea);

            confirmFireNode = new FireNode();
            confirmFireNode.setRenderable(confirmFire);

            return null;
        });
    }

    public void setPlayArea(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        if (gameInfo != null && gameInfo.currState == GameInfo.State.SetPlayArea) {
            Anchor anchor = plane.createAnchor(hitResult.getHitPose());
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            // Make labels
            boardAnchor = anchorNode;
            boardNode = new TransformableNode(arFragment.getTransformationSystem());
            boardNode.setRenderable(frame);
            boardNode.setParent(boardAnchor);

            // Change game state
            gameInfo.currState = GameInfo.State.AdjustingBoard;
            reticle.setVisibility(View.INVISIBLE);
            instructions.setText("Pinch and drag to change the size and move the board to a comfortable position.");
            ar_button.setVisibility(View.VISIBLE);
            ar_button.setEnabled(true);
        }
    }

    private void setupPlayArea() {
        float width = boardNode.getWorldScale().x;
        float height = boardNode.getWorldScale().z;

        // 7 x 7 board
        float dw = width / (GameInfo.BOARD_SIZE);
        float dy = height / (GameInfo.BOARD_SIZE);
        float startX = -width/2;
        float startY = -height/2;

        SphereNode[][] positions = new SphereNode[GameInfo.BOARD_SIZE][GameInfo.BOARD_SIZE];

        MaterialFactory.makeOpaqueWithColor(this, new Color(0, 0, 0)).handle(
                ((material, throwable) -> {

                    if (throwable != null) {
                        Log.e("BattleshipDemo", "Couldn't make sphere material!");
                    }

                    for (int x = 0; x < GameInfo.BOARD_SIZE; x++) {
                        for (int y = 0; y < GameInfo.BOARD_SIZE; y++) {
                            // TODO: Fill this in with if it actually contains a ship or not
                            positions[x][y] = new SphereNode(true, arFragment, (y * GameInfo.BOARD_SIZE) + x, gameInfo);
                            Renderable sphere = ShapeFactory.makeSphere(width * 0.7f / 14f, new Vector3(0, 0, 0), material.makeCopy());
                            positions[x][y].setRenderable(sphere);
                            positions[x][y].setLocalPosition(new Vector3((dw * x) + startX + 1, 0,(dy *y) + startY));
                            positions[x][y].setParent(boardNode);
                            positions[x][y].listenForChanges(new Observer() {
                                @Override
                                public void update(Observable o, Object arg) {
                                    handleArTap((SphereNode.SphereNodeTouchedEvent)o, (Boolean)arg);
                                }
                            });
                        }
                    }

                    return null;
                })
        );
    }

    private void handleArTap(SphereNode.SphereNodeTouchedEvent tappedEvent, boolean containsShip) {
        if (lastTouched!= null && lastTouched != tappedEvent.thisNode)
            lastTouched.notTouched();
        lastTouched = tappedEvent.thisNode;
        confirmFireNode.target = lastTouched;
    }

    private void debugState() {

    }

    private void checkEndGame(){
        if(!((gameInfo.currState == GameInfo.State.Player1Choosing && gameInfo.amIPlayer1)
        ||(gameInfo.currState == GameInfo.State.Player2Choosing && !gameInfo.amIPlayer1))) return;
        if(checkLoseState()){
            Intent newIntent = new Intent(this, WinActivity.class);
            startActivity(newIntent);
            return;
        }
        if(checkWinState()){
            Intent newIntent = new Intent(this, LossActivity.class);
            startActivity(newIntent);
            return;
        }
    }

    private boolean takeAShot(){
        lastTouched.onSelected();
        confirmFireNode.target = null;
        int id = lastTouched.id;
        int x = id % GameInfo.BOARD_SIZE; // I regret not just saving x and y.
        int y = id / GameInfo.BOARD_SIZE;
        Board board;
        if(gameInfo.amIPlayer1){
            board = gameInfo.player2Board;
        }else{
            board = gameInfo.player1Board;
        }
        return board.shoot(x,y);
    }
    private boolean checkWinState(){
        if(gameInfo.amIPlayer1){
            return (gameInfo.player2Board.livingShips == 0);
        }else{
            return (gameInfo.player1Board.livingShips == 0);
        }
    }
    private boolean checkLoseState(){
        return (gameInfo.playerBoard.livingShips == 0);
    }
    private View makeLabel(String text) {
        FrameLayout layout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutparams=new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        layout.setLayoutParams(layoutparams);
        TextView label = new TextView(this);
        layout.setBackgroundColor(android.graphics.Color.WHITE);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        layout.setLayoutParams(params);
        layout.addView(label);
        return layout;
    }


    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e("BattleDemo", "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e("BattleShip", "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    int touchCount = 0;
    AnchorNode saved;
    Vector3[] positions;
    private void testRenderableDef(Pose pose, Plane plane, HitResult result) {
        if (touchCount == 0) {
            saved = new AnchorNode(plane.createAnchor(pose));
            saved.setParent(arFragment.getArSceneView().getScene());
            positions = new Vector3[4];
            Log.d("BattleshipDemo", "Created center anchor");
        } else if (touchCount < 5) {
            Vector3 position = new AnchorNode(plane.createAnchor(pose)).getWorldPosition();
            position = Vector3.subtract(position, saved.getWorldPosition());
            positions[touchCount - 1] = position;
            Log.d("BattleshipDemo", "Added position");
        } else if(touchCount == 5) {
            Log.d("BattleshipDemo", "About to make game board");
            GameBoardModel model = new GameBoardModel(positions[0], positions[1], positions[2], positions[3], saved, getApplicationContext());
        }
        touchCount++;
    }

    private static void createFloatingNodeTestScene(AnchorNode node, Renderable renderable) {
        // Create
        Vector3 upPos = new Vector3(0.5f, 1, 0);
        Vector3 downPos = new Vector3(-0.5f, -1, 0);
        Vector3 neutralPos = new Vector3(0, 0, 0);
        Vector3 regularPos = new Vector3(1f, 0, 0);
        Vector3 pathPos = new Vector3(-1f, 0, 0);

        Vector3[] path = new Vector3[]{new Vector3(0, 0, 0),
                new Vector3(0, 1, 0.5f),
                new Vector3(0, 0, 1),
        };

        FloatingNode up = new FloatingNode(node, FloatingNode.FloatState.Floating, upPos);
        up.setRenderable(renderable);
        FloatingNode down = new FloatingNode(node, FloatingNode.FloatState.Floating, downPos);
        down.setRenderable(renderable);
        FloatingNode neutral = new FloatingNode(node, FloatingNode.FloatState.Floating, neutralPos);
        neutral.setRenderable(renderable);
        neutral.setVisibility(false);
        FloatingNode pathNode = new FloatingNode(node, path, pathPos);
        pathNode.setRenderable(renderable);

        FloatingNode regular = new FloatingNode(node, regularPos);
        regular.setRenderable(renderable);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                regular.setVisibility(true);
                regular.setState(FloatingNode.FloatState.Floating);
            }
        }, 5000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                up.setState(FloatingNode.FloatState.Gone);
            }
        }, 7000);
    }

    public void onClickDone(View view) {
        if(gameInfo != null && gameInfo.currState == GameInfo.State.AdjustingBoard) {
            if (boardAnchor == null) {
                Log.e("BattleshipDemo", "Error: board anchor not setup!");
                return;
            }

            ar_button.setEnabled(false);
            ar_button.setVisibility(View.INVISIBLE);

            instructions.setText("");

            Node newNode = new FrameNode();
            newNode.setRenderable(frame);
            newNode.setLocalRotation(boardNode.getLocalRotation());
            newNode.setLocalPosition(boardNode.getLocalPosition());
            newNode.setLocalScale(boardNode.getLocalScale());
            newNode.setParent(boardAnchor);

            boardNode.setParent(null);
            boardNode = newNode;

            arFragment.getArSceneView().getPlaneRenderer().setEnabled(false);

            setupPlayArea();

            gameInfo.currState = GameInfo.State.Player1Choosing;
        }
    }
}
