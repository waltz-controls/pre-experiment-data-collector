package hzg.wpn.hdri.predator.data;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import hzg.wpn.hdri.predator.storage.Storage;
import hzg.wpn.util.beanutils.BeanUtilsHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.01.14
 */
public class DataSetsManager {
    private static final Logger LOG = LoggerFactory.getLogger(DataSetsManager.class);

    private final Path pathToHome;
    private final Storage storage;

    public DataSetsManager(Path pathToHome, Storage storage) {
        this.pathToHome = pathToHome;
        this.storage = storage;
    }

    public Iterable<String> getUserDataSetNames(String user) {
        Path pathToHomeUser = pathToHome.resolve(user);

        //TODO cache

        try {
            DirectoryStream<Path> ds = Files.newDirectoryStream(pathToHomeUser, new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(Path entry) throws IOException {
                    return Files.isRegularFile(entry);
                }
            });

            return Iterables.transform(ds,new Function<Path,String>(){
                @Override
                public String apply(@Nullable Path input) {
                    return input.getFileName().toString();
                }
            });
        } catch (IOException e) {
            LOG.error("Can not load user dir[" + e.getMessage() + "]");
            return Collections.emptyList();
        }
    }


    public Iterable<DynaBean> getUserDataSets(String user) {
        final Path pathToHomeUser = pathToHome.resolve(user);

        Iterable<String> names = getUserDataSetNames(user);

        //TODO cache

        return Iterables.transform(names, new Function<String, DynaBean>() {
            @Override
            public DynaBean apply(@Nullable String input) {
                try {
                    return storage.load(input,pathToHomeUser);
                } catch (IOException e) {
                    LOG.error("Can not load data set[" + input + "]", e);
                    return null;
                }
            }
        });
    }

    public DynaBean getUserDataSet(final String user, final String dataSetName) {
        return Iterables.find(getUserDataSets(user), new Predicate<DynaBean>() {
            @Override
            public boolean apply(@Nullable DynaBean input) {
                return BeanUtilsHelper.getProperty(input, "name", String.class).equalsIgnoreCase(dataSetName);
            }
        });
    }
}
