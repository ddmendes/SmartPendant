package ddioriomendes.smartpendant.spmessage;

import android.graphics.Color;

/**
 * Abstracts the creation of led actuation messages.
 * @author Davi Diorio Mendes [ddioriomendes@gmail.com]
 */
public class SpLedActuation extends SpActuation {

    public SpLedActuation() {
        super("led");
    }

    public static SpLedActuation blink(int color, int count, int length) {
        SpLedActuation led = new SpLedActuation();

        for(int i = 0; i < count; ++i) {
            led.addState(new LedValue(color, length));
            led.addState(new LedValue(Color.BLACK, length));
        }

        return led;
    }

    private static class LedValue extends Value<Integer> {

        public LedValue(int color, long duration) {
            value = color;
            this.duration = duration;
        }

        @Override
        public String getJson() {
            return "{\"val\":{\"r\":" + Color.red(value) +
                    ",\"g\":" + Color.green(value) +
                    ",\"b\":" + Color.blue(value) +
                    "},\"dur\":" + duration + "}";
        }
    }
}
