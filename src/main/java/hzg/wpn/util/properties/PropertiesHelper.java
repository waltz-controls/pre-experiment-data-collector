package hzg.wpn.util.properties;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.01.14
 */
public class PropertiesHelper {
    private PropertiesHelper() {
    }

    public static Properties loadProperties(Path pathToProperties) throws IOException {
        try (Reader rdr = Files.newBufferedReader(pathToProperties, Charset.defaultCharset())) {
            Properties result = new Properties();
            result.load(rdr);
            return result;
        }
    }
}
