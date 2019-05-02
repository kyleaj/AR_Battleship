package com.example.battleshipdemo;

import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;

public class BoatNode extends Node {

    Node parent;
    float yVelocity;
    float g = 9.8f;
    boolean animating;

    public BoatNode(float startHeight, Node parent) {
        Vector3 currPosition = this.getLocalPosition();
        currPosition.y += startHeight;
        this.setLocalPosition(currPosition);
        this.parent = parent;
        animating = true;
        this.yVelocity = 0;
        this.setLocalScale(this.getLocalScale().scaled(0.2f));
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        super.onUpdate(frameTime);
        if (animating) {
            float dt = frameTime.getDeltaSeconds();
            yVelocity = yVelocity + (dt * g);
            Vector3 currPosition = this.getLocalPosition();
            currPosition.y -= yVelocity * dt;
            this.setLocalPosition(currPosition);
            if (this.getWorldPosition().y < parent.getWorldPosition().y) {
                animating = false;
                this.getWorldPosition().y = parent.getWorldPosition().y;
            }
        }

    }

}
