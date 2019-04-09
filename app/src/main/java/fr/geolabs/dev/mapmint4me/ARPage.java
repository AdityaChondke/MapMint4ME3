package fr.geolabs.dev.mapmint4me;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ARPage extends FragmentActivity {

        private ArFragment arFragment;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.arpage);
            arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
            arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                Anchor anchor = hitResult.createAnchor();

                ModelRenderable.builder()
                        .setSource(this, Uri.parse("ArcticFox_Posed.sfb")).build()
                        .thenAccept(modelRenderable -> addModelToScene(anchor,modelRenderable))
                        .exceptionally(throwable ->{
                            AlertDialog.Builder builder= new AlertDialog.Builder(this);
                            builder.setMessage(throwable.getMessage()).show();
                            return null;

                        });



            });
        }



    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {

        AnchorNode anchorNode=new AnchorNode(anchor);
        TransformableNode transformableNode= new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }


    }
