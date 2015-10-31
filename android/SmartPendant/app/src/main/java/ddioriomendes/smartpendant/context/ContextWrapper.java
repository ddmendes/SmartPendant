package ddioriomendes.smartpendant.context;

import android.content.Context;
import android.util.Log;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import ddioriomendes.smartpendant.spmessage.SpEvent;

/**
 * Created by ddiorio on 29-Oct-15.
 */
public class ContextWrapper implements SpEvent.EventHandler {

    private static final String TAG = "ContextWrapper";
    private static ContextWrapper sharedInstance = null;

    private android.content.Context mContext;
    
    private ArrayList<CEHTuple> leftTopHandlers;
    private ArrayList<CEHTuple> leftBottomHandlers;
    private ArrayList<CEHTuple> rightTopHandlers;
    private ArrayList<CEHTuple> rightBottomHandlers;
    private ArrayList<CEHTuple> doubleTopHandlers;
    private ArrayList<CEHTuple> doubleBottomHandlers;
    private ArrayList<CEHTuple> doubleLeftHandlers;
    private ArrayList<CEHTuple> doubleRightHandlers;
    

    private ContextWrapper(android.content.Context context) {
        mContext = context;
        
        leftTopHandlers = new ArrayList<>();
        leftBottomHandlers = new ArrayList<>();
        rightTopHandlers = new ArrayList<>();
        rightBottomHandlers = new ArrayList<>();
        doubleTopHandlers = new ArrayList<>();
        doubleBottomHandlers = new ArrayList<>();
        doubleLeftHandlers = new ArrayList<>();
        doubleRightHandlers = new ArrayList<>();
    }
    
    protected void registerSpEventHandler(int button, Context context, SpEvent.EventHandler handler) {
        CEHTuple entry = new CEHTuple(context, handler);
        switch (button) {
            case SpEvent.SRC_LEFT_TOP:
                leftTopHandlers.add(entry);
                break;
            case SpEvent.SRC_LEFT_BOTTOM:
                leftBottomHandlers.add(entry);
                break;
            case SpEvent.SRC_RIGHT_TOP:
                rightTopHandlers.add(entry);
                break;
            case SpEvent.SRC_RIGHT_BOTTOM:
                rightBottomHandlers.add(entry);
                break;
            case SpEvent.SRC_DOUBLE_TOP:
                doubleTopHandlers.add(entry);
                break;
            case SpEvent.SRC_DOUBLE_BOTTOM:
                doubleBottomHandlers.add(entry);
                break;
            case SpEvent.SRC_DOUBLE_LEFT:
                doubleLeftHandlers.add(entry);
                break;
            case SpEvent.SRC_DOUBLE_RIGHT:
                doubleRightHandlers.add(entry);
                break;
            default:
                Log.e(TAG, "Unknown button to register handler: " + button);
                break;
        }
    }

    public static ContextWrapper getInstance(android.content.Context context) {
        if(sharedInstance == null) {
            sharedInstance = new ContextWrapper(context);
            MusicContext.construct(context);
        }
        return sharedInstance;
    }

    @Override
    public void onEvent(SpEvent event) {
        ArrayList<CEHTuple> searchScope = getHandlerList(event.getSource());

        for(CEHTuple tuple : searchScope) {
            if(tuple.context.isActive()) {
                tuple.eventHandler.onEvent(event);
                break;
            }
        }
    }

    private ArrayList<CEHTuple> getHandlerList(int button)
            throws InvalidParameterException {
        switch (button) {
            case SpEvent.SRC_LEFT_TOP:
                return leftTopHandlers;
            case SpEvent.SRC_LEFT_BOTTOM:
                return leftBottomHandlers;
            case SpEvent.SRC_RIGHT_TOP:
                return rightTopHandlers;
            case SpEvent.SRC_RIGHT_BOTTOM:
                return rightBottomHandlers;
            case SpEvent.SRC_DOUBLE_TOP:
                return doubleTopHandlers;
            case SpEvent.SRC_DOUBLE_BOTTOM:
                return doubleBottomHandlers;
            case SpEvent.SRC_DOUBLE_LEFT:
                return doubleLeftHandlers;
            case SpEvent.SRC_DOUBLE_RIGHT:
                return doubleRightHandlers;
            default:
                throw new InvalidParameterException("Not a valid smartpendant button");
        }
    }

    protected interface Context {
        public Boolean isActive();
    }

    private class CEHTuple {
        protected Context context;
        protected SpEvent.EventHandler eventHandler;

        protected CEHTuple(Context C, SpEvent.EventHandler EH) {
            context = C;
            eventHandler = EH;
        }
    }
}
