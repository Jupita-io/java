package io.jupita.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.jupita.agent.Agent;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String token = "Access-token";
		Agent agent = new Agent.Builder(this)
								.setToken(token)
								.setAgent_id("2")
								.build();
		try {
			Log.d("JUPITA", "Starting different API calls");
			agent.dump("Hi", "2", Agent.AGENT, new Agent.DumpListener() {
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
