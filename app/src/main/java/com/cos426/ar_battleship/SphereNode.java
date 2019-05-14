package com.cos426.ar_battleship;

import android.content.Context;
import android.view.MotionEvent;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.ux.ArFragment;

public class SphereNode extends Node {

    private boolean containsShip;
    private boolean exposed;
    private ArFragment thisFragment;

    public SphereNode(boolean containsShip, ArFragment fragment) {
        this.containsShip = containsShip;
        thisFragment = fragment;
        exposed = false;
    }

    public void onTouched() {
        if (exposed) return;
        this.getRenderable().getMaterial().setFloat3(MaterialFactory.MATERIAL_COLOR, new Color(android.graphics.Color.YELLOW));
    }

    public void notTouched() {
        if (exposed) return;
        this.getRenderable().getMaterial().setFloat3(MaterialFactory.MATERIAL_COLOR, new Color(android.graphics.Color.BLACK));
    }

    public boolean onSelected() {
        if (containsShip) {
            this.getRenderable().getMaterial().setFloat3(MaterialFactory.MATERIAL_COLOR, new Color(android.graphics.Color.RED));
        } else {
            this.getRenderable().getMaterial().setFloat3(MaterialFactory.MATERIAL_COLOR, new Color(android.graphics.Color.WHITE));
            this.addChild(new ExplosionNode(thisFragment));
        }



        return containsShip;
    }
}
