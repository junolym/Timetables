package com.example.liwensheng.timetables;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

/**
 * Created by 陈鑫 on 2016/12/18.
 */

public class countdownReceive extends BroadcastReceiver {
    String RECEIVER = "countdownReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(RECEIVER)) {
            Bundle bundle = intent.getExtras();
            Notification.Builder builder = new Notification.Builder(context);
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(),R.mipmap.time);
            builder.setContentTitle(bundle.getString("name"))
                    .setContentText(bundle.getString("counedownday"))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(bm)
                    .setAutoCancel(true);
            Intent intent1 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            builder.setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify = builder.build();
            manager.notify(0, notify);
        }
    }
}
