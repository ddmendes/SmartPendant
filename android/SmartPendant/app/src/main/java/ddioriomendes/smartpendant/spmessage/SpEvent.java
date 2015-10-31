package ddioriomendes.smartpendant.spmessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Represents the smartpendant events.
 * @author Davi Diorio Mendes [ddioriomendes@gmail.com]
 */
public class SpEvent {
    private static final String TAG = "SpEvent";

    public static final int TYPE_BUTTON_PUSH = 10;
    public static final int LENGTH_SHORT = 20;
    public static final int LENGTH_LONG = 21;
    public static final int SRC_LEFT_TOP = 30;
    public static final int SRC_LEFT_BOTTOM = 31;
    public static final int SRC_RIGHT_TOP = 32;
    public static final int SRC_RIGHT_BOTTOM = 33;
    public static final int SRC_DOUBLE_TOP = 34;
    public static final int SRC_DOUBLE_BOTTOM = 35;
    public static final int SRC_DOUBLE_LEFT = 36;
    public static final int SRC_DOUBLE_RIGHT = 37;

    private static final String KEY_TYPE = "type";
    private static final String KEY_LENGTH = "length";
    private static final String KEY_SOURCE = "source";

    private static final HashMap<String, Integer> paramMap = new ParamMap();

    private final int type;
    private final int length;
    private final int source;

    public SpEvent(JSONObject jsonEvent) throws JSONException {
        type = paramMap.get(jsonEvent.getString(KEY_TYPE));
        length = paramMap.get(jsonEvent.getString(KEY_LENGTH));
        source = paramMap.get(jsonEvent.getString(KEY_SOURCE));
    }

    public int getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public int getSource() {
        return source;
    }

    public String toString() {
        return "SpEvent: " + hashCode() + "\n" +
                "type: " + getType() + "\n" +
                "length: " + getLength() + "\n" +
                "source: " + getSource();
    }

    private static class ParamMap extends HashMap<String, Integer> {
        public ParamMap() {
            super();
            super.put("button-push", TYPE_BUTTON_PUSH);
            super.put("short", LENGTH_SHORT);
            super.put("long", LENGTH_LONG);
            super.put("left_top", SRC_LEFT_TOP);
            super.put("left_bottom", SRC_LEFT_BOTTOM);
            super.put("right_top", SRC_RIGHT_TOP);
            super.put("right_bottom", SRC_RIGHT_BOTTOM);
            super.put("double_top", SRC_DOUBLE_TOP);
            super.put("double_bottom", SRC_DOUBLE_BOTTOM);
            super.put("double_left", SRC_DOUBLE_LEFT);
            super.put("double_right", SRC_DOUBLE_RIGHT);
        }

        @Override
        public Integer put(String key, Integer value) {
            throw new UnsupportedOperationException("This map is blocked to new entries.");
        }
    }

    public interface EventHandler {
        public void onEvent(SpEvent event);
    }
}
