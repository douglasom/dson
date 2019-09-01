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
    private Gson g = new GsonBuilder().registerTypeAdapter(Map.class, new MapDeserializer()).registerTypeAdapter(List.class, new ListDeserializer()).create();

    public String jsonToCsv(String json) {
        String csv;
        try {
            Map jsonObj = g.fromJson(json, Map.class);
            Map flatMap = MapUtils.flatten(jsonObj);

            CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
            for (Object keyObj : flatMap.keySet()) {
                String key = String.valueOf(keyObj);
                csvSchemaBuilder.addColumn(key);
            }
            CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();

            CsvMapper csvMapper = new CsvMapper();
            //        csvMapper.enable(CsvParser.Feature.INSERT_NULLS_FOR_MISSING_COLUMNS)
            csv = csvMapper.writer(csvSchema).writeValueAsString(flatMap);
        } catch (Exception e) {
            throw new RuntimeException("There was an unexpected error when generating the CSV.", e);
        }
        return csv;
    }

    private class MapDeserializer implements JsonDeserializer<Map<String, Object>> {

        public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Map<String, Object> m = new LinkedHashMap<>();
            JsonObject jo = json.getAsJsonObject();
            for (Map.Entry<String, JsonElement> mx : jo.entrySet()) {
                String key = mx.getKey();
                JsonElement v = mx.getValue();
                if (v.isJsonArray()) {
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
