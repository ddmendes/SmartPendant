package ddioriomendes.smartpendant.spmessage;

/**
 * Abstracts the creation of vibra actuation messages
 * @author Davi Diorio Mendes [ddioriomendes@gmail.com]
 */
public class SpVibratorActuation extends SpActuation {

    public static final int LENGTH_SHORT = 200;

    public SpVibratorActuation() {
        super("vibra");
    }

    public static SpVibratorActuation vibrate(int pulses, long pulseLength) {
        SpVibratorActuation vibra = new SpVibratorActuation();

        for(int i = 0; i < pulses; ++i) {
            vibra.addState(new VibratorValue(1, pulseLength));
            vibra.addState(new VibratorValue(0, pulseLength));
        }

        return vibra;
    }

    protected static class VibratorValue extends Value<Integer> {

        public VibratorValue(Integer value, long pulseLength) {
            this.value = value;
            this.duration = pulseLength;
        }

        @Override
        public String getJson() {
            return "{\"val\": " + value + ", \"dur\": " + duration + "}";
        }
    }
}
