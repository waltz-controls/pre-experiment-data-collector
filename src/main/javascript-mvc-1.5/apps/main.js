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

include.css('my.custom', 'validationEngine.jquery');
include.resources(
    'jsonp.utility.functions', 'jquery.validationEngine', 'languages/jquery.validationEngine-en', 'webtoolkit.base64'
);
include.engines(
    'FileUpload', 'Wizard'
);
include.plugins(
    'controller', 'controller/scaffold',
    'WEF/patches/controller',
    'view', 'view/helpers',
    'dom/element',
    'io/ajax',
    'model/jsonp', 'model/validations',
    'WEF/patches/model/jsonp',
    'my_view_helpers',
    'prototypes', 'reflection'
);

include(function () { //runs after prior includes are loaded
    include.models('main/MetaDataInfo', 'main/Wrapped', 'main/DataSetName', 'main/DataSet');
    include.controllers('main/main', 'main/Wrapped', 'main/DataSets', 'main/DataSetNames');
    include.views(
        'views/main/meta.form.fieldset', 'views/main/meta.form.upload',
        'views/main/meta.form.multichoice', 'views/main/meta.field',
        'views/DataSetNames/init',
        'views/main/tomo.scan',
        'views/main/wizard.form.submit'
    );
});