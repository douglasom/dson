package douglasom.json2csv;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MapUtils {
    public static Map flatten(Map sourceMap, String keySeparator) {
        TreeMap destinyMap = new TreeMap();
        flattenInto(sourceMap, destinyMap, null, keySeparator);
        return destinyMap;
    }

    public static Map flatten(Map sourceMap) {
        return flatten(sourceMap, ".");
    }

    /* tail recursion */
    protected static void flattenInto(Map sourceMap, Map destinyMap, String keyPrefix, String keySeparator) {
        Set<Map.Entry> set = sourceMap.entrySet();
        for (Map.Entry entry : set) {
            if (entry.getValue() != null && entry.getValue() instanceof Map) {
                flattenInto((Map) entry.getValue(), destinyMap, concatenate(keyPrefix, String.valueOf(entry.getKey()), keySeparator), keySeparator);
            } else {
                destinyMap.put(concatenate(keyPrefix, String.valueOf(entry.getKey()), keySeparator), entry.getValue());
            }
        }
    }

    protected static String concatenate(String k, String v, String keySeparator) {
        return k == null ? v : String.format("%s%s%s", k, keySeparator, v);
    }
}
