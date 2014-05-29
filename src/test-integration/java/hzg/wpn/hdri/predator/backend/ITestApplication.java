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

package hzg.wpn.hdri.predator.backend;

import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import hzg.wpn.hdri.predator.ApplicationContext;
import hzg.wpn.hdri.predator.data.User;
import hzg.wpn.hdri.predator.storage.SimpleSerializationStorage;
import hzg.wpn.hdri.predator.storage.Storage;
import hzg.wpn.util.reflection.ReflectionUtils;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.beanutils.DynaBean;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @author Igor Khokhriakov <igor.khokhriakov@hzg.de>
 * @since 05.04.12
 */
public class ITestApplication {
    private final static String WEBAPP_DIR_LOCATION = "target/PreExperimentDataCollector/";
    private final static String REAL_PATH = new File(WEBAPP_DIR_LOCATION).getAbsolutePath();
    private final static int PORT = 28080;
    private final static String CONTEXT = "/PreExperimentDataCollector";

    private final static String WEBAPP_URL = "http://localhost:" + PORT + CONTEXT;

    private static final String USERNAME = "Test";
    private static final String ROLE = "user";
    private static final String PASS = "test";

    private static Tomcat TOMCAT;

    private static User TEST_USER;
    private ApplicationContext ctx;

    private WebDriver webDriver;

    @BeforeClass
    public static void beforeClass() throws Exception {
        TOMCAT = new Tomcat();

        TOMCAT.setPort(PORT);

        //TODO override application.properties, login.properties etc
        TOMCAT.addWebapp(CONTEXT, REAL_PATH);

        TOMCAT.addUser(USERNAME, PASS);
        TOMCAT.addRole(USERNAME, ROLE);

        TOMCAT.start();
    }

    @Before
    public void before() throws Exception {
        //TODO test on different browser versions
        webDriver = createHtmlUnitDriver();

//        webDriver = createFireFoxDriver();

//        webDriver.get(WEBAPP_URL);
        webDriver.get("http://" + USERNAME + ":" + PASS + "@localhost:" + PORT + CONTEXT);

        ctx = new ApplicationContext(REAL_PATH + "/", CONTEXT, "test-beamtime", null, null, null, null);
    }

    private WebDriver createFireFoxDriver() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("network.http.phishy-userpass-length", 255);
        profile.setPreference("network.automatic-ntlm-auth.trusted-uris", "localhost");

        return new FirefoxDriver(profile);
    }

    private WebDriver createHtmlUnitDriver() throws Exception {
        WebDriver webDriver = new HtmlUnitDriver(true);

        //BASIC authentication workaround
        Field webClientField = ReflectionUtils.getDeclaredField("webClient", HtmlUnitDriver.class);

        try {
            webClientField.setAccessible(true);
            WebClient webClient = (WebClient) webClientField.get(webDriver);

            DefaultCredentialsProvider credentialsProvider = new DefaultCredentialsProvider();
            credentialsProvider.addCredentials(USERNAME, PASS);

            webClient.setCredentialsProvider(credentialsProvider);

            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        } finally {
            webClientField.setAccessible(false);
        }

        return webDriver;
    }

    @Test
    public void testCreateDataSet() throws Exception {

        //click new
        webDriver.findElement(By.id("new")).click();
        //set new data set name
        webDriver.findElement(By.id("newScanName")).clear();
        webDriver.findElement(By.id("newScanName")).sendKeys("test-data-set-1");
        //click forward
        webDriver.findElement(By.name("forward")).click();

        //set test fields
        webDriver.findElement(By.id("string")).sendKeys("Test string value");
        webDriver.findElement(By.id("number")).sendKeys("12345");
        webDriver.findElement(By.id("text")).sendKeys("Test text value");

        //move next step
        webDriver.findElement(By.name("forward")).click();

        //TODO upload

        //move next step
        webDriver.findElement(By.name("forward")).click();

        webDriver.findElement(By.xpath("//h3[@choice-id='choice-2']")).click();

        //TODO check fields visible

        //set test field values
        webDriver.findElement(By.id("field-4")).sendKeys("1234");
        webDriver.findElement(By.id("field-5")).sendKeys("98765");

        //move next step
        webDriver.findElement(By.name("forward")).click();


        //submit
//        webDriver.findElement(By.id("frmDataSubmit")).submit();
        webDriver.findElement(By.name("submit")).click();


        Storage storage = new SimpleSerializationStorage();


        DynaBean result = storage.load("test-data-set-1", null);
    }

    @After
    public void after() throws Exception {
        webDriver.close();
        webDriver = null;
    }

    @AfterClass
    public static void afterClass() throws Exception {
        TOMCAT.stop();
        TOMCAT = null;
    }
}
