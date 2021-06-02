package uk.ac.abertay.cmp309.WalkMapper;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    SQLiteHelper sqLiteHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        sqLiteHelper = SQLiteHelper.getInstance(context);
        if (sqLiteHelper.loadRecentWalks(1).size() == 0) {
            // create pending intent to open main activity when notification pressed
            Intent openIntent = new Intent(context, MainActivity.class);
            PendingIntent openPI = PendingIntent.getActivity(context, 1, openIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "walkNotification")
                    .setSmallIcon(android.R.drawable.ic_dialog_map)
                    .setContentTitle("Walk Reminder")
                    .setContentText("Remeber to go for a walk today")
                    .setContentIntent(openPI)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(200, builder.build());
        }
    }
}
