package ddioriomendes.smartpendant.spmessage;

/**
 * Created by ddiorio on 24-Oct-15.
 */
public class SpVibratorActuation extends SpActuation {

    public static final int LENGTH_SHORT = 200;

    public SpVibratorActuation(int pulses, long pulseLength) {
        super("vibra");
        for(int i = 0; i < pulses; ++i) {
            addState(new VibratorValue(255, pulseLength));
            addState(new VibratorValue(0, pulseLength));
        }
    }

    protected class VibratorValue extends Value<Integer> {

        public VibratorValue(Integer value, long pulseLength) {
            this.value = value;
            this.duration = pulseLength;
        }

        @Override
        public String getJson() {
            return "{\"value\": " + value + ", \"duration\": " + duration + "}";
        }
    }
}
