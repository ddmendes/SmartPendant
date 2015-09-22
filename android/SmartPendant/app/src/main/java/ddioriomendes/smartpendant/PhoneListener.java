package ddioriomendes.smartpendant;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by ddiorio on 21-Sep-15.
 */
public class PhoneListener extends PhoneStateListener {

    public static final String TAG = "PhoneListener";

    private TelephonyManager telephonyManager;

    public PhoneListener(TelephonyManager telephonyManager) {
        this.telephonyManager = telephonyManager;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Log.d(TAG, "Incoming call from " + incomingNumber);
    }
}
