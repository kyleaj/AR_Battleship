package com.cos426.ar_battleship;

import android.content.Context;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableDefinition;
import com.google.ar.sceneform.rendering.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GameBoardModel {

    private static final int DEPTH = 2; // How low should the hole go?

    // Create a game board "hole" model/hole in floor. Takes four points to draw the board with and the anchor holding them
    public GameBoardModel(Vector3 topLeft, Vector3 topRight, Vector3 bottomLeft, Vector3 bottomRight, AnchorNode anchorNode, Context context) {
        Vector3 downDirection = anchorNode.getDown().normalized();
        // Let's draw a plane first
        Vector3 upDirection = anchorNode.getDown();

        Vector3 downTopLeft = Vector3.add(topLeft, downDirection.scaled(DEPTH));
        Vector3 downTopRight = Vector3.add(topRight, downDirection.scaled(DEPTH));
        Vector3 downBottomLeft = Vector3.add(bottomLeft, downDirection.scaled(DEPTH));
        Vector3 downBottomRight = Vector3.add(bottomRight, downDirection.scaled(DEPTH));

        Vector3 topLeftNormal = Vector3.subtract(bottomRight, topLeft).normalized();
        Vector3 topRightNormal = Vector3.subtract(bottomLeft, topRight).normalized();
        Vector3 bottomLeftNormal = Vector3.subtract(new Vector3(0, 0, 0), topLeftNormal);
        Vector3 bottomRightNormal = Vector3.subtract(new Vector3(0, 0, 0), topRightNormal);

        Vector3 downTopLeftNormal = Vector3.add(topLeftNormal, Vector3.up()).normalized();
        Vector3 downTopRightNormal = Vector3.add(topRightNormal, Vector3.up()).normalized();
        Vector3 downBottomLeftNormal = Vector3.add(bottomLeftNormal, Vector3.up()).normalized();
        Vector3 downBottomRightNormal = Vector3.add(bottomRightNormal, Vector3.up()).normalized();

        Color black = new Color(0, 0, 0);
        Color red = new Color(1, 0, 0);
        Color green = new Color(0, 1, 0);
        Color blue = new Color(0, 0, 1);

        ArrayList<Vertex> vertices = new ArrayList<>(8);
        vertices.add(Vertex.builder().setPosition(topLeft).setNormal(topLeftNormal).setUvCoordinate(new Vertex.UvCoordinate(1, 1)).build());
        vertices.add(Vertex.builder().setPosition(topRight).setNormal(topRightNormal).setUvCoordinate(new Vertex.UvCoordinate(1, 0)).build());
        vertices.add(Vertex.builder().setPosition(bottomLeft).setNormal(bottomLeftNormal).setUvCoordinate(new Vertex.UvCoordinate(0, 1)).build());
        vertices.add(Vertex.builder().setPosition(bottomRight).setNormal(bottomRightNormal).setUvCoordinate(new Vertex.UvCoordinate(0, 0)).build());
        vertices.add(Vertex.builder().setPosition(downTopLeft).setNormal(downTopLeftNormal).setUvCoordinate(new Vertex.UvCoordinate(1, 1)).build());
        vertices.add(Vertex.builder().setPosition(downTopRight).setNormal(downTopRightNormal).setUvCoordinate(new Vertex.UvCoordinate(1, 0)).build());
        vertices.add(Vertex.builder().setPosition(downBottomLeft).setNormal(downBottomLeftNormal).setUvCoordinate(new Vertex.UvCoordinate(0, 1)).build());
        vertices.add(Vertex.builder().setPosition(downBottomRight).setNormal(downBottomRightNormal).setUvCoordinate(new Vertex.UvCoordinate(0, 0)).build());
        Log.d("BattleshipDemo", "Built vertices");

        ArrayList<Integer> triangleIndices = new ArrayList<>(Arrays.asList(0, 4, 1, 1, 4, 5, 2, 3, 6, 3, 7, 6, 0, 2, 4, 4, 2, 6, 1, 5, 3, 3, 5, 7, 5, 4, 6, 5, 6, 7));

        Log.d("BattleshipDemo", "Creating material RenderableDefinition");

        CompletableFuture<Material> mat = MaterialFactory.makeOpaqueWithColor(context, green);


        mat.handle((material, throwable) -> {
            if (throwable != null) {
                Log.e("BattleshipDemo", "Error loading custom renderable1.");
                return null;
            }
            if (material == null) {
                Log.e("BattleshipDemo", "Material null!");
                return null;
            }
            Log.e("BattleshipDemo", "Material Loaded.");
            RenderableDefinition.Submesh submesh = RenderableDefinition.Submesh.builder()
                    .setMaterial(material)
                    .setName("Plane")
                    .setTriangleIndices(triangleIndices)
                    .build();

            RenderableDefinition def = RenderableDefinition.builder()
                    .setVertices(vertices)
                    .setSubmeshes(new ArrayList<RenderableDefinition.Submesh>(Arrays.asList(submesh)))
                    .build();

            ModelRenderable.builder()
                    .setSource(def)
                    .build().handle(((modelRenderable, throwable1) -> {
                        if (throwable1 != null) {
                            Log.e("BattleshipDemo", "Error loading custom renderable2.");
                            return null;
                        }
                        if (modelRenderable == null) {
                            Log.e("BattleshipDemo", "Model Renderable null!");
                            return null;
                        }
                        Log.d("BattlshipDemo", "About to set renderable to node...");
                        Node node = new Node();
                        node.setRenderable(modelRenderable);
                        node.setParent(anchorNode);
                        return null;
                    }));
            return null;
        });
    }
}
