package ssapps.com.datingapp;

import com.backendless.Backendless;
import com.backendless.push.BackendlessBroadcastReceiver;
import com.backendless.push.BackendlessPushService;

/**
 * Created by sagar on 19/02/18.
 */

public class PushNotificationReciever extends BackendlessBroadcastReceiver {

    @Override
    public Class<? extends BackendlessPushService> getServiceClass() {
        return HandlingPushNotifications.class;
    }
}
