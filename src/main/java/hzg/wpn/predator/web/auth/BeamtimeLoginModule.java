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
package hzg.wpn.predator.web.auth;

import com.sun.security.auth.module.Krb5LoginModule;
import org.apache.catalina.realm.GenericPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.util.Map;


/**
 * This class simply wraps Krb5LoginModule. The only change is that it adds a dummy role principal during the commit phase.
 * Implementation is based on {@link GenericPrincipal} what may cause a trouble with different versions of the TomCat
 * <p/>
 * Current implementation depends on the catalina-7.0.23
 * <p/>
 *
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 15.02.12
 */
public class BeamtimeLoginModule implements LoginModule {
    private final static Logger logger = LoggerFactory.getLogger(BeamtimeLoginModule.class);

    private Krb5LoginModule krbmod;
    private Subject subject = null;

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        logger.trace("initialize...");
        this.subject = subject;

        krbmod = new Krb5LoginModule();
        krbmod.initialize(subject, callbackHandler, sharedState, options);
        logger.trace("initialize... Done.");
    }

    public boolean login() throws LoginException {
        logger.trace("login...");
        try {
            return krbmod.login();
        } finally {
            logger.trace("login... Done.");
        }
    }


    public boolean commit() throws LoginException {
        logger.trace("commit...");
        boolean answer = krbmod.commit();

        if (answer && subject != null) {
            GenericPrincipal role = new GenericPrincipal("user", null);

            subject.getPrincipals().add(role);
        }
        try {
            return answer;
        } finally {
            logger.trace("commit... Done.");
        }
    }

    public boolean abort() throws LoginException {
        logger.trace("abort...");
        try {
            return krbmod.abort();
        } finally {
            logger.trace("abort... Done.");
        }
    }

    public boolean logout() throws LoginException {
        logger.trace("logout...");
        try {
            return krbmod.logout();
        } finally {
            logger.trace("logout... Done.");
        }
    }
}
