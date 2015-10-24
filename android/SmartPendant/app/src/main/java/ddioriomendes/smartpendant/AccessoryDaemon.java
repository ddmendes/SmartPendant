package ddioriomendes.smartpendant;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import org.json.JSONException;
import org.json.JSONObject;

import ddioriomendes.smartpendant.spmessage.SpEvent;
import ddioriomendes.smartpendant.spmessage.SpVibratorActuation;

public class AccessoryDaemon extends AccessibilityService {
    public static final String TAG = "AccessoryDaemon";
    private static final String ACCESSORY_NAME_PREFIX = "HC-05";

    private static final int NOTIFICATION_CONNECTED_ID       = 0x01;
    private static final int NOTIFICATION_CONNECTION_LOST_ID = 0x02;

    private static AccessoryDaemon sharedInstance = null;

    private AccessibilityServiceInfo info = new AccessibilityServiceInfo();
    private BluetoothSerial mBluetoothSerial;

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");

        AccessoryDaemon.sharedInstance = this;

        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        this.setServiceInfo(info);

        mStart();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        AccessoryDaemon.sharedInstance = null;
        return super.onUnbind(intent);
    }

    private int mStart() {
        Log.d(TAG, "onStartCommand");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(new ScreenListener(), intentFilter);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneListener(telephonyManager), PhoneStateListener.LISTEN_CALL_STATE);

        mBluetoothSerial = new BluetoothSerial(this, btListener);
        // TODO finds accessory asyncronously and repeat until connect.
        mBluetoothSerial.findBondedDevice(ACCESSORY_NAME_PREFIX);

        return START_STICKY;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, String.valueOf(event.getText()));
        int eventType = event.getEventType();
        if(eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            SpVibratorActuation msg = new SpVibratorActuation(1, SpVibratorActuation.LENGTH_SHORT);
            Log.d(TAG, "Sending message: " + msg.getJson());
            mBluetoothSerial.write(msg.getJson());
        }
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

    private BluetoothSerial.BluetoothSerialListener btListener = new BluetoothSerial.BluetoothSerialListener() {
        @Override
        public void onDeviceFound(BluetoothDevice device, int status) {
            if(status == BluetoothSerial.STATUS_DEVICE_FOUND) {
                Log.d(TAG, "Device found.");
                mBluetoothSerial.connect(device);
            }
        }

        @Override
        public void onDeviceConnected(int status) {
            if(status == BluetoothSerial.STATUS_DEVICE_CONNECTED) {
                Log.d(TAG, "Device connected.");
                notifyPendantConnected();
            }
        }

        private void notifyPendantConnected() {
            NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(AccessoryDaemon.this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.notification_connected_title))
                    .setContentText(getString(R.string.notification_connected_text))
                    .setOngoing(true);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIFICATION_CONNECTED_ID, mNotificationBuilder.build());
        }

        @Override
        public void onMessageReceived(String message) {
            JSONObject event = null;
            try {
                event = new JSONObject(message);
                SpEvent spEvent = new SpEvent(event.getJSONObject("event"));
                Log.d(TAG, "Message received: " + spEvent.toString());
            } catch (JSONException e) {
                Log.e(TAG, "When parsing event JSON: " + e.getMessage());
            }
        }

        @Override
        public void onConnectionLost() {
            Log.d(TAG, "Connection lost.");
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.cancel(NOTIFICATION_CONNECTED_ID);

            NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(AccessoryDaemon.this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.notification_connection_lost_title))
                    .setContentText(getString(R.string.notification_connection_lost_text));
            mNotificationManager.notify(NOTIFICATION_CONNECTION_LOST_ID, mNotificationBuilder.build());
        }
    };
}
