package com.prashant.stockmarketadviser.firebase;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationSender {

    private static final String FCM_SERVER_KEY = "AAAA-J_8VnM:APA91bEkM8iGX2V7Ren82a7IO9-X_8hjQCS2MN6vYznEL5EjF_LNcF8pMT0QAwwmjABvathH9kijFYraIYPmqqT0PIAmFV15hgmAUhL9u5orzF5JUluRurizGQffNO50DfS-AjVa29qh"; // Replace with your FCM server key
    private static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";

    public static void sendNotificationToTopic(Context context, String topic, String title, String message) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject notificationBody = new JSONObject();
        JSONObject notificationData = new JSONObject();

        try {
            notificationBody.put("title", title);
            notificationBody.put("body", message);
            notificationData.put("notification", notificationBody);
            notificationData.put("to", "/topics/" + topic);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, FCM_API_URL, notificationData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Failed to send notification", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "key=" + FCM_SERVER_KEY);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
