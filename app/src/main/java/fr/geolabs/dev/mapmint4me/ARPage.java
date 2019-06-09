package fr.geolabs.dev.mapmint4me;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ARPage extends FragmentActivity {



        private CustomArFragment arFragment;
        private enum AppAnchorState{
            NONE,
            HOSTING,
            HOSTED
        }
        private Anchor anchor;
        private  AppAnchorState appAnchorState=AppAnchorState.NONE;
        private boolean isPlaced =false;

        private SharedPreferences prefs;
        private  SharedPreferences.Editor editor;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.arpage);
            prefs = getSharedPreferences("AnchodID",MODE_PRIVATE);
            editor=prefs.edit();

            Button resolve=findViewById(R.id.resolve);

            arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

            arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

                if(!isPlaced) {
                    Anchor anchor = arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                    appAnchorState = AppAnchorState.HOSTING;

                    Toast.makeText(this,"Hosting...",Toast.LENGTH_SHORT).show();
                    createModel(anchor);

                    isPlaced=true;
                }

            });

            arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime ->{

                if(appAnchorState!=AppAnchorState.HOSTING)
                    return;
                Anchor.CloudAnchorState cloudAnchorState = anchor.getCloudAnchorState();

                if(cloudAnchorState.isError()){
                    Toast.makeText(this,cloudAnchorState.toString(),Toast.LENGTH_SHORT).show();
                }
                else if(cloudAnchorState==Anchor.CloudAnchorState.SUCCESS)
                {
                    appAnchorState = AppAnchorState.HOSTED;


                    String anchorId=anchor.getCloudAnchorId();

                    editor.putString("anchorId",anchorId);
                    editor.apply();

                    Toast.makeText(this,"Anchor Hosted id = "+anchorId,Toast.LENGTH_SHORT).show();


                }

            } );



            resolve.setOnClickListener(view -> {
                String anchorId= prefs.getString("anchorId","null");

                if(anchorId.equals("null")){
                    Toast.makeText(this,"No Anchor id ",Toast.LENGTH_SHORT).show();
                    return;
                }

                Anchor resolvedAnchor = arFragment.getArSceneView().getSession().resolveCloudAnchor(anchorId);
                createModel(resolvedAnchor);
            });
        }

    private void createModel(Anchor anchor) {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("ArcticFox_Posed.sfb"))
                .build()
                .thenAccept(modelRenderable -> addModelToScene(anchor,modelRenderable));
    }


    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {

        AnchorNode anchorNode=new AnchorNode(anchor);
        anchorNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);

    }


    }
