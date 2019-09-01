package douglasom.json2csv;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DsonTest {
    @Test
    void jsonToMap() {
        Map map = new Dson().jsonToMap("{\n" +
                "\"name\": \"Douglas\",\n" +
                "\"religion\": null\n" +
                "}");

        assertTrue(map.containsKey("name"));
        assertEquals("Douglas", map.get("name"));
        assertTrue(map.containsKey("religion"));
        assertNull(map.get("religion"));
    }

    @Test
    void flatMapToCsv() throws JsonProcessingException {
        Map<String, Object> flatMap = new HashMap<>();
        flatMap.put("name", "Douglas");
        flatMap.put("religion", null);

        String csv = new Dson().flatMapToJson(flatMap);

        assertEquals("name,religion\n" +
                "Douglas,\n", csv);
    }

    @Test
    void jsonToCsv() {
        String csv = new Dson().jsonToCsv("{\n" +
                "    \"name\": \"Douglas\",\n" +
                "    \"yearOfBirth\": 1983,\n" +
                "    \"religion\": null,\n" +
                "    \"hobbies\": [\n" +
                "        \"motorcycling\",\n" +
                "        \"videogames\",\n" +
                "        \"music\"\n" +
                "    ],\n" +
                "    \"contactInfo\": {\n" +
                "        \"email\": \"douglas.mendes@gmail.com\",\n" +
                "        \"github\": \"douglasom\"\n" +
                "    }\n" +
                "}");

        assertEquals("contactInfo.email,contactInfo.github,hobbies,name,religion,yearOfBirth\n" +
                "douglas.mendes@gmail.com,douglasom,\"motorcycling;videogames;music\",Douglas,,1983\n", csv);
    }

    @Test
    void jsonToCsvReadNullsFromJsonFalse() {
        String csv = new Dson().setReadNullsFromJson(false).jsonToCsv("{\n" +
                "    \"name\": \"Douglas\",\n" +
                "    \"yearOfBirth\": 1983,\n" +
                "    \"religion\": null\n" +
                "}");

        assertEquals("name,yearOfBirth\n" +
                "Douglas,1983\n", csv);
    }
}
