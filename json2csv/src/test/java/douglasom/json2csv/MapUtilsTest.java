package douglasom.json2csv;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

class MapUtilsTest {
    @Test
    void concatenate() {
        assertEquals("a.b", MapUtils.concatenate("a","b", "."));
    }

    @Test
    void flatten() {
        Map<String, Object> deepMap = new HashMap<>();
        deepMap.put("name", "Douglas");
        deepMap.put("religion", null);
        List<String> hobbies = Arrays.asList("motorcycling", "videogames", "music");
        deepMap.put("hobbies", hobbies);
        Map<String, Object> contactInfo = new HashMap<>();
        contactInfo.put("email", "douglas.mendes@gmail.com");
        contactInfo.put("github", "douglasom");
        deepMap.put("contactInfo", contactInfo);

        Map flatMap = MapUtils.flatten(deepMap);
        assertEquals(5, flatMap.size());
        assertEquals("Douglas", flatMap.get("name"));
        assertNull(flatMap.get("religion"));
        assertTrue(flatMap.containsKey("religion"));
        assertEquals(hobbies, flatMap.get("hobbies"));
        assertTrue(flatMap.containsKey("contactInfo.email"));
        assertEquals("douglas.mendes@gmail.com", flatMap.get("contactInfo.email"));
        assertTrue(flatMap.containsKey("contactInfo.github"));
        assertEquals("douglasom", flatMap.get("contactInfo.github"));
    }
}
