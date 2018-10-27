package com.cyfer.jazzmax;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationChannels extends Application {

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }


    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Notifications for New Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("Disable this to turn off Notifications for Messages in the chat");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Notifications for New Files",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("Disable this to turn off Notifications for Recieved Files");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }


}
