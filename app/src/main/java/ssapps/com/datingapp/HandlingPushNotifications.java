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

        Toast.makeText(context,"BNotification recieved",Toast.LENGTH_LONG).show();
        Toast.makeText(context,intent.getStringExtra("message"),Toast.LENGTH_LONG).show();


        prefs = new Prefs(context);
        //displayNotification();
        if (intent.getStringExtra("message").contains("chat")) {
            Toast.makeText(context,"Entered chat",Toast.LENGTH_LONG).show();
            displayNotification(context, intent.getStringExtra(PublishOptions.ANDROID_TICKER_TEXT_TAG),
                    intent.getStringExtra(PublishOptions.ANDROID_CONTENT_TITLE_TAG), intent.getStringExtra(PublishOptions.ANDROID_CONTENT_TEXT_TAG),
                    intent.getStringExtra("message"));
            return false;
        } else {
            Toast.makeText(context,"skipped chat",Toast.LENGTH_LONG).show();
        }

        return true;
    }


    private void displayNotification(Context context,String ticker_text,String title_text,String chat_message,String type){

        if (!prefs.isInChat()) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, prefs.getname());

            builder.setTicker("New Message");
            builder.setContentTitle("Message from " + ticker_text);
            builder.setContentText(chat_message);
            builder.setSmallIcon(R.drawable.fb);

            Intent i = new Intent(context, MainActivity.class);
           // if (ticker_text.equals("message")) {

                i.putExtra("chatRedirect", "Yes");

                Message message = new Message();
                message.setChat_message(chat_message);
                message.setTime(String.valueOf(Calendar.getInstance().getTimeInMillis()));
//                message.setTo(prefs.getname());
//                message.setFrom(title_text);
                message.setMessage_to(title_text);
                message.setMessage_from(ticker_text);
                message.setObjectId(type.substring(type.indexOf(",")+1));
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
