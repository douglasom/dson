package douglasom.json2csv;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DsonTest {
    @Test
    void jsonToCsv() {
        String csv = new Dson().jsonToCsv("{\n" +
                "    \"name\": \"Douglas\",\n" +
                "    \"yearOfBirth\": 1983,\n" +
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

        assertEquals("contactInfo.email,contactInfo.github,hobbies,name,yearOfBirth\n" +
                "douglas.mendes@gmail.com,douglasom,\"motorcycling;videogames;music\",Douglas,1983\n", csv);
    }
}
