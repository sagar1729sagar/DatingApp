package ssapps.com.datingapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.messaging.PublishOptions;
import com.backendless.push.BackendlessPushService;

import java.util.Calendar;

import Models.Message;
import Util.Prefs;

/**
 * Created by sagar on 19/02/18.
 */

public class HandlingPushNotifications extends BackendlessPushService {


    private Prefs prefs;

    @Override
    public boolean onMessage(Context context, Intent intent) {

        prefs = new Prefs(context);
        //displayNotification();
        displayNotification(context,intent.getStringExtra(PublishOptions.ANDROID_TICKER_TEXT_TAG),
                intent.getStringExtra(PublishOptions.ANDROID_CONTENT_TITLE_TAG),intent.getStringExtra(PublishOptions.ANDROID_CONTENT_TEXT_TAG),
                intent.getStringExtra("message"));
        return false;
    }


    private void displayNotification(Context context,String ticker_text,String title_text,String chat_message,String type){

        if (!prefs.isInChat()) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, prefs.getname());

            builder.setTicker("Message from " + title_text);
            builder.setContentTitle(title_text);
            builder.setContentText(chat_message);
            builder.setSmallIcon(R.drawable.fb);

            Intent i = new Intent(context, MainActivity.class);
           // if (ticker_text.equals("message")) {

                i.putExtra("chatRedirect", "Yes");

                Message message = new Message();
                message.setChat_message(chat_message);
                message.setTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                message.setTo(prefs.getname());
                message.setFrom(title_text);
                message.setObjectId(ticker_text);
                message.setType("Notification");
                message.save();


           // }


            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);

            stackBuilder.addNextIntent(i);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);


            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(123, builder.build());

        }

    }

    @Override
    public void onError(Context context, String message) {
        super.onError(context, message);

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()+1000,
                restartServicePendingIntent);
        super.onTaskRemoved(rootIntent);
    }
}
