package ddioriomendes.smartpendant.spmessage;

import java.util.ArrayList;

/**
 * Created by ddiorio on 24-Oct-15.
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
        String json = "{\"actuation\": {\"target\": " + target + ", \"states\": [";
        for(Value v : states) {
            json = json.concat(v.getJson() + ",");
        }
        return json.concat("]}}\n");
    }

    protected abstract class Value<T extends Object> {
        protected T value;
        protected long duration;

        public abstract String getJson();
    }
}
