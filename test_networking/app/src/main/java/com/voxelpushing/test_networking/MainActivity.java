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
    ImageView top;
    ImageSet imageSet;

    // Lists that hold the images and labels

    final String jsonUrl = "http://35.185.87.150:5000/getlabels";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // target
        top = findViewById(R.id.top_semicircle);

        // setting the OnDragListener for the target
        top.setOnDragListener(new View.OnDragListener() {

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
//                        ValueAnimator anim1 = ValueAnimator.ofInt(v.getMeasuredHeight(), 150);
//                        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                                int val = (Integer) valueAnimator.getAnimatedValue();
//                                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
//                                layoutParams.height = val;
//                                v.setLayoutParams(layoutParams);
//                            }
//                        });
//                        anim1.setDuration(1000);
//                        anim1.start();
                        //v.animate().scaleYBy(1f).scaleXBy(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DROP:
//                        ValueAnimator anim2 = ValueAnimator.ofInt(v.getMeasuredHeight(), 150);
//                        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                                int val = (Integer) valueAnimator.getAnimatedValue();
//                                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
//                                layoutParams.height = val;
//                                v.setLayoutParams(layoutParams);
//                            }
//                        });
//                        anim2.setDuration(1000);
//                        anim2.start();
                        // v.animate().scaleYBy(-1f).scaleXBy(-1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
                        Toast.makeText(getApplicationContext(), "drop", Toast.LENGTH_SHORT).show();
                        v.invalidate();
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        // v.animate().scaleYBy(-1f).scaleXBy(-1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
                        Toast.makeText(getApplicationContext(), "ended", Toast.LENGTH_SHORT).show();
                        View dragView = (View) dragEvent.getLocalState();
                        dragView.setVisibility(View.VISIBLE);
                        return true;
                }
                return true;
            }
        });

        loadImageSet();

        // finding the ImageView for the draggable image
        move = findViewById(R.id.weimin);
        move.setImageResource(R.drawable.wml);
        // Log.d("Log URL", imageSet.getImages().get(0));
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
        // Initializing the lists from above
        StringRequest stringRequest = new StringRequest(Request.Method.GET, jsonUrl, new Response.Listener<String>() {

            List<String> imgList = new ArrayList<>();
            List<String> lblList = new ArrayList<>();

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray imgArray = obj.getJSONArray("img");
                    JSONArray lblArray = obj.getJSONArray("labels");

                    for (int i = 0; i < imgArray.length(); i++) {
                        String imgStr = imgArray.getString(i);
                        // Log.d("Image URL", imgStr);
                        imgList.add(imgStr);
                    }

                    for (int i = 0; i < lblArray.length(); i++) {
                        String lblStr = lblArray.getString(i);
                        // Log.d("Label", lblStr);
                        lblList.add(lblStr);
                    }

                    imageSet = new ImageSet(imgList, lblList);
                    for (int i = 0; i < imageSet.getImages().size(); i++){
                        Log.d("Image URL", imageSet.getImages().get(i));
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