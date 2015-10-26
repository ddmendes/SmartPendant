package ddioriomendes.smartpendant.spmessage;

import android.graphics.Color;

/**
 * Created by ddiorio on 26-Oct-15.
 */
public class SpLedActuation extends SpActuation {

    public SpLedActuation() {
        super("led");
    }

    public void blink(int color, int count, int length) {
        for(int i = 0; i < count; ++i) {
            addState(new LedValue(color, length));
            addState(new LedValue(Color.BLACK, length));
        }
    }

    private class LedValue extends Value<Integer> {

        public LedValue(int color, long duration) {
            value = color;
            this.duration = duration;
        }

        @Override
        public String getJson() {
            return "{\"value\": {\"red\": " + Color.red(value) +
                    ", \"green\": " + Color.green(value) +
                    ", \"blue\": " + Color.blue(value) +
                    "}, \"duration\": " + duration + "}";
        }
    }
}
