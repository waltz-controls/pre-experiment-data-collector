package hzg.wpn.hdri.predator.data;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import hzg.wpn.hdri.predator.storage.Storage;
import hzg.wpn.util.beanutils.BeanUtilsHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.01.14
 */
public class DataSetsManager {
    private static final Logger LOG = LoggerFactory.getLogger(DataSetsManager.class);

    private final String beamtimeId;
    private final Path pathToHome;
    private final DynaClass dataSetClass;
    private final Storage storage;

    public DataSetsManager(String beamtimeId, Path pathToHome, DynaClass dataSetClass, Storage storage) {
        this.beamtimeId = beamtimeId;
        this.pathToHome = pathToHome;
        this.dataSetClass = dataSetClass;
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

    /**
     *
     * @param user
     * @param dataSetName
     * @return a newly created instance or null
     */
    public DynaBean newDataSet(String user, String dataSetName) {
        try {
            DynaBean result = dataSetClass.newInstance();
            BeanUtils.setProperty(result,"user",user);
            BeanUtils.setProperty(result,"name",dataSetName);
            BeanUtils.setProperty(result,"beamtimeId",beamtimeId);

            return result;
        } catch (InvocationTargetException|IllegalAccessException|InstantiationException e) {
            return null;
        }
    }

    public void save(DynaBean data) throws IOException {
        String user = BeanUtilsHelper.getProperty(data,"user",String.class);

        final Path pathToHomeUser = pathToHome.resolve(user);
        storage.save(data,pathToHomeUser);
    }
}
