package com.cos426.ar_battleship;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;

public class ExplosionNode extends Node {

    Renderable thisRenderable;
    boolean done;

    float velocty = 1.01f; // 1.1 meters a second to expand?
    float counter = 0.0f;

    public ExplosionNode(ArFragment fragment) {
        thisRenderable = null;
        done = false;
        Texture.builder().setSource(fragment.getContext(), R.drawable.explosion).build().handle((texture, throwable) -> {
            // TODO: Add alpha channel to texture to make this semi transparent.
            MaterialFactory.makeTransparentWithTexture(fragment.getContext(), texture).handle((mat, throwable1) -> {
                this.setRenderable(ShapeFactory.makeSphere(0.01f, Vector3.zero(), mat));
                thisRenderable = this.getRenderable();
                fragment.getArSceneView().playSoundEffect(R.raw.explosion_sound);
                Vibrator v = (Vibrator) fragment.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 700 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(700, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(700);
                }
                return null;
            });
            return null;
        });

    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        super.onUpdate(frameTime);

        if (!done && thisRenderable!= null) {
            this.setLocalScale(this.getLocalScale().scaled(velocty));
            this.setLocalRotation(Quaternion.axisAngle(this.getLocalScale(), velocty));
            velocty = velocty * 1.5f;
            counter = counter + frameTime.getDeltaSeconds();
            if (counter > 0.7f) {
                done = true;
            }
        }


    }
}
