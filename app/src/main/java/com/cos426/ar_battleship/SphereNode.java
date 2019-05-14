package com.cos426.ar_battleship;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class SphereNode extends Node {

    private final ArrayList<Observer> listeners;

    private boolean containsShip; // Is this node a part of a ship on the board?
    private boolean exposed; // Do we already know the status of this node?
    private ArFragment thisFragment;
    public final int id;
    private final GameInfo gameInfo;

    public SphereNode(boolean containsShip, ArFragment fragment, int id, GameInfo gameInfo) {
        listeners = new ArrayList<>();
        this.id = id;
        this.containsShip = containsShip;
        thisFragment = fragment;
        exposed = false;
        this.gameInfo = gameInfo;
    }

    // Subscribe to events: was this node tapped? etc.
    public void listenForChanges(Observer observer) {
        listeners.add(observer);
    }

    @Override
    public boolean onTouchEvent(HitTestResult hitTestResult, MotionEvent motionEvent) {
        // && ((gameInfo.currState==GameInfo.State.Player2Choosing && gameInfo.amIPlayer1)
        //                        || (gameInfo.currState==GameInfo.State.Player1Choosing && !gameInfo.amIPlayer1)))
        Log.d("BattleshipDemo", "Node Tapped");
        if (!exposed) {
            this.getRenderable().getMaterial().setFloat3(MaterialFactory.MATERIAL_COLOR, new Color(android.graphics.Color.YELLOW));
            SphereNodeTouchedEvent event = new SphereNodeTouchedEvent();
            event.thisNode = this;
            event.thisId = this.id;
            for (Observer listener : listeners) {
                listener.update(event, containsShip);
            }
        }
        return super.onTouchEvent(hitTestResult, motionEvent);
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

    public class SphereNodeTouchedEvent extends Observable {
        public SphereNode thisNode;
        public int thisId;
    }
}
