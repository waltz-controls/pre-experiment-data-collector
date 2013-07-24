/*
 * The main contributor to this project is Institute of Materials Research,
 * Helmholtz-Zentrum Geesthacht,
 * Germany.
 *
 * This project is a contribution of the Helmholtz Association Centres and
 * Technische Universitaet Muenchen to the ESS Design Update Phase.
 *
 * The project's funding reference is FKZ05E11CG1.
 *
 * Copyright (c) 2012. Institute of Materials Research,
 * Helmholtz-Zentrum Geesthacht,
 * Germany.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package wpn.hdri.web.backend.submit;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import wpn.hdri.util.base64.Base64InputStream;
import wpn.hdri.web.ApplicationContext;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.data.DataSet;
import wpn.hdri.web.data.DataSets;
import wpn.hdri.web.data.User;
import wpn.hdri.web.meta.*;
import wpn.hdri.web.meta.json.JsonMetaSource;
import wpn.hdri.web.meta.json.JsonStringFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.05.12
 */
public class OfflineSubmitHelper extends SubmitHelper {
    @Override
    protected DataSet processSubmitInternal(User user, SubmitDataHandler.Parameters requestParameters, String dataSetName, ApplicationContext applicationContext, Logger log) throws BackendException {
        MetaData meta = getMetaData(requestParameters.meta, applicationContext);
        log.info("Using user submitted meta data:");
        //TODO add formatter for meta data to print it in different forms, aka toJson, toHtml etc
        for (MetaForm frm : meta.getForms()) {
            log.info(frm.getId() + "{");
            for (MetaField fld : frm.getAllFields()) {
                log.info(fld.getId());
            }
            log.info("}");
        }
        try {
            DynaBean values = getDataSetValues(requestParameters.data, meta);
            log.info("Writing new values to data set:");
            try {
                for (Map.Entry entry : (Set<Map.Entry>) PropertyUtils.describe(values).entrySet()) {
                    log.info(String.valueOf(entry.getKey()) + "=" + String.valueOf(entry.getValue()));
                }
            } catch (IllegalAccessException e) {
                log.error("Ag-rh", e);
            } catch (InvocationTargetException e) {
                log.error("Ouch", e);
            } catch (NoSuchMethodException e) {
                log.error("Oops", e);
            }
            DataSet dataSet = DataSets.createDataSet(user, meta, applicationContext.getBeamtimeId(), dataSetName, values);
            return dataSet;
        } catch (Exception e) {
            throw new BackendException("Can not create DataSet for user:" + user.getName(), e);
        }
    }

    /**
     * If meta parameter is passed - creates new {@link wpn.hdri.web.meta.MetaData} from the passed
     *
     * @param encoded
     * @param applicationContext
     * @return
     * @throws wpn.hdri.web.backend.BackendException
     *
     */
    private MetaData getMetaData(String encoded, ApplicationContext applicationContext) throws BackendException {
        if (encoded == null) {
            throw new IllegalArgumentException("User has not submitted meta data.");
        }
        String json = new String(Base64InputStream.decode(encoded)).trim();

        try {
            MetaData metaFromRequest = new MetaDataHelpers(new MetaDataFactory<JsonMetaSource>(new JsonStringFactory(json))).createMetaData();

            MetaData metaOnServer = applicationContext.getMetaDataHelper().getMetaData();

            if (MetaDataHelpers.compare(metaFromRequest, metaOnServer) != 0) {
                throw new IllegalArgumentException("Meta from request differs from meta on server.");
            }

            return metaFromRequest;
        } catch (Exception e) {
            throw new BackendException("Can not create meta data from json:" + json, e);
        }
    }
}
