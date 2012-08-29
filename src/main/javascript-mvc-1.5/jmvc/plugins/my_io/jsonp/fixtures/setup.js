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
 * @author Ingvord
 * @since 31.03.12
 */
include.plugins('io/ajax/fixtures', 'io/jsonp');
/**
 * Changes url for MVC.JsonP.
 * <p/>
 * Replaces <code>'http://<server>:<port>'</code> with <code>'../../test/fixtures'</code>, i.e.
 * <p/>
 * <code>http://localhost:8080/MyApp/backend/Some.json</code> -> <code>../../test/fixtures/MyApp/backend/Some.json.get</code>
 * <p/>
 * Fixture should contain a valid JavaScript code, where test data passed to MVC.JsonP._cbs.fixture function. Example:
 * <p/><code>
 *     MVC.JsonP._cbs.fixture({"text":"Hello World!!!"});
 * </code>
 *
 */
MVC.JsonP.prototype.send = (function (oldSend) {
    MVC.JsonP.prototype.send0 = oldSend;
    return function () {
        var method = this.options.method;
        var options = MVC.Ajax.setup_request(this.url, {method:method});
        this.url = options.url;
        this.send0();
    }
})(MVC.JsonP.prototype.send);

/**
 * Changes callback function name from random to fixed.
 * In fixtures use 'MVC.JsonP._cbs.fixture' to wrap test data.
 *
 */
MVC.JsonP.prototype.callback_and_random = (function (oldCallback_and_random) {
    MVC.JsonP.prototype.callback_and_random0 = oldCallback_and_random;
    return function (n) {
        return this.callback_and_random0("fixture");
    }
})(MVC.JsonP.prototype.callback_and_random);