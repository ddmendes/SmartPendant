package ddioriomendes.smartpendant;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Listens for phone state changes.
 * @author Davi Diorio Mendes [ddioriomendes@gmail.com]
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
