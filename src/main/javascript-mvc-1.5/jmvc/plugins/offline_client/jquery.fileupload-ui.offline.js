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

/* File Created: Mai 22, 2012 */
;
(function ($) {
    $.widget('blueimpUI.fileupload.offline', $.extend({}, $.blueimpUI.fileupload.prototype, {
        _onSend:function (e, data) {
            if (data.uploadToServer) {
                return $.blueimp.fileupload.prototype._onSend.call(this, e, data);//this._onSend0(e,data);
            }

            var me = this,
                uploadFolderPath = ApplicationContext.domain + "/upload",
                slot,
                pipe,
                options = this._getAJAXSettings(data),
                resolveThumbnail = function (file) {
                    switch (/*getExtension*/
                        file.Name.substr(file.Name.lastIndexOf('.'))) {
                        case '.pdf':
                            return './resources/images/thumbnails/pdf_ico.png';
                        case '.tif':
                            return './resources/images/thumbnails/TIF-Image-icon.png';
                        default:
                            return '';
                    }
                },
                copyFile = function (file, destination) {

                    FSO.CopyFile(filePath, destination, true);
                },
                /**
                 * Simulates communication with server
                 */
                    send = function (file, destination) {
                    var jXHR = me._getXHRPromise(false, options.context);
                    //copyFile should succeed at this point
                    $.extend(jXHR, {
                        readyState:4,
                        status:200,
                        statusText:'success'
                    });

                    //simulate server response
                    var result = [
                        {
                            "name":file.Name,
                            "size":file.Size,
                            "url":destination,
                            "thumbnail_url":resolveThumbnail(file),
                            "delete":null,
                            "deleteType":'DELETE'
                        }
                    ];
                    me._onDone(result, jXHR.statusText, jXHR, options);


                    return jXHR;
                };


            this._beforeSend(e, options);

            //copyFiles
            if (!FSO.FolderExists(uploadFolderPath)) {
                FSO.CreateFolder(uploadFolderPath);
            }
            //IE supports single file upload only
            var filePath = data.fileInput.val();

            var file = FSO.GetFile(filePath);
            var destination = uploadFolderPath + "/" + file.Name;

            copyFile(file, destination);

            return send(file, destination);
        }

        // Override other methods here.

    }));
})(jQuery);
