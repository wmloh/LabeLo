package com.voxelpushing.test_networking;


import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // The image that you drag
    ImageView move;
    // Just one drag target for now
    TextView top;
    // static ImageSet imageSet;
    TextView right;
    TextView left;
    TextView bottom;

    String[] imgList = {"img1.jpg", "img2.jpg",
            "img3.jpg",
            "img4.jpg",
            "img5.jpg",
            "img6.jpg",
            "img7.jpg",
            "img8.jpg",
            "img9.jpg",
            "img10.jpg",
            "img11.jpg",
            "img12.jpg",
            "img13.jpg",
            "img14.jpg",
            "img15.jpg",
            "img16.jpg",
            "img17.jpg",
            "img18.jpg",
            "img19.jpg",
            "img20.jpg"};
    String[] lblList = {"cat",
            "dog",
            "rabbit",
            "horse"};
    int i = 0;
    String currentImg = imgList[i];
    String label;
    String iurl = "http://35.185.87.150:5000/getimg/" + currentImg;

    // Lists that hold the images and labels

    final String jsonUrl = "http://35.185.87.150:5000/getlabels";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // targets
        top = findViewById(R.id.top);
        right = findViewById(R.id.right);
        left = findViewById(R.id.left);
        bottom = findViewById(R.id.bottom);

        final Response.Listener<String> vlistener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("Sent", "Good job");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        View.OnDragListener listener = new View.OnDragListener() {
            @Override
            public boolean onDrag(final View v, DragEvent dragEvent) {

                final int action = dragEvent.getAction();
                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        //v.animate().scaleYBy(-1f).scaleXBy(-1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        //v.animate().scaleYBy(1f).scaleXBy(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DROP:
                        // v.animate().scaleYBy(-1f).scaleXBy(-1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
                        Toast.makeText(getApplicationContext(), "sent", Toast.LENGTH_SHORT).show();
                        switch (v.getId()) {
                            case R.id.top:
                                label = "cat";
                                break;
                            case R.id.right:
                                label = "horse";
                                break;
                            case R.id.bottom:
                                label = "rabbit";
                                break;
                            case R.id.left:
                                label = "dog";
                                break;
                            default:
                                label = "shit";
                                break;
                        }
                        String url = "http://35.185.87.150:5000/sendlabel/" + currentImg + "=" + label + "=p1";
                        Log.d("POST URL", url);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, vlistener,
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                        if (i < 20){
                            i++;
                        } else i = 19;
                        currentImg = imgList[i];
                        iurl = "http://35.185.87.150:5000/getimg/" + currentImg;
                        Picasso.get().load(iurl).into(move);
                        Log.d("New URL", iurl);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        // v.animate().scaleYBy(-1f).scaleXBy(-1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
                        // Toast.makeText(getApplicationContext(), "ended", Toast.LENGTH_SHORT).show();
                        View dragView = (View) dragEvent.getLocalState();
                        dragView.setVisibility(View.VISIBLE);
                        return true;
                }
                return true;
            }
        };
        // setting the OnDragListener for the target
        top.setOnDragListener(listener);
        right.setOnDragListener(listener);
        left.setOnDragListener(listener);
        bottom.setOnDragListener(listener);

        // finding the ImageView for the draggable image
        move = findViewById(R.id.weimin);
        move.setImageResource(R.drawable.wml);

        Picasso.get().load(iurl).into(move);

        // Setting the OnTouchListener for the ImageView
        move.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setVisibility(View.INVISIBLE);
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadow = new View.DragShadowBuilder(move);
                    view.startDrag(data, shadow, view, 0);
                    return true;
                }
                return false;
            }
        });
    }

//
//    // method to parse JSON and put into a the List<String> above
//    private void loadImageSet() {
//        // Initializing the lists from above
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonUrl, listener,
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }
}



//    public boolean onTouch(View view, MotionEvent event) {
//
//        switch (event.getAction()) {
//
//            case MotionEvent.ACTION_DOWN:
//                dX = view.getX() - event.getRawX();
//                dY = view.getY() - event.getRawY();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                view.animate()
//                        .x(event.getRawX() + dX)
//                        .y(event.getRawY() + dY)
//                        .setDuration(0)
//                        .start();
//            default:
//                return false;
//        }
//        return true;
//    }


//        final TextView shit = findViewById(R.id.first);
//
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = "http://www.google.com";
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response){
//                shit.setText("Response is " + response.substring(0,500));
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error){
//                shit.setText("That did not work!");
//            }
//        });
//
//        queue.add(stringRequest);