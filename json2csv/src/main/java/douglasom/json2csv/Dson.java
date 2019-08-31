package douglasom.json2csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.gson.Gson;

import java.util.Map;

public class Dson {
    public static String jsonToCsv(String json) {
        String csv;
        try {
            Map jsonObj = new Gson().fromJson(json, Map.class);
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
}
