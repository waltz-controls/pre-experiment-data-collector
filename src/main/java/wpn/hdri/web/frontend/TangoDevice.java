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

package wpn.hdri.web.frontend;


import fr.esrf.TangoDs.Util;
import org.apache.log4j.Logger;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;

public class TangoDevice implements Runnable {

    private static final Logger log = Logger.getLogger(TangoDevice.class);

    private final String tangoServerName;
    private final String tangoInstanceName;
    private final String[] tangoServerArguments;

    public TangoDevice(String tangoServerName, String tangoInstanceName, String[] tangoServerArguments) {
        this.tangoServerName = tangoServerName;
        this.tangoInstanceName = tangoInstanceName;
        this.tangoServerArguments = tangoServerArguments;
    }

    public void run() {
        try {
            String[] args = new String[tangoServerArguments.length + 1];
            args[0] = tangoInstanceName;
            for (int i = 1, tangoServerArgumentsLength = tangoServerArguments.length; i < tangoServerArgumentsLength; i++) {
                args[i] = tangoServerArguments[i - 1];
            }

            //Util tg = Util.init(args, tangoServerName);
            Util tg = Util.init(args, tangoServerName);
            tg.server_init();

            log.info("Ready to accept request");
            tg.server_run();
        } catch (OutOfMemoryError ex) {
            log.error("Can't allocate memory !!!!");
            log.error("Exiting");
            Thread.currentThread().interrupt();
        } catch (UserException ex) {
            log.error("Received a CORBA user exception", ex);
            log.error("Exiting");
            Thread.currentThread().interrupt();
        } catch (SystemException ex) {
            log.error("Received a CORBA system exception", ex);
            log.error("Exiting");
            Thread.currentThread().interrupt();
        } catch (Throwable throwable) {
            log.error("Failed to initialize Tango frontend.", throwable);
            log.error("Exiting.");
            Thread.currentThread().interrupt();
        }
    }
}