package com.cos426.ar_battleship;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.rendering.Color;


public class ARActivity extends AppCompatActivity {

    ArFragment arFragment;
    ModelRenderable boat;
    ModelRenderable redSphereRenderable;
    private static final double MIN_OPENGL_VERSION = 3.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ar_activity);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        ModelRenderable.builder()
                .setSource(this.getApplicationContext(), R.raw.old_boat)
                .build()
                .thenAccept(renderable -> boat = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        MaterialFactory.makeOpaqueWithColor(this, new Color(android.graphics.Color.RED))
                .thenAccept(
                        material -> {
                            redSphereRenderable =
                                    ShapeFactory.makeCube(new Vector3(10.0f, 0.0f, 10.0f), new Vector3(0.0f, 0.15f, 0.0f), material); });
        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (boat == null) {
                        return;
                    }
                    Material fakeMat = boat.getMaterial().makeCopy();
                    redSphereRenderable.setMaterial(fakeMat);
                    // Create the Anchor, make it attached to a plane
//                    Anchor anchor = plane.createAnchor(hitResult.getHitPose());
//                    AnchorNode anchorNode = new AnchorNode(anchor);
//                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                    // Debug FloatingNode
                    //createFloatingNodeTestScene(anchorNode, redSphereRenderable);
                    testRenderableDef(hitResult.getHitPose(), plane, hitResult);
                });
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
//        if (touchCount == 0) {
//            saved = new AnchorNode(plane.createAnchor(pose));
//            saved.setParent(arFragment.getArSceneView().getScene());
//            positions = new Vector3[4];
//            Log.d("BattleshipDemo", "Created center anchor");
//        } else if (touchCount < 5) {
//            Vector3 position = new AnchorNode(plane.createAnchor(pose)).getWorldPosition();
//            position = Vector3.subtract(position, saved.getWorldPosition());
//            positions[touchCount - 1] = position;
//            Log.d("BattleshipDemo", "Added position");
//        } else if(touchCount == 5) {
//            Log.d("BattleshipDemo", "About to make game board");
//            GameBoardModel model = new GameBoardModel(positions[0], positions[1], positions[2], positions[3], saved, getApplicationContext(), redSphereRenderable);
//        }
        saved = new AnchorNode(plane.createAnchor(pose));
        saved.setParent(arFragment.getArSceneView().getScene());
        GameBoardModel model = new GameBoardModel(new Vector3(0, 0, 0), Vector3.right(), Vector3.back(), Vector3.add(Vector3.right(), Vector3.back()), saved, getApplicationContext(), redSphereRenderable);
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
}
