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
 * @since 10.04.12
 */
TestMain = new Test.Functional("Main", {
    test_create:function () {
        var $new = $('#new'),
            $newDataSetName = $('#newScanName'),
            $forward = $('button[name="forward"]');


        $new.click();
        $newDataSetName.val("test-data-set-1");

        $forward.click();

        //refresh dom
        $(document).on();

        //assert that MetaData downloaded successfully
        //First form's name should be visible
        var $label = $('div.title-wrap > strong.label:visible');
        this.assert($label.size() == 1);
        this.assert_equal("Fieldset form example:", $label.text(), "Cannot find form.");
    }
});