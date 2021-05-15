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
		String token = "fb26f2624c75b78c46ef98601661d834abc0f866571902d18fb1bce50883d644";
		Agent agent = new Agent.Builder(this)
								.setToken(token)
								.setAgentId("1")
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

			agent.rating(new Agent.RatingListener() {
				@Override
				public void onSuccess(double rating) {
					Log.d("JUPITA", String.valueOf(rating));
				}

				@Override
				public void onError(String statusCode, JSONObject response) {

					Log.d("JUPITA", response.toString());
				}
			});

			agent.feed(new Agent.FeedListener() {
				@Override
				public void onSuccess(JSONObject week) {
					Log.d("JUPITA", week.toString());
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