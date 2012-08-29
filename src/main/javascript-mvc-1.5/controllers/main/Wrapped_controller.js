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

WrappedController = MVC.Controller.extend('Wrapped',
    /* @Static */
    {},
    /* @Prototype */
    {
        "Wrapped.create.as_existing subscribe":function (params) {
            this.data = params.data;
            this.render({to:'Wrapped', action:'create'});
        },
        "Wrapped.update_all_ui subscribe":function (params) {
            var data = params.values,
                meta = params.meta;

            $('input,textarea').each(function () {
                var id = $(this).attr('id');
                if (data.hasOwnProperty(id)) {
                    $(this).val(data[id]);
                }
            });

            //TODO avoid this upload form type special treatment
            $.each(meta.getFormsByType('upload'), function (i, frm) {
                $.each(frm.fields, function (ii, fld) {
                    var newData = data[fld.id] ? data[fld.id].split(';') : [];
                    Controller.publish('FileUpload.add_file_names', {data:newData, id:frm.id});
                });
            });
        },
        "Wrapped.update subscribe":function (params) {
            var currentForm = params.element;
            var frmId = currentForm.attr('id');
            var data = DataSet.store.find_one(ApplicationContext.crtDataSetId).data;
            //TODO avoid this upload form type special treatment
            if (currentForm.attr('type') == 'upload') {
                for (var fldId in data[frmId].attributes()) {
                    var input = $('<input type="hidden">').attr({name:fldId});
                    var value = [];
                    $('tr.file-row > td.name', currentForm).each(function (ndx) {
                        value.push($(this).text());
                    });

                    input
                        .val(value.join(';'))
                        .appendTo(currentForm);
                }
            }

            var values = {};

            values[frmId] = (function () {
                var fields = {};
                $.each(currentForm.serializeArray(), function (i, field) {
                    fields[field.name] = field.value;
                });
                return fields;
            })();

            data.update(values);
        }
    }
);