package douglasom.json2csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Dson {
    private Gson g = getGsonBuilder().create();

    private boolean readNullsFromJson = true;

    private String nestedFieldSeparator = ".";

    /**
     * If true, will generate columns even for the fields that are null (but present) at the JSON.
     */
    public Dson setReadNullsFromJson(boolean readNullsFromJson) {
        this.readNullsFromJson = readNullsFromJson;
        return this;
    }

    /**
     * String appended right before each nested field at the column header. Default value is ".". That means for a
     * JSON like this: { "book": { "title": "Pragmatic Programmer" } } the property "title" will become a column
     * with the following header: "book.title".
     */
    public Dson setNestedFieldSeparator(String separator) {
        this.nestedFieldSeparator = separator;
        return this;
    }

    private GsonBuilder getGsonBuilder() {
        return new GsonBuilder().registerTypeAdapter(Map.class, new MapDeserializer()).registerTypeAdapter(List.class, new ListDeserializer());
    }

    /**
     * Generates a CSV row and respective headers. The JSON object is flattened, which means each nested field becomes
     * a column.
     *
     * @param json a JSON object as a String.
     * @return a String containing the generated CSV.
     */
    public String jsonToCsv(String json) {
        return mapToCsv(jsonToMap(json));
    }

    /**
     * Reads a JSON into a Map, nesting embedded objects as instances of Map as well and collections as instances of
     * List. Primitives are all read as String.
     *
     * @param json a JSON as String
     * @return a Map where each key is a String and each value is either a String, a List or a Map
     * (recursive).
     */
    protected Map jsonToMap(String json) {
        return g.fromJson(json, Map.class);
    }

    /**
     * Converts a Map into a flat, single row CSV String.
     *
     * @param map The map to be converted
     * @return a single row (and its headers) CSV
     */
    protected String mapToCsv(Map map) {
        Map flatMap = MapUtils.flatten(map, this.nestedFieldSeparator);
        return flatMapToCsv(flatMap);
    }

    /**
     * Generates a CSV from a flat map using its keys as column headers and its values as column values.
     * @param flatMap a Map that won't be dug deep for its values.
     * @return a String containing a CSV row and its column headers. A line break at the end.
     */
    protected String flatMapToCsv(Map flatMap) {
        String csv;
        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        for (Object keyObj : flatMap.keySet()) {
            String key = String.valueOf(keyObj);
            csvSchemaBuilder.addColumn(key);
        }
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();

        CsvMapper csvMapper = new CsvMapper();
        try {
            csv = csvMapper.writer(csvSchema).writeValueAsString(flatMap);
        } catch (Exception e) {
            throw new RuntimeException("There was an unexpected error when generating the CSV.", e);
        }
        return csv;
    }

    private class MapDeserializer implements JsonDeserializer<Map<String, Object>> {

        public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<String, Object> deserializedMap = new LinkedHashMap<>();
            JsonObject jsonObject = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                Object deserializedValue = Dson.this.deserialize(entry.getValue());
                if (readNullsFromJson || deserializedValue != null) {
                    deserializedMap.put(key, deserializedValue);
                }
            }
            return deserializedMap;
        }
    }

    private class ListDeserializer implements JsonDeserializer<List<Object>> {

        public List<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<Object> deserializedList = new ArrayList<>();
            JsonArray jsonArray = json.getAsJsonArray();
            for (JsonElement v : jsonArray) {
                Object deserializedValue = Dson.this.deserialize(v);
                if (deserializedValue != null) {
                    deserializedList.add(deserializedValue);
                }
            }
            return deserializedList;
        }
    }

    private Object deserialize(JsonElement jsonElement) {
        Object deserializedValue;
        if (jsonElement.isJsonNull()) {
            deserializedValue = null;
        } else if (jsonElement.isJsonObject()) {
            deserializedValue = deserializeMap(jsonElement);
        } else if (jsonElement.isJsonArray()) {
            deserializedValue = deserializeList(jsonElement);
        } else if (jsonElement.isJsonPrimitive()) {
            deserializedValue = deserializePrimitive(jsonElement);
        } else {
            throw new RuntimeException("Unexpected jsonElement type. Can't be deserialized.");
        }
        return deserializedValue;
    }

    private String deserializePrimitive(JsonElement jsonElement) {
        return jsonElement.getAsString();
    }

    private List deserializeList(JsonElement jsonElement) {
        return g.fromJson(jsonElement, List.class);
    }

    private Map deserializeMap(JsonElement jsonElement) {
        return g.fromJson(jsonElement, Map.class);
    }
}
