package ddioriomendes.smartpendant.spcontext;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import ddioriomendes.smartpendant.spmessage.SpEvent;

/**
 * Listens for phone state changes.
 * @author Davi Diorio Mendes [ddioriomendes@gmail.com]
 */
public class RingingContext extends PhoneStateListener implements ContextWrapper.SpContext {

    public static final String TAG = "RingingContext";

    private static RingingContext sharedInstance = null;

    private AudioManager audioManager;
    private TelephonyManager telephonyManager;
    private int state;

    public RingingContext(Context context) {
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(this, PhoneStateListener.LISTEN_CALL_STATE);

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        Log.d(TAG, "Incoming call from " + incomingNumber);
        synchronized (this) {
            this.state = state;
            audioManager.setStreamMute(AudioManager.STREAM_RING, false);
        }
    }

    @Override
    public Boolean isActive() {
        synchronized (this) {
            return state == TelephonyManager.CALL_STATE_RINGING;
        }
    }

    public static RingingContext construct(Context context) {
        if(sharedInstance == null) {
            sharedInstance = new RingingContext(context);

            ContextWrapper contextWrapper = ContextWrapper.getInstance(context);

            contextWrapper.registerSpEventHandler(
                    SpEvent.SRC_LEFT_TOP,
                    sharedInstance,
                    sharedInstance.silenceCallHandler);

            contextWrapper.registerSpEventHandler(
                    SpEvent.SRC_LEFT_BOTTOM,
                    sharedInstance,
                    sharedInstance.endCallHandler);

            return sharedInstance;
        } else {
            return null;
        }
    }

    protected SpEvent.EventHandler endCallHandler = new SpEvent.EventHandler() {
        @Override
        public void onEvent(SpEvent event) {
            try{
                Class c = Class.forName(telephonyManager.getClass().getName());
                Method m = c.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                ITelephony iTelephony = (ITelephony) m.invoke(telephonyManager);

                iTelephony.endCall();
            }catch (Exception e) {
                Log.e(TAG, "When ending call: " + e.getMessage());
            }
        }
    };

    protected SpEvent.EventHandler silenceCallHandler = new SpEvent.EventHandler() {
        @Override
        public void onEvent(SpEvent event) {
            audioManager.setStreamMute(AudioManager.STREAM_RING, true);
        }
    };
}
