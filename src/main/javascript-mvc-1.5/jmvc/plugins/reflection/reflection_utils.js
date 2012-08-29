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
 * Created by
 * User: khokhria
 * Date: 18.01.12
 */
MVC.ReflectionUtils = (function ($) {
    return {
        /**
         * Creates a new Class instance. New instance extends MVC.Model
         *
         * @param className a class name
         * @param attributes must contain 'domain' and 'meta' values
         * @param properties prototype properties
         */
        createModel:function (className, attributes, properties) {

            var clazz = MVC.Model./*JsonP.*/extend(className, {
                domain:attributes.domain,
                attributes:{},
                default_attributes:{},
                init:function () {
                    var me = this;
                    $.each(attributes.meta.fields, function (i, field) {
                        //TODO type based factory
                        if (field.type == 'choice') {
                            me.add_attribute(field.id, 'boolean');
                            $.each(field.fields, function (i, field) {
                                me.add_attribute(field.id, field.type);
                            })
                        } else {
                            me.add_attribute(field.id, field.type);
                        }
                    });
                    me._super();
                },
                update:function (instance/*void*/, params, callbacks) {
                    this.create(params, callbacks);
                }
            }, {

            });
            window[className] = clazz;
            return clazz;
        }
    }
})(jQuery);