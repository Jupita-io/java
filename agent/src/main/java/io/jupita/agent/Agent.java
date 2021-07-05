package io.jupita.agent;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class Agent {
	private String token;
	private String agentId;
	private static RequestQueue requestQueue;
	private Context context;

	public static final int AGENT = 0;
	public static final int CLIENT = 1;
	public static final String JUPITAV1 = "JupitaV1";
	public static final String JUPITAV2 = "JupitaV2";


	public Agent(@NonNull Context context, @NonNull String token, @NonNull String agentId){
		this.token = token;
		this.agentId = agentId;
		this.context = context;
		requestQueue = Volley.newRequestQueue(context);
	}

	private void dumpRequestAPI(@NonNull String text, @NonNull String clientId,
								int type, boolean isCall,
								DumpListener dumpListener) throws JSONException, IllegalArgumentException {
		if(type != AGENT && type != CLIENT){
			throw new IllegalArgumentException("Use either Agent or Client type");
		}
		final JSONObject jsonData = new JSONObject("{" +
				"\"token\":\"" + this.token + "\"," +
				"\"client_id\":\""+ clientId + "\"," +
				"\"agent_id\":\"" + this.agentId + "\"," +
				"\"message_type\":" + String.valueOf(type) + "," +
				"\"text\":\""+ text +"\"," +
				"\"isCall\":"+ String.valueOf(isCall) +"}");

		JsonObjectRequest request = new JsonObjectRequest(Constants.dumpEndpoint, jsonData,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						String msg = "";
						double utterance = 0.0;
						try {
							msg = response.getString("message");
							utterance = response.getDouble("score");
						} catch (JSONException e) {
							Log.e(Constants.name, e.getMessage());
							e.printStackTrace();
						}
						if(dumpListener != null){
							dumpListener.onSuccess(msg, utterance);
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						String body = "";
						JSONObject jsonResponse = null;
						String statusCode = String.valueOf(error.networkResponse.statusCode);
						if(error.networkResponse.data!=null) {
							try {
								body = new String(error.networkResponse.data,"UTF-8");
								jsonResponse = new JSONObject(body);
							} catch (UnsupportedEncodingException | JSONException e) {
								Log.e(Constants.name, e.getMessage());
								e.printStackTrace();
							}
						}
						if(dumpListener != null){
							dumpListener.onError(statusCode, jsonResponse);
						}
					}
				}
		);
		requestQueue.add(request);
	}

	private void ratingRequestAPI(@NonNull String modelName, RatingListener ratingListener)
			throws JSONException, IllegalArgumentException {
		if(!modelName.equals(JUPITAV1)){
			throw new IllegalArgumentException("Only Jupita v1 is supported");
		}

		final JSONObject jsonData = new JSONObject("{" +
				"\"token\":\"" + this.token + "\"," +
				"\"agent_id\":\"" + this.agentId + "\"," +
				"\"model\":\"" + modelName + "\"}");

		JsonObjectRequest request = new JsonObjectRequest(Constants.ratingEndpoint, jsonData,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						double rating = 0.0;
						try {
							rating = response.getDouble("rating");
						} catch (JSONException e) {
							Log.e(Constants.name, e.getMessage());
							e.printStackTrace();
						}
						if(ratingListener != null){
							ratingListener.onSuccess(rating);
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						String body = "";
						JSONObject jsonResponse = null;
						String statusCode = String.valueOf(error.networkResponse.statusCode);
						if(error.networkResponse.data!=null) {
							try {
								body = new String(error.networkResponse.data,"UTF-8");
								jsonResponse = new JSONObject(body);
							} catch (UnsupportedEncodingException | JSONException e) {
								Log.e(Constants.name, e.getMessage());
								e.printStackTrace();
							}
						}
						if(ratingListener != null){
							ratingListener.onError(statusCode, jsonResponse);
						}
					}
				}
		);
		requestQueue.add(request);
	}

	private void feedRequestAPI(FeedListener feedListener)
			throws JSONException, IllegalArgumentException {

		final JSONObject jsonData = new JSONObject("{" +
				"\"token\":\"" + this.token + "\"," +
				"\"agent_id\":\"" + this.agentId + "\"}");

		JsonObjectRequest request = new JsonObjectRequest(Constants.feedEndpoint, jsonData,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if(feedListener != null){
							feedListener.onSuccess(response);
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						String body = "";
						JSONObject jsonResponse = null;
						String statusCode = String.valueOf(error.networkResponse.statusCode);
						if(error.networkResponse.data!=null) {
							try {
								body = new String(error.networkResponse.data,"UTF-8");
								jsonResponse = new JSONObject(body);
							} catch (UnsupportedEncodingException | JSONException e) {
								Log.e(Constants.name, e.getMessage());
								e.printStackTrace();
							}
						}
						if(feedListener != null){
							feedListener.onError(statusCode, jsonResponse);
						}
					}
				}
		);
		requestQueue.add(request);
	}

	public void dump(@NonNull String text, @NonNull String clientId)
			throws JSONException, IllegalArgumentException {
		dumpRequestAPI(text, clientId, AGENT, false, null);
	}

	public void dump(@NonNull String text, @NonNull String clientId, DumpListener dumpListener)
			throws JSONException, IllegalArgumentException {
		dumpRequestAPI(text, clientId, AGENT, false, dumpListener);
	}

	public void dump(@NonNull String text, @NonNull String clientId,
					 int type, DumpListener dumpListener)
			throws JSONException, IllegalArgumentException {
		dumpRequestAPI(text, clientId, type, false, dumpListener);
	}

	public void dump(@NonNull String text, @NonNull String clientId, int type,
					 boolean isCall, DumpListener dumpListener)
			throws JSONException, IllegalArgumentException {
		dumpRequestAPI(text, clientId, type, isCall, dumpListener);
	}

	public void rating(RatingListener ratingListener)
			throws JSONException, IllegalArgumentException {
		ratingRequestAPI(JUPITAV1, ratingListener);
	}

	public void rating(@NonNull String modelName, RatingListener ratingListener)
			throws JSONException, IllegalArgumentException {
		ratingRequestAPI(modelName, ratingListener);
	}

	public void feed(FeedListener feedListener)
			throws JSONException, IllegalArgumentException {
		feedRequestAPI(feedListener);
	}

	public interface DumpListener {
		public void onSuccess(String msg, double utterance);
		public void onError(String statusCode, JSONObject response);
	}

	public interface RatingListener {
		public void onSuccess(double rating);
		public void onError(String statusCode, JSONObject response);
	}

	public interface FeedListener {
		public void onSuccess(JSONObject week);
		public void onError(String statusCode, JSONObject response);
	}

	// Create a builder class to build Agent
	public static class Builder {
		private String token;
		private String agentId;
		private Context context;

		public Builder(@NonNull Context context){
			this.context = context;
		}

		public Builder setToken(@NonNull String token) {
			this.token = token;
			return this;
		}

		public Builder setAgentId(@NonNull String agentId) {
			this.agentId = agentId;
			return this;
		}

		public Agent build(){
			return new Agent(this.context, this.token, this.agentId);
		}
	}
}