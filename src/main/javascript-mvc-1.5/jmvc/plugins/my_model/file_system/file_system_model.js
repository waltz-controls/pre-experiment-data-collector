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
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 18.05.12
 */
MVC.Model.FileSystem = MVC.Model.extend(
    /* @Static*/
    {
        /**
         * FileSystemObject
         */
        fso:null,
        /**
         * Where data will be looked in the file system (.data by default)
         */
        path:"./.data",
        /**
         * Goes through the list of static functions.  If they end with _success, creates
         * a requesting function for them.
         */
        init:function () {
            if (!this.className) return;
            if (!this.path) throw "Model.FileSystem should define path.";
            this.fso = new ActiveXObject("Scripting.FileSystemObject");
            if (!this.fso.FolderExists(this.path)) {
                this.fso.CreateFolder(this.path)
            }
            this._super();
        },
        /**
         *
         * @param {Object} options
         * @param {Object} cbs
         */
        find_all:function (options, cbs) {
            var result = [];

            //fso alias
            var fso = this.fso;

            var folder = fso.GetFolder(this.path);
            var files = new Enumerator(folder.Files);

            for (; !files.atEnd(); files.moveNext()) {
                var file = files.item();
                result.push({
                    value:file.Name
                });
            }
            result = this.create_many_as_existing(result);
            cbs.onSuccess(result);
        },
        create:function (options, cbs) {
            var result = this.create_as_existing(options);
            if (!result) {
                cbs.onFailure(null);
            } else {
                //fso alias
                var fso = this.fso;
                var file = fso.CreateTextFile(this.path + "/" + result.value, true);
                try {
                    file.WriteLine(JSON.stringify(result.attributes()));
                } finally {
                    file.Close();
                }
                cbs.onSuccess(result);
            }
        },
        /**
         * overwrite this function if you don't want to eval js
         * @param {Object} json_string json string
         * @return {Object} json converted to data
         */
        json_from_string:function (json_string) {
            if (JSON) {
                return JSON.parse(json_string);
            } else {
                return eval('(' + json_string + ')'); //
            }
        }

    },
//prototype methods
    {}
);

if (!MVC._no_conflict && typeof Model.FileSystem == 'undefined') {
    Model.FileSystem = MVC.Model.FileSystem;
}