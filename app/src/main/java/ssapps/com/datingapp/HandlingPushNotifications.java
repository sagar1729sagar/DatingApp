package ssapps.com.datingapp;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.push.BackendlessPushService;

/**
 * Created by sagar on 19/02/18.
 */

public class HandlingPushNotifications extends BackendlessPushService {
    @Override
    public boolean onMessage(Context context, Intent intent) {
        return super.onMessage(context, intent);
        // todo display push notification
    }

    @Override
    public void onError(Context context, String message) {
        super.onError(context, message);
        Toast.makeText(context,"Error recieving push notifications : "+message,Toast.LENGTH_SHORT).show();
    }
}
