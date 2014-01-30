package hzg.wpn.hdri.predator.frontend;

import org.junit.Test;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 27.08.13
 */
public class TangoDeviceTest {
    @Test
    public void testRun() throws Exception {
//        TangoDevice.setContext(UsefulTestConstants.NULL_APP_CTX);
        TangoDevice instance = new TangoDevice(/*"development", new String[0]*/);

        instance.run();

        //TODO
    }
}
