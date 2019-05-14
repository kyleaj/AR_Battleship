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
import com.google.ar.schemas.lull.ModelPipelineRenderableDef;
import com.google.ar.schemas.sceneform.MaterialDef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GameBoardModel {

    private static final float DEPTH = 0.1f; // How low should the hole go?

    // Create a game board "hole" model/hole in floor. Takes four points to draw the board with and the anchor holding them
    public GameBoardModel(Vector3 topLeft, Vector3 topRight, Vector3 bottomLeft, Vector3 bottomRight, AnchorNode anchorNode, Context context) {
        Vector3 downDirection = anchorNode.getDown().normalized();
        Vector3 upDirection = anchorNode.getUp().normalized();
        // Let's draw a plane first

        topLeft = Vector3.add(topLeft, upDirection.scaled(DEPTH));
        topRight = Vector3.add(topRight, upDirection.scaled(DEPTH));
        bottomLeft = Vector3.add(bottomLeft, upDirection.scaled(DEPTH));
        bottomRight = Vector3.add(bottomRight, upDirection.scaled(DEPTH));

        Vector3 downTopLeft = Vector3.add(topLeft, downDirection.scaled(DEPTH));
        Vector3 downTopRight = Vector3.add(topRight, downDirection.scaled(DEPTH));
        Vector3 downBottomLeft = Vector3.add(bottomLeft, downDirection.scaled(DEPTH));
        Vector3 downBottomRight = Vector3.add(bottomRight, downDirection.scaled(DEPTH));

        Vector3 topLeftNormal = Vector3.subtract(bottomRight, topLeft).normalized();
        Vector3 topRightNormal = Vector3.subtract(bottomLeft, topRight).normalized();
        Vector3 bottomLeftNormal = topRightNormal.scaled(-1);
        Vector3 bottomRightNormal = topLeftNormal.scaled(-1);

        Vector3 downTopLeftNormal = Vector3.add(topLeftNormal, Vector3.up()).normalized();
        Vector3 downTopRightNormal = Vector3.add(topRightNormal, Vector3.up()).normalized();
        Vector3 downBottomLeftNormal = Vector3.add(bottomLeftNormal, Vector3.up()).normalized();
        Vector3 downBottomRightNormal = Vector3.add(bottomRightNormal, Vector3.up()).normalized();


//        Vector3 occludeTopLeft = Vector3.lerp(topLeft, Vector3.add(topLeft, topLeftNormal.scaled(-1)), 0.1f);
//        Vector3 occludeTopRight = Vector3.lerp(topRight, Vector3.add(topRight, topRightNormal.scaled(-1)), 0.1f);
//        Vector3 occludeBottomLeft = Vector3.lerp(bottomLeft, Vector3.add(bottomLeft, bottomLeftNormal.scaled(-1)), 0.1f);
//        Vector3 occludeBottomRight = Vector3.lerp(bottomRight, Vector3.add(bottomRight, bottomRightNormal.scaled(-1)), 0.1f);
//
//        Vector3 occludeDownTopLeft = Vector3.add(occludeTopLeft, downDirection.scaled(DEPTH));
//        Vector3 occludeDownTopRight = Vector3.add(occludeTopRight, downDirection.scaled(DEPTH));
//        Vector3 occludeDownBottomLeft = Vector3.add(occludeBottomLeft, downDirection.scaled(DEPTH));
//        Vector3 occludeDownBottomRight = Vector3.add(occludeBottomRight, downDirection.scaled(DEPTH));
//
//
//        Vector3 occludeTopLeftNormal = topLeftNormal.scaled(-1f);
//        Vector3 occludeTopRightNormal = topRightNormal.scaled(-1f);
//        Vector3 occludeBottomLeftNormal = bottomLeftNormal.scaled(-1f);
//        Vector3 occludeBottomRightNormal = bottomRightNormal.scaled(-1f);
//
//        Vector3 occludeDownTopLeftNormal = downTopLeftNormal.scaled(-1f);
//        Vector3 occludeDownTopRightNormal = downTopRightNormal.scaled(-1f);
//        Vector3 occludeDownBottomLeftNormal = downBottomLeftNormal.scaled(-1f);
//        Vector3 occludeDownBottomRightNormal = downBottomRightNormal.scaled(-1f);

        Color brown = new Color(123, 63, 0);
        Color black = new Color(0, 0, 0);


        ArrayList<Vertex> vertices = new ArrayList<>(8);
        vertices.add(Vertex.builder().setPosition(topLeft).setNormal(topLeftNormal).setColor(brown).build());
        vertices.add(Vertex.builder().setPosition(topRight).setNormal(topRightNormal).setColor(brown).build());
        vertices.add(Vertex.builder().setPosition(bottomLeft).setNormal(bottomLeftNormal).setColor(brown).build());
        vertices.add(Vertex.builder().setPosition(bottomRight).setNormal(bottomRightNormal).setColor(brown).build());
        vertices.add(Vertex.builder().setPosition(downTopLeft).setNormal(downTopLeftNormal).setColor(black).build());
        vertices.add(Vertex.builder().setPosition(downTopRight).setNormal(downTopRightNormal).setColor(black).build());
        vertices.add(Vertex.builder().setPosition(downBottomLeft).setNormal(downBottomLeftNormal).setColor(black).build());
        vertices.add(Vertex.builder().setPosition(downBottomRight).setNormal(downBottomRightNormal).setColor(black).build());

//        ArrayList<Vertex> occludeVertices = new ArrayList<>(8);
//        occludeVertices.add(Vertex.builder().setPosition(occludeTopLeft).setNormal(occludeTopLeftNormal).setColor(brown).build());
//        occludeVertices.add(Vertex.builder().setPosition(occludeTopRight).setNormal(occludeTopRightNormal).setColor(brown).build());
//        occludeVertices.add(Vertex.builder().setPosition(occludeBottomLeft).setNormal(occludeBottomLeftNormal).setColor(brown).build());
//        occludeVertices.add(Vertex.builder().setPosition(occludeBottomRight).setNormal(occludeBottomRightNormal).setColor(brown).build());
//        occludeVertices.add(Vertex.builder().setPosition(occludeDownTopLeft).setNormal(occludeDownTopLeftNormal).setColor(black).build());
//        occludeVertices.add(Vertex.builder().setPosition(occludeDownTopRight).setNormal(occludeDownTopRightNormal).setColor(black).build());
//        occludeVertices.add(Vertex.builder().setPosition(occludeDownBottomLeft).setNormal(occludeDownBottomLeftNormal).setColor(black).build());
//        occludeVertices.add(Vertex.builder().setPosition(occludeDownBottomRight).setNormal(occludeDownBottomRightNormal).setColor(black).build());

        Log.d("BattleshipDemo", "Built vertices");

        ArrayList<Integer> triangleIndices = new ArrayList<>(Arrays.asList(0, 4, 1, 1, 4, 5, 2, 3, 6, 3, 7, 6, 0, 2, 4, 4, 2, 6, 1, 5, 3, 3, 5, 7, 5, 4, 6, 5, 6, 7));

        ArrayList<Integer> occTriangleIndices = new ArrayList<>(Arrays.asList(1, 4, 0,
                5, 4, 1,
                6, 3, 2,
                6, 7, 3,
                4, 2, 0,
                6, 2, 4,
                3, 5, 1,
                7, 5, 3,
                6, 4, 5,
                7, 6, 5));

        Log.d("BattleshipDemo", "Creating material RenderableDefinition");

        CompletableFuture<Material> mat = MaterialFactory.makeOpaqueWithColor(context, new Color(android.graphics.Color.GREEN));
//         CompletableFuture<Material> mat_occlude = Material.builder().setSource(context, R.raw.occlude).build();
        CompletableFuture<ModelRenderable> occluder = ModelRenderable.builder().setSource(context, R.raw.occlude_material).build();

        CompletableFuture.allOf(mat, occluder).handle((notUsed, throwable) -> {
            if (throwable != null) {
                Log.e("BattleshipDemo", "Error loading custom renderable1.");
                return null;
            }
            Material material = null;
            Material occlude_material = null;
            ModelRenderable occlude_model = null;
            try {
                material = mat.get();
                occlude_material = occluder.get().getMaterial().makeCopy();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (material == null) {
                Log.e("BattleshipDemo", "Material null!");
                return null;
            }
            if (occlude_material == null) {
                Log.e("BattleshipDemo", "Material null!!");
                return null;
            }
            Log.e("BattleshipDemo", "Material Loaded.");
            occlude_material.setBoolean("colorWrite", false);
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
                Log.d("BattleshipDemo", "About to set renderable to node...");
                Node node = new Node();
                node.setRenderable(modelRenderable);
                modelRenderable.setRenderPriority(7);
                node.setParent(anchorNode);
                return null;
            }));
            Log.e("BattleshipDemo", "Material 1.");
            RenderableDefinition.Submesh submesh_occlude = RenderableDefinition.Submesh.builder()
                    .setMaterial(occlude_material)
                    .setName("Plane")
                    .setTriangleIndices(occTriangleIndices)
                    .build();

            Log.e("BattleshipDemo", "Material 2.");

            Log.e("BattleshipDemo", "Material 3.");

            Log.e("BattleshipDemo", "Material 4.");

//            ModelRenderable.builder().setSource(def_occlude).build().handle(((modelRenderable, throwable1) -> {
//                if (throwable1 != null) {
//                    Log.e("BattleshipDemo", "Error loading custom renderable2.");
//                    return null;
//                }
//                if (modelRenderable == null) {
//                    Log.e("BattleshipDemo", "Model Renderable null!");
//                    return null;
//                }
//                Log.d("BattleshipDemo", "About to set renderable to node...");
//                Node node = new Node();
//                node.setRenderable(modelRenderable);
//                modelRenderable.setRenderPriority(7);
//                node.setParent(anchorNode);
//                return null;
//            }));
            return null;
        });
    }
}
