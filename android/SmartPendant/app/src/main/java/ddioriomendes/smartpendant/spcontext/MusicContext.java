package ddioriomendes.smartpendant.spcontext;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import ddioriomendes.smartpendant.spmessage.SpEvent;

/**
 * Created by ddiorio on 29-Oct-15.
 */
public class MusicContext implements ContextWrapper.SpContext {
    private static final String TAG = "MusicContext";
    private static final String MUSIC_COMMAND = "com.android.music.musicservicecommand";
    private static final String COMMAND = "command";
    private static final String CMD_TOGGLEPAUSE = "togglepause";
    private static final String CMD_STOP = "stop";
    private static final String CMD_PREVIOUS = "previous";
    private static final String CMD_NEXT = "next";

    private static MusicContext sharedInstance = null;

    private Context mContext;
    private AudioManager mAudioManager;
    private boolean paused;

    private MusicContext(Context context) {
        mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        paused = false;
        Log.d(TAG, "MusicContext created.");
    }

    public MusicContext getInstance(Context context) {
        if(sharedInstance == null) {
            MusicContext.construct(context);
        }

        return sharedInstance;
    }

    @Override
    public Boolean isActive() {
        Boolean b = mAudioManager.isMusicActive() || paused;
        Log.d(TAG, "isActive(): " + b);
        return b;
    }

    protected static void construct(Context context) {
        if(sharedInstance == null) {
            sharedInstance = new MusicContext(context);
        }

        ContextWrapper contextWrapper = ContextWrapper.getInstance(context);

        contextWrapper.registerSpEventHandler(
                SpEvent.SRC_LEFT_TOP,
                sharedInstance,
                sharedInstance.playPauseHandler);

        contextWrapper.registerSpEventHandler(
                SpEvent.SRC_LEFT_BOTTOM,
                sharedInstance,
                sharedInstance.stopHandler);

        contextWrapper.registerSpEventHandler(
                SpEvent.SRC_RIGHT_BOTTOM,
                sharedInstance,
                sharedInstance.previousHandler);

        contextWrapper.registerSpEventHandler(
                SpEvent.SRC_RIGHT_TOP,
                sharedInstance,
                sharedInstance.nextHandler);
    }

    protected SpEvent.EventHandler playPauseHandler = new SpEvent.EventHandler() {
        @Override
        public void onEvent(SpEvent event) {
            Log.d(TAG, "playPauseHandler");
            Intent intent = new Intent(MUSIC_COMMAND);
            intent.putExtra(COMMAND, CMD_TOGGLEPAUSE);
            mContext.sendBroadcast(intent);
        }
    };

    protected SpEvent.EventHandler stopHandler = new SpEvent.EventHandler() {
        @Override
        public void onEvent(SpEvent event) {
            Log.d(TAG, "stopHandler");
            Intent intent = new Intent(MUSIC_COMMAND);
            intent.putExtra(COMMAND, CMD_STOP);
            mContext.sendBroadcast(intent);
        }
    };

    protected SpEvent.EventHandler nextHandler = new SpEvent.EventHandler() {
        @Override
        public void onEvent(SpEvent event) {
            Log.d(TAG, "nextHandler");
            Intent intent = new Intent(MUSIC_COMMAND);
            intent.putExtra(COMMAND, CMD_NEXT);
            mContext.sendBroadcast(intent);
        }
    };

    protected SpEvent.EventHandler previousHandler = new SpEvent.EventHandler() {
        @Override
        public void onEvent(SpEvent event) {
            Log.d(TAG, "previousHandler");
            Intent intent = new Intent(MUSIC_COMMAND);
            intent.putExtra(COMMAND, CMD_PREVIOUS);
            mContext.sendBroadcast(intent);
        }
    };
}
