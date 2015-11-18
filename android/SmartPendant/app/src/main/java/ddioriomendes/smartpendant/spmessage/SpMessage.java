package ddioriomendes.smartpendant.spmessage;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ddiorio on 15-Nov-15.
 */
public class SpMessage {
    public static final String TAG = "SpMessage";

    private ArrayList<SpEvent.EventHandler> listeners;

    public SpMessage() {
        listeners = new ArrayList<>();
    }

    public void addEventListener(SpEvent.EventHandler listener) {
        listeners.add(listener);
    }

    public void removeEventListener(SpEvent.EventHandler listener) {
        listeners.remove(listener);
    }

    public void processMessage(String message) {
        JSONObject event;
        try {
            event = new JSONObject(message);
            SpEvent spEvent = new SpEvent(event.getJSONObject("event"));

            for(SpEvent.EventHandler eh : listeners) {
                eh.onEvent(spEvent);
            }

            Log.d(TAG, "Message received: " + spEvent.toString());
        } catch (JSONException e) {
            Log.e(TAG, "When parsing event JSON: " + e.getMessage());
        }
    }

    public Builder getBuilder() {
        return new Builder();
    }

    public class Builder {
        private ArrayList<SpActuation> actuations;

        public Builder() {
            actuations = new ArrayList<>();
        }

        public void addActuation(SpActuation actuation) {
            actuations.add(actuation);
        }

        public String buildMessage() {
            JSONObject spmessage = new JSONObject();

            try {
                spmessage.put("mod", "ddiorio");
                spmessage.put("ver", 0.1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray actsArray = new JSONArray();




            String message = "{\"spmsg\":{\"mod\":\"ddiorio\",\"ver\":0.1},\"acts\":[";
            int i = 0;

            for(i = 0; i < actuations.size() - 1; ++i) {
                message = message.concat(actuations.get(i).getJson());
                message = message.concat(",");
            }
            message = message.concat(actuations.get(i).getJson());

            /*for(SpActuation act : actuations) {
                message = message.concat(act.getJson());
                message = message.concat(",");
            }*/

            return message.concat("]}\n");
        }
    }
}
