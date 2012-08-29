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

/**
 * Utility model used to store user data on client.
 */
Wrapped = MVC.Model.JsonP.extend('Wrapped',
    /* @Static */
    {
        domain:ApplicationContext.domain,
        create_url:ApplicationContext.domain + '/backend/wrapped/Wrapped.json',
        find_url:ApplicationContext.domain + '/backend/wrapped/Wrapped.json',
        attributes:{},
        default_attributes:{},
        update:function (instance/*void*/, params, callbacks) {
            this.create(params, callbacks);
        }
    },
    /* @Prototype */
    {
        init:function (metaData) {
            if (typeof metaData != "object") throw('MetaData is not an object.');
            var me = this.Class;
            metaData.forEachFormDo(function (i, form) {
                var metaClass = MVC.String.classize(form.id);
                me.belong_to(metaClass);
                MVC.ReflectionUtils.createModel(metaClass, {
                    domain:me.domain + "/backend/wrapped",
                    meta:form
                }, {
                });
                me.add_attribute(form.id, metaClass);
            });
            var attributes = metaData.extractAttributes();
            this.set_attributes(attributes);
        },
        save:function (onSuccess, onFailure) {
            this.Class.create(this.attributes(), {
                onSuccess:onSuccess,
                onFailure:onFailure
            });
        },
        update:function (params) {
            this.set_attributes(params);
        }
    }
);