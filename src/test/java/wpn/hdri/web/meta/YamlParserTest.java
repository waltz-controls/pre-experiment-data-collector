package wpn.hdri.web.meta;

import com.google.gson.Gson;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 29.01.14
 */
public class YamlParserTest {
    @Test
    public void testParse() throws Exception {
        YamlParser instance = new YamlParser();
        Object parsed = instance.parse(Paths.get("D:\\MyProjects\\hzg.wpn.projects\\Predator\\src\\main\\resources\\Meta.yaml"));
        Gson gson = new Gson();
        String json = gson.toJson(parsed);
        Files.write(Paths.get("D:\\MyProjects\\hzg.wpn.projects\\Predator\\src\\main\\resources\\Meta.json"), json.getBytes());
    }

    @Test
    public void testParse_Failed() throws Exception {
        //TODO
    }
}
