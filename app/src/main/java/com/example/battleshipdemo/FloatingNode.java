package com.example.battleshipdemo;

import android.os.Debug;
import android.util.Log;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;

import java.io.Console;
import java.util.Vector;


// TODO: Make visibility do something
/**
 * Simulates the motion of an item floating in water - Buoyancy, Gravity, and a Bobbing (waves)
 * @author kyleaj
 * @version 1.0
 */
public class FloatingNode extends Node {
    /**
     * Keep a track of the possible states of the node. It's either
     * Floating, Submerged, Gone (exploaded?), or tracing a path (not implemented yet)
     */
    public enum FloatState{Submerged, Floating, Gone, PathTracing};

    // Acceleration due to gravity.
    private static final float G = -8f;
    private static final float B = 14f; // Buoyant force
    private static final float water_drag = 0.7f; // Drag due to water

    // Is the boat visible?
    private boolean visibility;
    // What state is the boat currently in?
    private FloatState state;
    // What is the boat's current velocity?
    private Vector3 Velocity;
    // Is the boat animating?
    private boolean animating;
    // The parent of the node
    private final Node parent;
    // The path the node should follow in hte path tracing state. An array of local displacements.
    private Vector3[] path;
    // Original local position, the equilibrium state
    private final Vector3 origin;
    // Keep track of bobbing offsets, just to make updates easy and to avoid drifting
    private Vector3 wave;
    // Speed/how fast we travel to each point
    private float speed = 1f;
    // Creation time
    private long creationTime;


    /**
     * Creates a node with the default starting values: submerged at -0.5 meters invisible,
     * animating, and Velocity of zero.
     * For best results, consider making the parent an Anchornode and set the Anchor node to
     * be attatched to the water plane.
     * @param parent The parent node.
     */
    public FloatingNode(Node parent, Vector3 pos) {
        this.setParent(parent);
        creationTime = System.currentTimeMillis();
        this.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f));
        origin = Vector3.add(new Vector3(), pos); // Prevent any accidental edits
        visibility = false;
        state = FloatState.Submerged;
        this.parent = parent;
        this.animating = true;
        this.Velocity = new Vector3();
        pos.y = -0.5f;
        this.setLocalPosition(pos);
        wave = new Vector3();
    }

    /**
     * Creates a node with the current state and makes it visible,
     * animating, and with an initial velocity of zero.
     * For best results, consider making the parent an Anchornode and set the Anchor node to
     * be attatched to the water plane.
     * @param parent The parent node.
     * @param state The initial state of the node.
     */
    public FloatingNode(Node parent, FloatState state, Vector3 pos) {
        this.setParent(parent);
        creationTime = System.currentTimeMillis();
        this.setLocalPosition(pos);
        this.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f));
        origin = Vector3.add(new Vector3(), pos); // Prevent any accidental edits
        visibility = true;
        this.state = state;
        this.parent = parent;
        this.animating = true;
        this.Velocity = new Vector3();
        wave = new Vector3();
    }

    /**
     * Creates a node in the path tracing state.
     * @param parent The parent node.
     * @param path The path for the node to follow
     */
    public FloatingNode(Node parent, Vector3[] path, Vector3 pos) {
        this.setParent(parent);
        creationTime = System.currentTimeMillis();
        this.setLocalPosition(pos);
        this.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f));
        origin = Vector3.add(new Vector3(), pos); // Prevent any accidental edits
        visibility = true;
        this.state = FloatState.PathTracing;
        this.path = path;
        this.parent = parent;
        this.animating = true;
        this.Velocity = new Vector3();
        wave = new Vector3();
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public void setState(FloatState state) {
        this.state = state;
    }

    public FloatState getState() {
        return state;
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        super.onUpdate(frameTime);

        if (animating) {
            switch (state) {
                case Submerged:
                    break;
                case Floating:
                    float_(frameTime);
                    break;
                case Gone:
                    this.setParent(null);
                    break;
                case PathTracing:
                    tracePath(frameTime);
                    break;
            }
        }

    }

    private void float_(FrameTime frameTime) {
        float amplitude = 0.001f; // Set max movement to 5 centimeters
        float frequency = (float)(Math.PI * 0.1F); // Set frequency to 2 seconds.
        float u_time = frameTime.getStartSeconds();
        float dt = frameTime.getDeltaSeconds();
        float currY = this.getLocalPosition().y;

        // Calculate bobbing updates
        float y = mixed_float_movement(origin.x, amplitude, frequency, u_time) - origin.x;
        float x = cos_float_movement(origin.z, amplitude, frequency, u_time) - origin.y;
        float z = sin_float_movement((y * x) + (origin.x * origin.z + origin.y) , amplitude, frequency, u_time) - origin.z;

        Vector3 bob = Vector3.subtract(new Vector3(), new Vector3(x, 0, z)); // Keep below the water
        Vector3 bobUpdate = Vector3.subtract(bob, wave).normalized().scaled(0.001f);
        wave = bob;

        // Calculate gravity offsets, only if it's above the ground
        Vector3 gravityUpdate = new Vector3();
        Vector3 buoyantUpdate = new Vector3();
        if (currY > 0.01) {
            Velocity.y = Velocity.y + (dt * G);
            gravityUpdate.y = Velocity.y * dt;
        }

        // Calculate buoyant force
        // This isn't physically accurate, it'll just get something that'll look about correct.
        else if (currY < -0.01) {
            Velocity.y = Velocity.y + (dt * B * Math.abs(currY)); // We'll say the further down it
                                                // is, the more motivation it has to come back up.
            Velocity.y = Velocity.y * water_drag;
            buoyantUpdate.y = Velocity.y * dt;
        }

        // Sum the forces and update the position
        Vector3 update = Vector3.add(bobUpdate, Vector3.add(gravityUpdate, buoyantUpdate));
        this.setLocalPosition(Vector3.add(this.getLocalPosition(), update));
    }

    // Insipred by: https://thebookofshaders.com/13/
    private float sin_float_movement(float x, float frequency, float amplitude, float u_time) {
        float y = (float)Math.sin(x * frequency);
        float t = 0.01f*(-u_time*130.0f);
        y += Math.sin(x*frequency*2.1 + t)*4.5;
        y += Math.sin(x*frequency*1.72 + t*1.121)*4.0;
        y += Math.sin(x*frequency*2.221 + t*0.437)*5.0;
        y += Math.sin(x*frequency*3.1122+ t*4.269)*2.5;
        y *= amplitude;
        return y;
    }

    private float cos_float_movement(float x, float frequency, float amplitude, float u_time) {
        float y = (float)Math.cos(x * frequency);
        float t = 0.01f*(-u_time*130.0f);
        y += Math.cos(x*frequency*2.1 + t)*4.5;
        y += Math.cos(x*frequency*1.72 + t*1.121)*4.0;
        y += Math.cos(x*frequency*2.221 + t*0.437)*5.0;
        y += Math.cos(x*frequency*3.1122+ t*4.269)*2.5;
        y *= amplitude;
        return y;
    }

    private float mixed_float_movement(float x, float frequency, float amplitude, float u_time) {
        float y = (float)Math.sin(x * frequency);
        float t = 0.01f*(-u_time*130.0f);
        y += Math.cos(x*frequency*2.1 + t)*4.5;
        y += Math.sin(x*frequency*1.72 + t*1.121)*4.0;
        y += Math.cos(x*frequency*2.221 + t*0.437)*5.0;
        y += Math.sin(x*frequency*3.1122+ t*4.269)*2.5;
        y *= amplitude;
        return y;
    }

    public void setSpeed(float s) {
        this.speed = s;
    }

    // Pass through(near) the points at each time step
    // Estimate bezier curves, without having the calculation grow linearly with number of points
    // Right now pass from one point to the next every second.
    // TODO: Make it work better for more than three points
    private void tracePath(FrameTime frameTime) {
        if (path == null) {
            throw new IllegalArgumentException("Points not defined in FloatingNode path tracer!");
        }
        if (path.length < 3) {
            throw new IllegalArgumentException("FloatingNode: Path must have at least 3 points!");
        }
        if (path.length != 3) {
            Log.e("BattleshipDemo", "Got a curve with more than three points. This'll look jittery right now, unfortunatley.");
        }

        // Weight each point in accordance to how close we are to the timestep it should be at
        float currTime = (System.currentTimeMillis() - creationTime) / 1000f;
        int roundedTime = Math.round(currTime);
        int currPoint = roundedTime % path.length;

        if (path.length == 3) {
            currPoint = 1;
        }

        Vector3 p1 = path[currPoint];
        Vector3 p2 = path[(currPoint + 1) % path.length];
        Vector3 p0 = path[(currPoint + path.length - 1) % path.length];

        float t = (currTime % 3f)/3f;

        float x = (1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x;
        float y = (1 - t) * (1 - t) * p0.y + 2 * (1 - t) * t * p1.y + t * t * p2.y;
        float z = (1 - t) * (1 - t) * p0.z + 2 * (1 - t) * t * p1.z + t * t * p2.z;

        //Log.v("BattleShip", x + ", " + y + ", " + z);

        this.setLocalPosition(new Vector3(x, y, z));
    }
}
