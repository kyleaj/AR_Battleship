package com.cos426.ar_battleship;

import android.view.View;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;

public class FireNode extends Node {

    public Node target = null;


    @Override
    public void onUpdate(FrameTime frameTime) {
        super.onUpdate(frameTime);
        Renderable renderer = this.getRenderable();
        if (target == null) {
            if (renderer instanceof ViewRenderable) {
                ((ViewRenderable)renderer).getView().setVisibility(View.INVISIBLE);
            }
            return;
        }
        if (renderer instanceof ViewRenderable) {
            ((ViewRenderable)renderer).getView().setVisibility(View.VISIBLE);
        }
        Vector3 targetPos = target.getWorldPosition();
        targetPos.y = targetPos.y + 0.2f;
        this.setWorldPosition(Vector3.lerp(this.getWorldPosition(), targetPos, 0.1F));
    }
}
