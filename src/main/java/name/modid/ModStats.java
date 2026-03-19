package name.modid;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModStats {

    public static final Map<String, Integer> COUNTS = new LinkedHashMap<>();

    static {
        COUNTS.put("ultranium", 0);
        COUNTS.put("garnet", 0);
        COUNTS.put("moon_stone", 0);
    }

    public static void increment(String blockId) {
        COUNTS.merge(blockId, 1, Integer::sum);
    }
}