package biz.riopapa.chatread.models;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class KeyVal {

    static Map<String, String> maps;
    public KeyVal() {
        maps = new HashMap<>();
    }
    public boolean isDup(String key, String val) {
        if (maps == null) {
            maps = new HashMap<>();
            maps.put(key, val);
            return false;
        } else if (maps.containsKey(key)) {
            if (maps.get(key).equals(val))
                return true;
            maps.replace(key, val);
            return false;
        }
        maps.put(key, val);
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Size=").append(maps.size()).append("\n");
        if (!maps.isEmpty()) {
            int i = 0;
            for (String key : maps.keySet()) {
                sb.append(i++).append(") [").append(key).append("] > ")
                        .append(maps.get(key)).append("\n");
            }
        } else
            sb.append("no data\n");
        return sb.toString();
    }

}