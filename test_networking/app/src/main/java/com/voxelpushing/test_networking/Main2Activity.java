package com.voxelpushing.test_networking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Main2Activity extends AppCompatActivity {

    Button button = findViewById(R.id.button);
    EditText etext = findViewById(R.id.playerName);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final com.android.volley.Response.Listener<String> vlistener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("Sent", "Good job");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String pName = etext.getText().toString();
                String url = "http://35.185.87.150:5000/add/" + pName;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, vlistener,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

                Intent startMatch = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(startMatch);
            }
        });
    }

}
