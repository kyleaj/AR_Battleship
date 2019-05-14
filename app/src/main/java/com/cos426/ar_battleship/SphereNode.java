package com.cos426.ar_battleship;

import android.view.MotionEvent;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;

public class SphereNode extends Node {

    private boolean containsShip;
    private boolean exposed;

    public SphereNode(boolean containsShip) {
        this.containsShip = containsShip;
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
        }



        return containsShip;
    }
}
