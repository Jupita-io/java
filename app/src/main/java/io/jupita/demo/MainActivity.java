package io.jupita.demo;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.jupita.Jupita;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = "Access-token";
        Jupita touchpoint = new Jupita.Builder(this)
                .setToken(token)
                .setTouchpoint_id("3")
                .build();
        try {
            Log.d("JUPITA", "Demo API call");
            touchpoint.dump("Hi, how are you?", "3", "Web chat", Jupita.TOUCHPOINT, new Jupita.DumpListener() {
                @Override
                public void onSuccess(String msg, double utterance) {
                    Log.d("JUPITA", msg);
                    Log.d("JUPITA", String.valueOf(utterance));
                }

                @Override
                public void onError(String statusCode, JSONObject response) {
                    Log.d("JUPITA", response.toString());
                }
            });

        } catch (JSONException e) {
            Log.d("JUPITA", "Error Occurred");
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
    }

}
