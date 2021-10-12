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

    private void dumpRequestAPI(@NonNull String text, @NonNull String input_id,
                                int type, boolean isCall,
                                DumpListener dumpListener) throws JSONException, IllegalArgumentException {
        if (type != TOUCHPOINT && type != INPUT) {
            throw new IllegalArgumentException("Use either Touchpoint or Input type");
        }
        final JSONObject jsonData = new JSONObject("{" +
                "\"token\":\"" + this.token + "\"," +
                "\"input_id\":\"" + input_id + "\"," +
                "\"touchpoint_id\":\"" + this.touchpoint_id + "\"," +
                "\"message_type\":" + type + "," +
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
                    JSONObject jsonResponse = null;
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

    public void dump(@NonNull String text, @NonNull String input_id)
            throws JSONException, IllegalArgumentException {
        dumpRequestAPI(text, input_id, TOUCHPOINT, false, null);
    }

    public void dump(@NonNull String text, @NonNull String input_id, DumpListener dumpListener)
            throws JSONException, IllegalArgumentException {
        dumpRequestAPI(text, input_id, TOUCHPOINT, false, dumpListener);
    }

    public void dump(@NonNull String text, @NonNull String input_id,
                     int type, DumpListener dumpListener)
            throws JSONException, IllegalArgumentException {
        dumpRequestAPI(text, input_id, type, false, dumpListener);
    }

    public void dump(@NonNull String text, @NonNull String input_id, int type,
                     boolean isCall, DumpListener dumpListener)
            throws JSONException, IllegalArgumentException {
        dumpRequestAPI(text, input_id, type, isCall, dumpListener);
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