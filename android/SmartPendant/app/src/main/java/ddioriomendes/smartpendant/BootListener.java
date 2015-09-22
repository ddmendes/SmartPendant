package ddioriomendes.smartpendant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootListener extends BroadcastReceiver {

    public static final String TAG = "BootListener";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent accessoryIntent = new Intent(context, AccessoryDaemon.class);
        context.startService(accessoryIntent);
    }
}
