package com.cos426.ar_battleship;

import android.content.Context;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GameBoardModel {

    private static final int DEPTH = 2; // How low should the hole go?

    // Create a game board "hole" model/hole in floor. Takes four points to draw the board with and the anchor holding them
    public GameBoardModel(Vector3 topLeft, Vector3 topRight, Vector3 bottomLeft, Vector3 bottomRight, AnchorNode anchorNode, Context context) {
//        Vector3 downDirection = anchorNode.getDown().normalized();
        // Let's draw a plane first
        Vector3 upDirection = anchorNode.getDown();

//        Vector3 downTopLeft = Vector3.subtract(topLeft, downDirection.scaled(DEPTH));
//        Vector3 downTopRight = Vector3.subtract(topLeft, downDirection.scaled(DEPTH));
//        Vector3 downBottomLeft = Vector3.subtract(topLeft, downDirection.scaled(DEPTH));
//        Vector3 downBottomRight = Vector3.subtract(topLeft, downDirection.scaled(DEPTH));

        Color black = new Color(0, 0, 0);
        Color red = new Color(1, 0, 0);
        Color green = new Color(0, 1, 0);
        Color blue = new Color(0, 0, 1);

        ArrayList<Vertex> vertices = new ArrayList<>(4);
        vertices.add(Vertex.builder().setPosition(topLeft).setNormal(upDirection).setUvCoordinate(new Vertex.UvCoordinate(1, 1)).build());
        vertices.add(Vertex.builder().setPosition(topRight).setNormal(upDirection).setUvCoordinate(new Vertex.UvCoordinate(1, 0)).build());
        vertices.add(Vertex.builder().setPosition(bottomLeft).setNormal(upDirection).setUvCoordinate(new Vertex.UvCoordinate(0, 1)).build());
        vertices.add(Vertex.builder().setPosition(bottomRight).setNormal(upDirection).setUvCoordinate(new Vertex.UvCoordinate(0, 0)).build());
        Log.d("BattleshipDemo", "Built vertices");

        ArrayList<Integer> triangleIndices = new ArrayList<>(Arrays.asList(0, 2, 1, 2, 3, 1));

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
            // TODO: VBO Order?
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
//                        Node node2 = new Node();
//                        node.setRenderable(second);
//                        node.setParent(anchorNode);
                        return null;
                    }));
            return null;
        });
    }
}
