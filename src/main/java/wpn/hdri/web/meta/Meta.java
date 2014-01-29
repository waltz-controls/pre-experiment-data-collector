package wpn.hdri.web.meta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaClass;

import javax.annotation.concurrent.ThreadSafe;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Wraps yaml description
 * <p/>
 * Provides methods to convert yaml description to DynaClass and to write description in json
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 29.01.14
 */
@ThreadSafe
public class Meta {
    private final static Map<String, Class<?>> TYPE_TO_CLASS = new HashMap<>();

    static {
        TYPE_TO_CLASS.put("string", String.class);
        TYPE_TO_CLASS.put("number", int.class);
        TYPE_TO_CLASS.put("double", float.class);
        TYPE_TO_CLASS.put("choice", boolean.class);
    }

    public static final String FLD_ID = "id";
    public static final String FLD_TYPE = "type";
    public static final String FRM_FIELDS = "fields";

    private final Path pathToYaml;
    private final YamlParser parser = new YamlParser();
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();
    private final AtomicReference<Object> yaml =
            new AtomicReference<>(null);
    private final AtomicReference<DynaClass> clazz =
            new AtomicReference<>(null);

    public Meta(Path pathToYaml) {
        this.pathToYaml = pathToYaml;
    }

    public void load() throws IOException {
        //TODO validate yaml
        yaml.set(parser.parse(pathToYaml));
    }

    /**
     * This method extracts DynaClass instance from yaml
     * <p/>
     * Resulting DynaClass represents flat structure that accumulates all fields from yaml description
     *
     * @return a DynaClass extracted from yaml
     * @throws FileNotFoundException
     */
    //TODO cache result
    public DynaClass extractDynaClass() {
        if (yaml.get() == null) throw new IllegalStateException("Yaml was not loaded yet.");

        LazyDynaClass result = new LazyDynaClass("MetaData", null, new DynaProperty[]{
                new DynaProperty("user", String.class),
                new DynaProperty("beamtimeId", String.class),
                new DynaProperty("id", String.class)
        });


        //assume that yaml has been properly validated and loaded
        for (Map<String, Object> frm : (List<Map<String, Object>>) yaml.get()) {
            extractFields(result, frm);
        }

        //finalize class
        result.setRestricted(true);
        return result;
    }

    private void extractFields(LazyDynaClass result, Map<String, Object> entity) {
        for (Map<String, Object> fld : (List<Map<String, Object>>) entity.get(FRM_FIELDS)) {
            String type = String.valueOf(fld.get(FLD_TYPE));
            result.add(String.valueOf(fld.get(FLD_ID)),
                    TYPE_TO_CLASS.get(type));
            if ("choice".equals(type)) {
                extractFields(result, fld);
            }
        }
    }

    public void writeAsJson(Writer out) throws IOException {
        try {
            gson.toJson(yaml, out);
        } catch (JsonIOException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
