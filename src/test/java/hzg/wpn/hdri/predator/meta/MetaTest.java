package hzg.wpn.hdri.predator.meta;

import org.apache.commons.beanutils.DynaClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 29.01.14
 */
public class MetaTest {
    private final Path pathToYaml = Paths.get("D:\\MyProjects\\hzg.wpn.projects\\Predator\\target\\classes\\Meta.yaml");

    @Test
    public void testExtractDynaClass() throws Exception {
        Meta instance = new Meta(pathToYaml);
        DynaClass result = instance.extractDynaClass();
        //TODO assert
    }

    @Test
    public void testWriteAsJson() throws Exception {
        Meta instance = new Meta(pathToYaml);
        try (BufferedWriter out =
                     Files.newBufferedWriter(pathToYaml.getParent().resolve("Meta.json"), Charset.defaultCharset())) {
            instance.writeAsJson(out);
        }
        //TODO assert
    }
}
