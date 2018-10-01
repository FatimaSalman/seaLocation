package com.apps.fatima.sealocation;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From:/// " + remoteMessage);
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        Map<String, String> data = remoteMessage.getData();
        Log.e("mapData", data + "");
        JSONObject jsonObject = new JSONObject(data);
        try {
            String a_data = jsonObject.getString("a_data");
            if (a_data != null) {
                JSONObject dataObject = new JSONObject(a_data);
                String messageText = dataObject.getString("message");
                String title = dataObject.getString("title");
                if (dataObject.has("partner_type")) {
                    String partner_type = dataObject.getString("partner_type");
                    Log.e("message0", messageText + " // " + partner_type);
                    Notifications.showNotification(getApplicationContext(), title, messageText,
                            partner_type, "", "", "", "", "", "", "");
                } else if (dataObject.has("approved")) {
                    String course_id = "", trip_id = "", tank_id = "", trip_name = "";
                    if (dataObject.has("course_id")) {
                        course_id = dataObject.getString("course_id");
                    }
                    if (dataObject.has("trip_id")) {
                        trip_id = dataObject.getString("trip_id");
                    }
                    if (dataObject.has("tank_id")) {
                        tank_id = dataObject.getString("tank_id");
                    }
                    if (dataObject.has("trip_name")) {
                        trip_name = dataObject.getString("trip_name");
                    }
                    String partner_type = dataObject.getString("approved");
                    Log.e("message0", messageText + " // " + partner_type);
                    if (partner_type.equals("1")) {
                        String user_id = dataObject.getString("partner_id");
                        String guid = dataObject.getString("guid");
                        String id = dataObject.getString("id");
                        Notifications.showNotification(getApplicationContext(), title, messageText,
                                partner_type, user_id, guid, id, course_id, trip_id, tank_id, trip_name);
                    } else
                        Notifications.showNotification(getApplicationContext(), title, messageText,
                                partner_type, "", "", "", course_id, trip_id, tank_id, trip_name);
                }

            }
//            else {
//                Notifications.showNotification(getApplicationContext(), title, messageText,
//                        "", "", "", "", "", "", "");
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}