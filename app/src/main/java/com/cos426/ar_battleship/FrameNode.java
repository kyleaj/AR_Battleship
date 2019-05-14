package com.cos426.ar_battleship;

import android.util.Log;
import android.view.MotionEvent;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;

import java.util.Observer;

// Just allow touches to pass through
public class FrameNode extends Node {

    @Override
    public boolean onTouchEvent(HitTestResult hitTestResult, MotionEvent motionEvent) {
        return false;
    }
}
