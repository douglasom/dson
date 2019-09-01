package douglasom.json2csv;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    public Dson setReadNullsFromJson(boolean readNullsFromJson) {
        this.readNullsFromJson = readNullsFromJson;
        return this;
    }

    public boolean isReadNullsFromJson() {
        return this.readNullsFromJson;
    }

    private GsonBuilder getGsonBuilder() {
        return new GsonBuilder().registerTypeAdapter(Map.class, new MapDeserializer()).registerTypeAdapter(List.class, new ListDeserializer());
    }

    public String jsonToCsv(String json) {
        String csv;
        try {
            Map jsonObj = jsonToMap(json);
            csv = mapToJson(jsonObj);
        } catch (Exception e) {
            throw new RuntimeException("There was an unexpected error when generating the CSV.", e);
        }
        return csv;
    }

    protected Map jsonToMap(String json) {
        return g.fromJson(json, Map.class);
    }

    protected String mapToJson(Map map) throws JsonProcessingException {
        Map flatMap = MapUtils.flatten(map);
        return flatMapToJson(flatMap);
    }

    protected String flatMapToJson(Map flatMap) throws JsonProcessingException {
        String csv;
        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        for (Object keyObj : flatMap.keySet()) {
            String key = String.valueOf(keyObj);
            csvSchemaBuilder.addColumn(key);
        }
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();

        CsvMapper csvMapper = new CsvMapper();
//        csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS); the way we're using it it makes no difference
        csv = csvMapper.writer(csvSchema).writeValueAsString(flatMap);
        return csv;
    }

    private class MapDeserializer implements JsonDeserializer<Map<String, Object>> {

        public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<String, Object> m = new LinkedHashMap<>();
            JsonObject jo = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> mx : jo.entrySet()) {
                String key = mx.getKey();
                JsonElement v = mx.getValue();
                if (v.isJsonNull()) {
                    if (isReadNullsFromJson()) {
                        m.put(key, null);
                    }
                } else if (v.isJsonArray()) {
                    m.put(key, g.fromJson(v, List.class));
                } else if (v.isJsonPrimitive()) {
                    m.put(key, v.getAsString());
                } else if (v.isJsonObject()) {
                    m.put(key, g.fromJson(v, Map.class));
                }

            }
            return m;
        }
    }

    private class ListDeserializer implements JsonDeserializer<List<Object>> {

        public List<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            List<Object> m = new ArrayList<>();
            JsonArray arr = json.getAsJsonArray();
            for (JsonElement jsonElement : arr) {
                if (jsonElement.isJsonObject()) {
                    m.add(g.fromJson(jsonElement, Map.class));
                } else if (jsonElement.isJsonArray()) {
                    m.add(g.fromJson(jsonElement, List.class));
                } else if (jsonElement.isJsonPrimitive()) {
                    m.add(jsonElement.getAsString());
                }
            }
            return m;
        }
    }
}
