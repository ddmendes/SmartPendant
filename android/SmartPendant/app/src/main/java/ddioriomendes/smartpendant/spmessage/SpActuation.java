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
        String json = "{\"actuation\": {\"target\": \"" + target +
                "\", \"steps\": " + states.size() + ", \"states\": [";
        for(Value v : states) {
            json = json.concat(v.getJson() + ",");
        }
        return json.concat("]}}\n");
    }

    protected abstract class Value<T> {
        protected T value;
        protected long duration;

        public abstract String getJson();
    }
}
