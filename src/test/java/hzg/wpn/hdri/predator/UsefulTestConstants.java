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

package hzg.wpn.hdri.predator;

import hzg.wpn.hdri.predator.data.User;
import hzg.wpn.hdri.predator.data.Users;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 28.03.12
 */
public class UsefulTestConstants {
    private UsefulTestConstants() {
    }

    public static final String TEST_DATA =
            "{\n" +
                    "test:{\n" +
                    "    string:'some value',\n" +
                    "    number:1234,\n" +
                    "    arr:[string1,string2]\n" +
                    "  }\n" +
                    "}";

    public static final String TEST_META_DATA_JSON =
            "{\n" +
                    "    \"forms\":[{\n" +
                    "        \"name\":\"Test form\",\n" +
                    "        \"id\":\"test\",\n" +
                    "        \"help\":\"Some text with hints\",\n" +
                    "        \"type\":\"fieldset\",\n" +
                    "        \"fields\":[\n" +
                    "                {\n" +
                    "                    \"name\":\"Test string\",\n" +
                    "                    \"id\":\"string\",\n" +
                    "                    \"description\":\"Field with string value\",\n" +
                    "                    \"type\":\"string\",\n" +
                    "                    \"validation\":\"required\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"name\":\"Test number\",\n" +
                    "                    \"id\":\"number\",\n" +
                    "                    \"description\":\"Field with number value\",\n" +
                    "                    \"type\":\"number\",\n" +
                    "                    \"validation\":\"\"\n" +
                    //TODO support arrays in JsonMetaSource
//            "                },\n" +
//            "                {\n" +
//            "                    \"name\":\"Test strings array\",\n" +
//            "                    \"id\":\"arr\",\n" +
//            "                    \"description\":\"Field with array of string value\",\n" +
//            "                    \"type\":\"file_multiply\",\n" +
//            "                    \"validation\":\"\"\n" +
                    "                }\n" +
                    "        ]\n" +
                    "    }]\n" +
                    "}";

    public static final User TEST_USER = Users.TEST_USER;
    public static final String TEST_BEAMTIME_ID = "test-beamtime";
    public static final ApplicationContext NULL_APP_CTX = ApplicationContext.NULL;
}
