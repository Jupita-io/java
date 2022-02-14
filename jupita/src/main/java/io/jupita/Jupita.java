package io.jupita;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Jupita {

    private String token;
    private String touchpoint_id;
    private static RequestQueue requestQueue;
    private Context context;

    public static final int TOUCHPOINT = 0;
    public static final int INPUT = 1;
    public static final String JUPITAV1 = "JupitaV1";
    public static final String JUPITAV2 = "JupitaV2";


    public Jupita(@NonNull Context context, @NonNull String token, @NonNull String touchpoint_id) {
        this.token = token;
        this.touchpoint_id = touchpoint_id;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    private void dumpRequestAPI(@NonNull String text, @NonNull String input_id, @NonNull String channel_type,
                                int message_type, boolean isCall,
                                DumpListener dumpListener) throws JSONException, IllegalArgumentException {
        if (message_type != TOUCHPOINT && message_type != INPUT) {
            throw new IllegalArgumentException("Use either `Jupita.TOUCHPOINT` or `Jupita.INPUT` type");
        }
        final JSONObject jsonData = new JSONObject("{" +
                "\"token\":\"" + this.token + "\"," +
                "\"input_id\":\"" + input_id + "\"," +
                "\"channel_type\":\"" + channel_type + "\"," +
                "\"touchpoint_id\":\"" + this.touchpoint_id + "\"," +
                "\"message_type\":" + message_type + "," +
                "\"text\":\"" + text + "\"," +
                "\"isCall\":" + isCall + "}");

        JsonObjectRequest request = new JsonObjectRequest(Constants.dumpEndpoint, jsonData,
                response -> {
                    String msg = "";
                    double utterance = 0.0;
                    try {
                        msg = response.getString("message");
                        utterance = response.getDouble("score");
                    } catch (JSONException e) {
                        Log.e(Constants.name, e.getMessage());
                        e.printStackTrace();
                    }
                    if (dumpListener != null) {
                        dumpListener.onSuccess(msg, utterance);
                    }
                },
                error -> {
                    String body = "";
                    JSONObject jsonResponse = new JSONObject();
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    if (error.networkResponse.data != null) {
                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
                            jsonResponse = new JSONObject(body);
                        } catch (UnsupportedEncodingException | JSONException e) {
                            Log.e(Constants.name, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    if (dumpListener != null) {
                        dumpListener.onError(statusCode, jsonResponse);
                    }
                }
        );
        requestQueue.add(request);
    }

    public void dump(@NonNull String text, @NonNull String input_id, @NonNull String channel_type)
            throws JSONException, IllegalArgumentException {
        dumpRequestAPI(text, input_id, channel_type, TOUCHPOINT, false, null);
    }

    public void dump(@NonNull String text, @NonNull String input_id, @NonNull String channel_type, DumpListener dumpListener)
            throws JSONException, IllegalArgumentException {
        dumpRequestAPI(text, input_id, channel_type, TOUCHPOINT, false, dumpListener);
    }

    public void dump(@NonNull String text, @NonNull String input_id, @NonNull String channel_type, 
                     int message_type, DumpListener dumpListener)
            throws JSONException, IllegalArgumentException {
        dumpRequestAPI(text, input_id, channel_type, message_type, false, dumpListener);
    }

    public void dump(@NonNull String text, @NonNull String input_id, @NonNull String channel_type, int message_type,
                     boolean isCall, DumpListener dumpListener)
            throws JSONException, IllegalArgumentException {
        dumpRequestAPI(text, input_id, channel_type, message_type, isCall, dumpListener);
    }

    public interface DumpListener {
        void onSuccess(String msg, double utterance);

        void onError(String statusCode, JSONObject response);
    }

    // Create a builder class to build Jupita
    public static class Builder {

        private String token;
        private String touchpoint_id;
        private Context context;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder setToken(@NonNull String token) {
            this.token = token;
            return this;
        }

        public Builder setTouchpoint_id(@NonNull String touchpoint_id) {
            this.touchpoint_id = touchpoint_id;
            return this;
        }

        public Jupita build() {
            return new Jupita(this.context, this.token, this.touchpoint_id);
        }
    }

}
