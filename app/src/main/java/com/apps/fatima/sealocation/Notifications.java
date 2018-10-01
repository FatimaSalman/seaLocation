package com.apps.fatima.sealocation;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.apps.fatima.sealocation.activities.MainActivity;
import com.apps.fatima.sealocation.activities.RatingActivity;
import com.apps.fatima.sealocation.activities.RatingCourseActivity;
import com.apps.fatima.sealocation.activities.RatingTankActivity;
import com.apps.fatima.sealocation.activities.RatingTripActivity;


@RequiresApi(api = Build.VERSION_CODES.N)
public class Notifications {

    public static void showNotification(Context context, String title, String messageText,
                                        String partner_type, String user_id, String guid, String id,
                                        String course_id, String trip_id, String tank_id, String trip_name) {
        Log.e("messageText", messageText);
        Log.e("partner_type", partner_type);
        Log.e("title", title);
        Log.e("user_id", user_id);
        Log.e("guid", guid);
        Log.e("course_id", course_id);
        Log.e("trip_id", trip_id);
        Log.e("tank_id", tank_id);
        Log.e("trip_name", trip_name);
        String CHANNEL_ID = "my_channel_01";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        String channelName = "Channel Name";
        NotificationChannel mChannel = null;
        int i = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
        }
        if (partner_type != null) {
            if (partner_type.equals("boat")) {
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("boat", partner_type);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent contentIntent = PendingIntent.getActivity(
                        context, (int) System.currentTimeMillis(), intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        context).setSmallIcon(R.drawable.logo_white_bg)
                        .setContentTitle(title != null ? title : context.getString(R.string.app_name)).setSound(soundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText))
                        .setContentText(messageText).setChannelId(CHANNEL_ID)
                        .setGroup("com.apps.fatima.sealocation").setGroupSummary(true);

                builder.setContentIntent(contentIntent);
                NotificationManager nManager = (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);
                assert nManager != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nManager.createNotificationChannel(mChannel);
                }
                builder.setAutoCancel(true);
                nManager.notify(++i, builder.build());
            } else if (partner_type.equals("jetski")) {
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Intent targetIntent = new Intent(context, MainActivity.class);
                targetIntent.putExtra("jetski", partner_type);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        context).setSmallIcon(R.drawable.logo_white_bg)
                        .setContentTitle(title != null ? title : context.getString(R.string.app_name)).setSound(soundUri)
                        .setContentText(messageText).setChannelId(CHANNEL_ID)
                        .setGroup("com.apps.fatima.sealocation").setGroupSummary(true);
                PendingIntent contentIntent = PendingIntent.getActivity(
                        context, 0, targetIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);
                NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                builder.setAutoCancel(true);
                assert nManager != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nManager.createNotificationChannel(mChannel);
                }
                nManager.notify(++i, builder.build());
                NotificationPrefManager notify = new NotificationPrefManager(context);
                notify.setNotifyCount(notify.getNotifyCount() + 1);
            } else if (partner_type.equals("diver")) {
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Intent targetIntent = new Intent(context, MainActivity.class);
                targetIntent.putExtra("diver", partner_type);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        context).setSmallIcon(R.drawable.logo_white_bg)
                        .setContentTitle(title != null ? title : context.getString(R.string.app_name)).setSound(soundUri)
                        .setContentText(messageText).setChannelId(CHANNEL_ID)
                        .setGroup("com.apps.fatima.sealocation").setGroupSummary(true);
                PendingIntent contentIntent = PendingIntent.getActivity(
                        context, 0, targetIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);
                NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                builder.setAutoCancel(true);
                assert nManager != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nManager.createNotificationChannel(mChannel);
                }
                nManager.notify(++i, builder.build());
            }
//            else if (partner_type.equals("0")) {
//                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                Intent targetIntent = new Intent(context, MainActivity.class);
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(
//                        context).setSmallIcon(R.drawable.logo_white_bg)
//                        .setContentTitle(title != null ? title : context.getString(R.string.app_name)).setSound(soundUri)
//                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText))
//                        .setContentText(messageText);
//                PendingIntent contentIntent = PendingIntent.getActivity(
//                        context, 0, targetIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(contentIntent);
//                NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                builder.setAutoCancel(true);
//                assert nManager != null;
//                nManager.notify(0, builder.build());
//            }
            else if (partner_type.equals("1")) {
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (course_id != null && !course_id.isEmpty()) {
                    Intent targetIntent = new Intent(context, RatingCourseActivity.class);
                    targetIntent.putExtra("user_id", user_id);
                    targetIntent.putExtra("guid", guid);
                    targetIntent.putExtra("id", id);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(
                            context).setSmallIcon(R.drawable.logo_white_bg)
                            .setContentTitle(title != null ? title : context.getString(R.string.app_name)).setSound(soundUri)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText))
                            .setContentText(messageText).setChannelId(CHANNEL_ID)
                            .setGroup("com.apps.fatima.sealocation").setGroupSummary(true);
                    PendingIntent contentIntent = PendingIntent.getActivity(
                            context, 0, targetIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);
                    NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    builder.setAutoCancel(true);
                    assert nManager != null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        nManager.createNotificationChannel(mChannel);
                    }
                    nManager.notify(++i, builder.build());
                } else if (trip_id != null && !trip_id.isEmpty()) {
                    if (!TextUtils.equals(trip_id, "0")) {
                        Log.e("trip_id///", trip_id);
                        Intent targetIntent = new Intent(context, RatingTripActivity.class);
                        targetIntent.putExtra("user_id", user_id);
                        targetIntent.putExtra("guid", guid);
                        targetIntent.putExtra("id", id);
                        targetIntent.putExtra("trip_name", trip_name);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                context).setSmallIcon(R.drawable.logo_white_bg)
                                .setContentTitle(title != null ? title : context.getString(R.string.app_name)).setSound(soundUri)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText))
                                .setContentText(messageText).setChannelId(CHANNEL_ID)
                                .setGroup("com.apps.fatima.sealocation").setGroupSummary(true);
                        PendingIntent contentIntent = PendingIntent.getActivity(
                                context, 0, targetIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(contentIntent);
                        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        builder.setAutoCancel(true);
                        assert nManager != null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            nManager.createNotificationChannel(mChannel);
                        }
                        nManager.notify(++i, builder.build());
                    } else {
                        Intent targetIntent = new Intent(context, RatingActivity.class);
                        targetIntent.putExtra("user_id", user_id);
                        targetIntent.putExtra("guid", guid);
                        targetIntent.putExtra("id", id);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                context).setSmallIcon(R.drawable.logo_white_bg)
                                .setContentTitle(title != null ? title : context.getString(R.string.app_name)).setSound(soundUri)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText))
                                .setContentText(messageText).setChannelId(CHANNEL_ID)
                                .setGroup("com.apps.fatima.sealocation").setGroupSummary(true);
                        PendingIntent contentIntent = PendingIntent.getActivity(
                                context, 0, targetIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(contentIntent);
                        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        builder.setAutoCancel(true);
                        assert nManager != null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            nManager.createNotificationChannel(mChannel);
                        }
                        nManager.notify(++i, builder.build());
                    }
                } else if (tank_id != null && !tank_id.isEmpty()) {
                    Intent targetIntent = new Intent(context, RatingTankActivity.class);
                    targetIntent.putExtra("user_id", user_id);
                    targetIntent.putExtra("guid", guid);
                    targetIntent.putExtra("id", id);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(
                            context).setSmallIcon(R.drawable.logo_white_bg)
                            .setContentTitle(title != null ? title : context.getString(R.string.app_name)).setSound(soundUri)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText))
                            .setContentText(messageText).setChannelId(CHANNEL_ID)
                            .setGroup("com.apps.fatima.sealocation").setGroupSummary(true);
                    PendingIntent contentIntent = PendingIntent.getActivity(
                            context, 0, targetIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);
                    NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    builder.setAutoCancel(true);
                    assert nManager != null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        nManager.createNotificationChannel(mChannel);
                    }
                    nManager.notify(++i, builder.build());
                } else {
                    Intent targetIntent = new Intent(context, RatingActivity.class);
                    targetIntent.putExtra("user_id", user_id);
                    targetIntent.putExtra("guid", guid);
                    targetIntent.putExtra("id", id);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(
                            context).setSmallIcon(R.drawable.logo_white_bg)
                            .setContentTitle(title != null ? title : context.getString(R.string.app_name)).setSound(soundUri)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText))
                            .setContentText(messageText).setChannelId(CHANNEL_ID)
                            .setGroup("com.apps.fatima.sealocation").setGroupSummary(true);
                    PendingIntent contentIntent = PendingIntent.getActivity(
                            context, 0, targetIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);
                    NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    builder.setAutoCancel(true);
                    assert nManager != null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        nManager.createNotificationChannel(mChannel);
                    }
                    nManager.notify(++i, builder.build());
                }
            } else if (partner_type.equals("2")) {
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Intent targetIntent = new Intent(context, MainActivity.class);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        context).setSmallIcon(R.drawable.logo_white_bg)
                        .setContentTitle(title != null ? title : context.getString(R.string.app_name)).setSound(soundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText))
                        .setContentText(messageText)
                        .setChannelId(CHANNEL_ID)
                        .setGroup("com.apps.fatima.sealocation").setGroupSummary(true);
                PendingIntent contentIntent = PendingIntent.getActivity(
                        context, 0, targetIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);
                NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                builder.setAutoCancel(true);
                assert nManager != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nManager.createNotificationChannel(mChannel);
                }
                nManager.notify(++i, builder.build());
            } else if (partner_type.equals("0") || partner_type.equals("3")) {
                Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Intent targetIntent = new Intent(context, MainActivity.class);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(
                        context).setSmallIcon(R.drawable.logo_white_bg)
                        .setContentTitle(title != null ? title : context.getString(R.string.app_name)).setSound(soundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText))
                        .setContentText(messageText)
                        .setChannelId(CHANNEL_ID).setGroup("com.apps.fatima.sealocation").setGroupSummary(true);
                PendingIntent contentIntent = PendingIntent.getActivity(
                        context, 0, targetIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(contentIntent);
                NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                builder.setAutoCancel(true);
                assert nManager != null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nManager.createNotificationChannel(mChannel);
                }
                nManager.notify(++i, builder.build());
            }
        }
    }
}
