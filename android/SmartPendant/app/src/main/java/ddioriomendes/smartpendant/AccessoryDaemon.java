package ddioriomendes.smartpendant;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.IOException;

import ddioriomendes.smartpendant.spcontext.ContextWrapper;
import ddioriomendes.smartpendant.spmessage.SpLedActuation;
import ddioriomendes.smartpendant.spmessage.SpMessage;
import ddioriomendes.smartpendant.spmessage.SpVibratorActuation;

/*
 * Este trabalho eu preciso
 * concluir pra eu casar
 * com uma pi muito lindinha
 * que encantou o meu olhar
 */

/**
 * SmartPendant background service.
 *
 * Controls the resource instantiation, receives notification state change and listens telephony state.
 *
 * @author Davi Diorio Mendes [ddioriomendes@gmail.com]
 */
public class AccessoryDaemon extends AccessibilityService {
    public static final String TAG = "AccessoryDaemon";

    private static final int NOTIFICATION_CONNECTED_ID       = 0x01;
    private static final int NOTIFICATION_CONNECTION_LOST_ID = 0x02;

    private static AccessoryDaemon sharedInstance = null;

    private AccessibilityServiceInfo info = new AccessibilityServiceInfo();
    private BluetoothSerial mBluetoothSerial;
    private ContextWrapper contextWrapper;
    private SpMessage spMessage;

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
    public void onDestroy() {
        AccessoryDaemon.sharedInstance = null;
    }

    private void mStart() {
        Log.d(TAG, "onStartCommand");
        mBluetoothSerial = new BluetoothSerial(this, btListener);
        contextWrapper = ContextWrapper.getInstance(this.getApplicationContext());
        spMessage = new SpMessage();
        spMessage.addEventListener(contextWrapper);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(new ScreenListener(), intentFilter);

        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(new BluetoothStateListener(), intentFilter);

        try {
            Process p = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, String.valueOf(event.getText()));
        int eventType = event.getEventType();
        if(eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            SpMessage.Builder builder = spMessage.getBuilder();
            builder.addActuation(SpLedActuation.blink(Color.CYAN, 2, 200));
            builder.addActuation(SpVibratorActuation.vibrate(2, SpVibratorActuation.LENGTH_SHORT));
            String msg = builder.buildMessage();
            Log.d(TAG, "Sending message: " + msg);
            mBluetoothSerial.write(msg);
        }
    }

    public void btWrite(char c) {
        SpMessage.Builder builder = spMessage.getBuilder();

        switch (c) {
            case 'r':
                builder.addActuation(SpLedActuation.blink(Color.RED, 2, 200));
                break;
            case 'g':
                builder.addActuation(SpLedActuation.blink(Color.GREEN, 2, 200));
                break;
            case 'b':
                builder.addActuation(SpLedActuation.blink(Color.BLUE, 2, 200));
                break;
            case 'v':
                builder.addActuation(SpVibratorActuation.vibrate(2, 200));
                builder.addActuation(SpLedActuation.blink(Color.BLUE, 2, 200));
                break;
        }

        mBluetoothSerial.write(builder.buildMessage());
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

    private BluetoothSerial.BluetoothSerialListener btListener =
            new BluetoothSerial.BluetoothSerialListener() {

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
            spMessage.processMessage(message);
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

    private class BluetoothStateListener extends BroadcastReceiver {

        public BluetoothStateListener() {
            if(mBluetoothSerial.isEnabled()) {
                mBluetoothSerial.findBondedDevice(getString(R.string.bt_serial_prefix));
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if(!intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                Log.d("BluetoothStateListener", "Wrong action " + intent.getAction() + ".");
                return;
            }

            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    Log.d(TAG, "Bluetooth turned on.");
                    mBluetoothSerial.findBondedDevice(getString(R.string.bt_serial_prefix));
                    break;
                case BluetoothAdapter.STATE_OFF:
                    Log.d(TAG, "Bluetooth turned off.");
                    try {
                        mBluetoothSerial.close();
                    } catch (IOException e) {
                        Log.e(TAG, "When closing bluetooth serial: " + e.getMessage());
                    }
                    break;
            }
        }
    }

    public static AccessoryDaemon getInstance() {
        return AccessoryDaemon.sharedInstance;
    }
}
