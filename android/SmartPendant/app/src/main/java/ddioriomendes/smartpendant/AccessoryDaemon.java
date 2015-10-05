package ddioriomendes.smartpendant;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class AccessoryDaemon extends AccessibilityService {

    public static final String TAG = "AccessoryDaemon";
    private AccessibilityServiceInfo info = new AccessibilityServiceInfo();

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        this.setServiceInfo(info);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(new ScreenListener(), intentFilter);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneListener(telephonyManager), PhoneStateListener.LISTEN_CALL_STATE);

        return START_STICKY;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, String.valueOf(event.getText()));
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    private class ScreenListener extends BroadcastReceiver {

        static final int NOTIFICATION_TIMEOUT = 50;

        long lastNotification = System.currentTimeMillis();

        @Override
        public void onReceive(Context context, Intent intent) {
            if(System.currentTimeMillis() - lastNotification > NOTIFICATION_TIMEOUT) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                    Log.d(TAG, "Screen off.");
                } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                    Log.d(TAG, "Screen on.");
                }
                lastNotification = System.currentTimeMillis();
            }
        }
    }
}
