package douglasom.json2csv;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DsonTest {
    @Test
    void jsonToCsv() {
        String csv = Dson.jsonToCsv("{\n" +
                "    \"name\": \"Douglas\",\n" +
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

        assertEquals("contactInfo.email,contactInfo.github,hobbies,name\n" +
                "douglas.mendes@gmail.com,douglasom,\"motorcycling;videogames;music\",Douglas\n", csv);
    }
}
