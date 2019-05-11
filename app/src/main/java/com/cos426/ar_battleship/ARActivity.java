package com.cos426.ar_battleship;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class ARActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private static final double MIN_OPENGL_VERSION = 3.0;

    // Gmae layout setup renderables
    private ModelRenderable bomb;
    private ViewRenderable bombULLabel;
    private ViewRenderable bombURLabel;
    private ViewRenderable bombBLLabel;
    private ViewRenderable bombBRLabel;

    // Setting up the playboard views
    private ImageView reticle;
    private TextView instructions;
    private Button ar_button;
    TransformableNode parentNode;

    // Game board variables
    private Node[] points;
    private AnchorNode[] extraAnchors;
    private AnchorNode boardAnchor;

    public GameInfo gameInfo;


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

        CompletableFuture<ModelRenderable> bombBuilder = ModelRenderable.builder().setSource(this, R.raw.bomb).build();
        CompletableFuture<ViewRenderable> labelULbuilder = ViewRenderable.builder().setView(this, makeLabel("Upper Left")).build();
        CompletableFuture<ViewRenderable> labelURbuilder = ViewRenderable.builder().setView(this, makeLabel("Upper Right")).build();
        CompletableFuture<ViewRenderable> labelBLbuilder = ViewRenderable.builder().setView(this, makeLabel("Bottom Left")).build();
        CompletableFuture<ViewRenderable> labelBRbuilder = ViewRenderable.builder().setView(this, makeLabel("Bottom Right")).build();

        CompletableFuture.allOf(
                bombBuilder,
                labelULbuilder,
                labelURbuilder,
                labelBLbuilder,
                labelBRbuilder
        ).handle((result, throwable) -> {
            if(throwable != null) {
                Log.wtf("BattleshipDemo", "Can't load renderables!");
                return null;
            }

            try {
                bomb = bombBuilder.get();
                bombULLabel = labelULbuilder.get();
                bombURLabel = labelURbuilder.get();
                bombBLLabel = labelBLbuilder.get();
                bombBRLabel = labelBRbuilder.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            // Now set everything up
            instructions.setText("Move the device so the reticle points towards the center of the play area, then tap anywhere on the screen.");
            reticle.setVisibility(View.VISIBLE);
            arFragment.setOnTapArPlaneListener(this::setPlayArea);
            return null;
        });
    }

    public void setPlayArea(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
        if (gameInfo != null && gameInfo.currState == GameInfo.State.SetPlayArea) {
            Anchor anchor = plane.createAnchor(hitResult.getHitPose());
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());

            // Have to make multiple anchors for the board for the transformable nodes :/
            AnchorNode anchorNode1 = new AnchorNode(plane.createAnchor(hitResult.getHitPose()));
            AnchorNode anchorNode2 = new AnchorNode(plane.createAnchor(hitResult.getHitPose()));
            AnchorNode anchorNode3 = new AnchorNode(plane.createAnchor(hitResult.getHitPose()));
            AnchorNode anchorNode4 = new AnchorNode(plane.createAnchor(hitResult.getHitPose()));
            anchorNode1.setParent(arFragment.getArSceneView().getScene());
            anchorNode2.setParent(arFragment.getArSceneView().getScene());
            anchorNode3.setParent(arFragment.getArSceneView().getScene());
            anchorNode4.setParent(arFragment.getArSceneView().getScene());

            TransformableNode upperLeft = new TransformableNode(arFragment.getTransformationSystem());
            upperLeft.setRenderable(bomb);
            TransformableNode upperRight = new TransformableNode(arFragment.getTransformationSystem());
            upperRight.setRenderable(bomb);
            TransformableNode bottomLeft = new TransformableNode(arFragment.getTransformationSystem());
            bottomLeft.setRenderable(bomb);
            TransformableNode bottomRight = new TransformableNode(arFragment.getTransformationSystem());
            bottomRight.setRenderable(bomb);

            upperLeft.setLocalPosition(new Vector3(-0.5f, 0, 0.5f));
            bottomLeft.setLocalPosition(new Vector3(0.5f, 0, 0.5f));
            upperRight.setLocalPosition(new Vector3(-0.5f, 0, -0.5f));
            bottomRight.setLocalPosition(new Vector3(0.5f, 0, -0.5f));

            upperLeft.setParent(anchorNode1);
            upperRight.setParent(anchorNode2);
            bottomLeft.setParent(anchorNode3);
            bottomRight.setParent(anchorNode4);

            // Make labels

            points = new Node[]{upperLeft, upperRight, bottomLeft, bottomRight};
            extraAnchors = new AnchorNode[]{anchorNode1, anchorNode2, anchorNode3, anchorNode4};
            boardAnchor = anchorNode;

            // Change game state
            gameInfo.currState = GameInfo.State.AdjustingBoard;
            reticle.setVisibility(View.INVISIBLE);
            instructions.setText("Tap the center bomb to move, scale, and rotate the board. Click \"Done\" when done.");
            ar_button.setVisibility(View.VISIBLE);
            ar_button.setEnabled(true);
        }
    }

    private void setupPlayArea() {

    }

    private void handleArTap() {

    }

    private void debugState() {

    }

    private View makeLabel(String text) {
        FrameLayout layout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutparams=new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        layout.setLayoutParams(layoutparams);
        TextView label = new TextView(this);
        layout.setBackgroundColor(Color.WHITE);
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
//                new Vector3(0, 1, 1.5f),
//                new Vector3(0, 0, 2f),
//                new Vector3(0, 1, 2.5f),
//                new Vector3(0, 0, 3f)
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
            if (points == null || points.length != 4) {
                Log.e("BattleshipDemo", "Error: points array not defined or not correct length");
                return;
            }
            if (boardAnchor == null) {
                Log.e("BattleshipDemo", "Error: board anchor not setup!");
                return;
            }

            ar_button.setEnabled(false);
            ar_button.setVisibility(View.INVISIBLE);

            instructions.setText("");

            new GameBoardModel(Vector3.subtract(points[0].getWorldPosition(), boardAnchor.getWorldPosition()),
                    Vector3.subtract(points[1].getWorldPosition(), boardAnchor.getWorldPosition()),
                    Vector3.subtract(points[2].getWorldPosition(), boardAnchor.getWorldPosition()),
                    Vector3.subtract(points[3].getWorldPosition(), boardAnchor.getWorldPosition()),
                    boardAnchor,
                    this);
            for (AnchorNode bye : extraAnchors){
                bye.setParent(null);
                bye.getAnchor().detach();
            }
            arFragment.getArSceneView().getPlaneRenderer().setEnabled(false);
        }
    }
}
