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
 *
 */
MetaData = MetaDataInfo = MVC.Model.JsonP.extend('MetaDataInfo',
    /* @Static */
    {
        domain:ApplicationContext.domain + "/backend",
        view_url:'views/main/meta.data.ejs'
    },
    /* @Prototype */
    {
        setReadonly:function(){
            this.forEachFieldDoIf(function(ii,form,field){
                field.readonly = true;
            },function(){return true;})
        },
        forEachFormDo:function (what) {
            var meta = this;
            $.each(meta.forms, function (i, form) {
                what(i, form);
            });
        },
        /**
         *
         * @param {Function(args)} what args:index, MetaForm, MetaField
         * @param {Function(arg)} predicate arg: MetaForm
         */
        forEachFieldDoIf:function (what, predicate) {
            var meta = this;
            $.each(meta.forms, function (i, form) {
                if (predicate(form)) {
                    $.each(form.fields, function (ii, field) {
                        what(ii, form, field);
                        //TODO avoid this choice special treatment
                        if(field.fields){
                            $.each(field.fields,function(iii,field){
                                what(iii, form, field);
                            })
                        }
                    });
                }
            });
        },
        extractAttributes:function () {
            var attributes = {};
            this.forEachFieldDoIf(
                function (ii, form, field) {
                    attributes[form.id][field.id] = field.defaultValue;
                }, function (form) {
                    attributes[form.id] = {};
                    return true;
                });
            return attributes;
        },
        toView:function () {
            return new View({url:this.Class.view_url}).render(this);
        },
        getFormsByType:function (type) {
            var result = [];
            $.each(this.forms, function (i, frm) {
                if (frm.type == type) {
                    result.push(frm);
                }
            });
            return result;
        }
    }
);