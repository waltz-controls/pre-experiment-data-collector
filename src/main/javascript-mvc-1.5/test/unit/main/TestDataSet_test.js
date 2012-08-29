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

new MVC.Test.Unit('TestDataSet', {
    test_create:function () {

        var me = this;
        DataSet.create({"data-set-name":"test-dataset-1"}, {
            onSuccess:function (data) {
                data = data;
            },
            onFailure:function () {
                me.assert(false);
            }
        });
    },
    test_findAll:function () {

        var me = this;
        DataSet.find_all({"data-set-name":"test-scan-1"}, {
            onSuccess:function (data) {
                DataSet.publish("found", {data:data, dataSetName:"test-scan-1"});
            }
        });
    },
    test_construct:function(){
        var dataSet = new DataSet(
            {
                id:"test-data-set",
                data:{
                    frmTest:{
                        fldTest:"test value for test field"
                    }
                },
                meta:{
                    forms:[{
                        id:"frmTest",
                        fields:[{
                            id:"fldTest"
                            }
                        ]
                    }]
                }
            }
        );

        this.assert_equal("test value for test field",dataSet.data.frmTest.fldTest);
    }
});