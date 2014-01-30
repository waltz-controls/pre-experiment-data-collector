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

import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.hdri.predator.meta.MetaData;
import hzg.wpn.hdri.predator.meta.MetaDataHelpers;
import hzg.wpn.hdri.predator.meta.MetaField;
import hzg.wpn.hdri.predator.meta.MetaForm;
import hzg.wpn.hdri.predator.meta.core.MetaStructure;
import hzg.wpn.hdri.predator.meta.json.JsonMetaSource;
import hzg.wpn.hdri.predator.meta.json.JsonStringFactory;
import hzg.wpn.util.base64.Base64InputStream;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.slf4j.Logger;
import wpn.hdri.web.backend.BackendException;
import wpn.hdri.web.data.DataSet;
import wpn.hdri.web.data.User;
import wpn.hdri.web.storage.StorageException;

import java.util.ArrayList;
import java.util.List;

/**
 * Derivatives of this class encapsulates different submit logic, i.e. when request comes from offline or online client.
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 30.05.12
 */
public abstract class SubmitHelper {
    /**
     * Updates types of the fields calls registered hooks for each field value.
     *
     * @param json aggregated data from the client in json format
     * @param meta per user defined MetaData (read from MetaData.json each session)
     * @return DynaBean created from json
     * @throws Exception in case of any trouble
     */
    public static Iterable<MetaStructure<? extends MetaStructure<?>>.Wrapper<? extends MetaStructure<?>, ?>> getAllFieldsWithValues(
            final String json, final MetaData meta) throws Exception {
        JsonStringFactory factory = new JsonStringFactory(json);

        JsonMetaSource data = factory.createInstance();

        List<MetaStructure<? extends MetaStructure<?>>.Wrapper<? extends MetaStructure<?>, ?>>
                result = new ArrayList<MetaStructure<? extends MetaStructure<?>>.Wrapper<? extends MetaStructure<?>, ?>>();

        for (MetaForm frm : meta.getForms()) {
            String frmId = frm.getId();
            for (MetaField fld : frm.getAllFields()) {
                String fldId = fld.getId();
                JsonMetaSource jsonMetaSource = data.get(fldId, frmId);
                result.add(jsonMetaSource.createWrapper(jsonMetaSource.getValue(), MetaDataHelpers.getFieldTypeAdaptor(fld)));
            }
        }

        return result;
    }

    protected void processSubmit(User user, SubmitDataHandler.Parameters requestParameters, ApplicationContext applicationContext, Logger log) throws BackendException {
        String dataSetName = requestParameters.dataSetName;
        log.info("Received data-set-name=" + dataSetName);
        DataSet result = null;
        try {
            result = processSubmitInternal(user, requestParameters, dataSetName, applicationContext, log);
        } catch (BackendException e) {
            log.error("Fail to process submit request!!!", e);
            throw e;
        }

        try {
            save(user, result, applicationContext);
        } catch (BackendException e) {
            log.error("Fail to store data!!!", e);
            throw e;
        }
    }

    protected abstract DataSet processSubmitInternal(User user, SubmitDataHandler.Parameters requestParameters, String dataSetName, ApplicationContext applicationContext, Logger log)
            throws BackendException;


    private void save(User user, DataSet newDataSet, ApplicationContext applicationContext) throws BackendException {
        try {
            applicationContext.getStorage().save(newDataSet.getData(), applicationContext);
        } catch (StorageException e) {
            throw new BackendException("An attempt to store dataset failed.", e);
        }
    }

    protected DynaBean getDataSetValues(String encoded, MetaData meta)
            throws BackendException {
        String json = new String(Base64InputStream.decode(encoded)).trim();

        DynaBean values = getValues(json, meta);

        return values;
    }

    private DynaBean getValues(String json, MetaData meta) throws BackendException {
        Iterable<MetaStructure<? extends MetaStructure<?>>.Wrapper<? extends MetaStructure<?>, ?>> values = null;
        try {
            values = getAllFieldsWithValues(json, meta);
        } catch (Exception e) {
            throw new BackendException("Unable to get values from json.", e);
        }

        //TODO validate data
        DynaBean wrapped = new LazyDynaBean();
        for (MetaStructure.Wrapper value : values) {
            //TODO provide saving strategy for different fields, i.e. readonly, file etc
            if (!meta.getFieldById(value.getId()).isReadonly()) {
                wrapped.set(value.getId(), value.getValue());
            }
        }

        return wrapped;
    }
}
