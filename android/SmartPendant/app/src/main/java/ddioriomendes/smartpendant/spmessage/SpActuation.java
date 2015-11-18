package ddioriomendes.smartpendant.spmessage;

import java.util.ArrayList;

/**
 * Controls the creation and JSON generation of SmartPendant actuation messages.
 * @author Davi Diorio Mendes [ddioriomendes@gmail.com]
 */
public class SpActuation {
    private String target;
    private ArrayList<Value> states;

    public SpActuation(String target) {
        this.target = target;
        states = new ArrayList<>();
    }

    public void addState(Value v) {
        states.add(v);
    }

    public String getJson() {
        String json = "{\"target\":\"" + target +
                "\",\"states\":[";
        int i = 0;

        for(i = 0; i < states.size() - 1; ++i) {
            json = json.concat(states.get(i).getJson() + ",");
        }
        json = json.concat(states.get(i).getJson());

        /*for(Value v : states) {
            json = json.concat(v.getJson() + ",");
        }*/

        return json.concat("]}");
    }

    protected static abstract class Value<T> {
        protected T value;
        protected long duration;

        public abstract String getJson();
    }
}
