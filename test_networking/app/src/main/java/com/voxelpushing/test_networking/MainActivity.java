package com.voxelpushing.test_networking;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // The image that you drag
    ImageView move;
    // Just one drag target for now
    TextView top;
    // ImageSet imageSet;

    // Lists that hold the images and labels
    List<String> imgList = new ArrayList<>();
    List<String> lblList = new ArrayList<>();

    final String jsonUrl = "http://35.185.87.150:5000/getlabels";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // target
        top = findViewById(R.id.top);

        // setting the OnDragListener for the target
        top.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent dragEvent) {
                final int action = dragEvent.getAction();

                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        v.setBackgroundColor(Color.RED);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundColor(Color.BLUE);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DROP:
                        v.setBackgroundColor(Color.TRANSPARENT);
                        Toast.makeText(getApplicationContext(), "drop", Toast.LENGTH_SHORT).show();
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackgroundColor(Color.TRANSPARENT);
                        Toast.makeText(getApplicationContext(), "ended", Toast.LENGTH_SHORT).show();
                        View dragView = (View) dragEvent.getLocalState();
                        dragView.setVisibility(View.VISIBLE);
                        return true;
                }
                return true;
            }
        });

        // Initializing the lists form above
        imgList = new ArrayList<>();
        lblList = new ArrayList<>();
        loadImageSet();

        // finding the ImageView for the draggable image
        move = findViewById(R.id.weimin);
        // String url = imgList.get(0);
        Log.d("URL", imgList.get(0));
        // Picasso.get().load(url).into(move);

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

    // method to parse JSON and put into a the List<String> above
    private void loadImageSet() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray imgArray = obj.getJSONArray("img");
                    JSONArray lblArray = obj.getJSONArray("labels");

                    for (int i = 0; i < imgArray.length(); i++) {
                        String imgStr = imgArray.getString(i);
                        Log.d("Image URL", imgStr);
                        imgList.add(imgStr);
                    }

                    for (int i = 0; i < lblArray.length(); i++) {
                        String lblStr = lblArray.getString(i);
                        Log.d("Label", lblStr);
                        lblList.add(lblStr);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
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