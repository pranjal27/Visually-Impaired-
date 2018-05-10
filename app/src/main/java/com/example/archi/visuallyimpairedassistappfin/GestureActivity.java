package com.example.archi.visuallyimpairedassistappfin;


import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GestureActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    String tag = "Gesture revelar";
    GestureLibrary gestureLib = null;
    private static final int CAMERA_REQUEST=1888;
//    private ImageView im;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final String url="http://192.168.43.242:5000/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
     //  im=(ImageView)findViewById(R.id.imageView4);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gesture);

        if (!gestureLib.load()) {
            Toast.makeText(this, "could not load", Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.v("Adi", "Library features:");
        Log.v("adi", "Orientation style:" + gestureLib.getOrientationStyle());
        Log.v("adi", "Sequence type:" + gestureLib.getSequenceType());

        for (String gesturename : gestureLib.getGestureEntries()) {
            Log.v("adi", "for gesture" + gesturename);
            int i = 1;
            for (Gesture gesture : gestureLib.getGestures(gesturename)) {
                Log.v("adi", "    " + i + ":ID:" + gesture.getID());
                Log.v("adi", "    " + i + ":Stroke count:" + gesture.getStrokesCount());
                Log.v("adi", "    " + i + ":Stroke Length:" + gesture.getLength());
                i++;


            }
        }

        GestureOverlayView gestureView = (GestureOverlayView) findViewById(R.id.gestureOverlay);
        gestureView.addOnGesturePerformedListener(this);

    }

    @Override
    public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        if (predictions.size() > 0) {
            Prediction prediction = (Prediction) predictions.get(0);
            if (prediction.score > 2.5) {
                Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
                if (prediction.name.equals("m")) {
                    Intent intent = getApplicationContext().getPackageManager()
                            .getLaunchIntentForPackage("com.google.android.music");
                    startActivity(intent);
                }
                else if (prediction.name.equals("c")){
                    if (checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_CAMERA_PERMISSION_CODE);
                    } else {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
            }

            }


        }


    }
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
        Intent cameraIntent = new
        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
        Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
        }

        }
        }

protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
       //         im.setImageBitmap(photo);
                String imtoString=bitmapToString(photo);

                uploadImage(imtoString);
               // Toast.makeText(getApplicationContext(),imtoString,Toast.LENGTH_LONG).show();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        }
//+"?image="+ps
    public void uploadImage(final String ps){
        Log.d("taggy",ps);

        StringRequest stringRequest=new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String res=jsonObject.getString("key");
                    String res1=jsonObject.getString("key1");
                    Bitmap b1=StringToBitMap(res);
                    //im.setImageBitmap(b1);
                    Toast.makeText(getApplicationContext(),res,Toast.LENGTH_SHORT).show();
                    Log.d("taggy",res);
                    Intent i=new Intent(GestureActivity.this,FetchProcessedImage.class);
                    i.putExtra("tttt",res);
                    i.putExtra("tttt1",res1);
                    startActivity(i);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("taggy","gygfythis" + error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("image", ps);
                return params;
            }
        };


        RequestQueue rq =new Volley().newRequestQueue(getApplicationContext());
        rq.add(stringRequest);
        // MySingleton.getInstance(MainActivity.this).addToRequestQue(stringRequest);
    }



    private String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            Log.d("tag","bitm");
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            Log.d("tag","error");
            return null;
        }
    }
    public Bitmap StringtoBitMap(String encodedString){
        byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }



}

