package wpn.hdri.web.meta;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 29.01.14
 */
public class YamlParser {
    private final Yaml yaml = new Yaml();

    public Object parse(Path pathToYaml) throws IOException {
        Object result = yaml.load(Files.newBufferedReader(pathToYaml, Charset.defaultCharset()));
        return result;
    }
}
