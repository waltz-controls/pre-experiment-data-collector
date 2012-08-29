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

DataSetNamesController = MVC.Controller.extend('DataSetNames',
    /* @Static */
    {
        /**
         * handles 'forward' button click
         */
        onForward:function () {
            $('input:checked').each(function (ndx) {
                var create = $(this).attr('id') == 'new';

                var dataSetName = (create) ? $('#newScanName').val() : $(this).attr('id');

                if (create) {
                    DataSet.create({
                        "data-set-name":dataSetName
                    }, {
                        onSuccess:function (data) {
                            DataSet.publish("created", {data:data});
                        }
                    });
                } else {
                    DataSet.find_all({
                        "data-set-name":dataSetName
                    }, {
                        onSuccess:function (data) {
                            DataSet.publish("found", {data:data, dataSetName:dataSetName});
                        }
                    });
                }
            });
            MainController.toggleLoading();
        }
    },
    /* @Prototype */
    {
        /**
         * Responses to {@link DataSetName#find_all} event.
         *
         * @param params
         */
        "DataSetName.find_all subscribe":function (params) {
            this.data = params.data;
            this.render({action:'init', to:document.getElementById('Wizard')});
        }
    }
);