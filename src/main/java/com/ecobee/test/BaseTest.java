package com.ecobee.test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URL;
import java.util.List;

/**
 * Created by RolandC on 2017-09-09.
 * This class initiates our Appium driver, holds setup / teadown, and all common methods that can be used across page classes.
 */
public class BaseTest {

    private static AppiumDriver driver;
    private static  WebDriverWait driverWait;

    private static String deviceName = "MyAndroidDevice";
    private static String platformVersion = "5.1";
    private static String packageName = "com.android.chrome";
    private static String mainActivity = "com.google.android.apps.chrome.Main";

    private static final String CONTEXT_NATIVE_APP = "NATIVE_APP";
    private static final String CONTEXT_WEB = "CHROMIUM";

    public static boolean nativeContext;

    @BeforeClass
    public static void setup() throws Exception {

        try {
            DesiredCapabilities capabilities;

            capabilities = DesiredCapabilities.android();
            capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, BrowserType.CHROME);
            capabilities.setCapability(MobileCapabilityType.PLATFORM, Platform.ANDROID);
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, Platform.ANDROID);

            /*
            TODO : uncomment this line to execute on an emulator, and update the avd name.
            capabilities.setCapability("avd", "MyAndroidDevice");
             */
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
            capabilities.setCapability(MobileCapabilityType.VERSION, platformVersion);
            capabilities.setCapability("app-package", packageName);
            capabilities.setCapability("app-activity", mainActivity);


            URL url= new URL("http://127.0.0.1:4723/wd/hub");
            driver = new AndroidDriver<WebElement>(url, capabilities);

            nativeContext = getNativeContext();

        } catch (Exception ex) {
            throw new Exception(String.format("Exception caught within BaseTest.setup(). Message: %s", ex.getMessage()));
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        try {
            driver.quit();
        } catch (Exception ex) {
            throw new Exception(String.format("Depending on your device and timing, the driver might already have closed: %s", ex.getMessage()));
        }
    }

    /**
     * Method to launch a website.
     * @param websiteURL - link that we want to open in browser
     * @throws Exception - if unable to launch site
     */
    public static void launchWebSite(String websiteURL) throws Exception {
        Logger.logAction(String.format("Launching URL: %s", websiteURL));
        driver.get(websiteURL);
    }

    /**
     * Waits for an element to load on screen
     * @param elementID id of element that needs to load
     * @throws Exception
     */
    public static void waitForElementToLoadByID(String elementID) throws Exception {
        Logger.logAction(String.format("Waiting for Element with ID '%s' to load", elementID));
        driverWait = new WebDriverWait(driver, AutomationConstants.WAIT_TIME_3S);
        int retries = 1;

        //waiting for an element, retrying few times before we declare element not found.
        while (true) {
            try {
                driverWait.until(ExpectedConditions.presenceOfElementLocated(By.id(elementID)));
                Logger.logComment(String.format("Found element with ID: %s", elementID));
                return;
            } catch (Exception elementDidNotLoad) {
                Logger.logWarning(String.format("Element with ID '%s' did not load after %d secs, after %d re-try", elementID, AutomationConstants.WAIT_TIME_10S, retries));
            }
            retries++;

            // terminating loop if retries complete
            if (retries > AutomationConstants.RE_TRY_3) {
                throw new Exception(String.format("Could not locate Element with ID '%s' after %d re-tries", elementID, AutomationConstants.RE_TRY_3));
            }
        }
    }

    /**
     * Finds element on screen with id.
     * @param elementID - string value of id
     * @return - WebElement
     * @throws Exception if id not located on screen
     */
    public static WebElement findElementByID(String elementID) throws Exception {
        Logger.logAction(String.format("Finding Element with ID '%s'", elementID));
        WebElement element ;
        try {
            element = driver.findElement(By.id(elementID));
        } catch (Exception elementNotFound) {
            throw new Exception(String.format("Element with ID '%s' not found", elementID));
        }
        return element;
    }

    /**
     * Finds element on screen with id and clicks it
     * @param elementID element to be clicked.
     * @throws Exception if id not found
     */
    protected static void findElementAndClickByID(String elementID) throws Exception {
        WebElement element = findElementByID(elementID);
        element.click();
    }


    /**
     * Find an element using its classname and text
     * @param className - class name of element
     * @param elementText - text of element
     * @return - WebElement
     * @throws Exception if no elements found
     */
    protected static WebElement findElementByClassAndText(String className, String elementText) throws Exception {
        List<WebElement> elementList = driver.findElementsByClassName(className);

        for(WebElement element : elementList) {
            if (element.getText().contains(elementText)) {
                return element;
            }
        }
        throw new Exception(String.format("Element with classname '%s' and text '%s' not found", className, elementText));
    }

    /**
     * Find and click an element using classname and text
     * @param className  - class name of element
     * @param elementText - text of element
     * @throws Exception if no elements found
     */
    protected static void findAndClickElementByClassAndText(String className, String elementText) throws Exception {
        WebElement element = findElementByClassAndText(className, elementText);
        element.click();
    }

    /**
     * Find all elements with the same id
     * @param id - id of the elements
     * @return list of all elements found
     * @throws Exception
     */
    protected static List<WebElement> findElementsByID(String id) throws Exception {
        List<WebElement> elementList = driver.findElementsById(id);
        return elementList;
    }

    /**
     * Find all elements with same class name
     * @param className - class name of elements
     * @return - list of all elements found
     * @throws Exception
     */
    public static List<WebElement> findElementsByClassName(String className) throws Exception {
        List<WebElement> elementList = driver.findElementsByClassName(className);
        return elementList;
    }

    /**
     * Switch to native app. Use before interacting with native app
     * @throws Exception
     */
    public static void useNativeContext() throws Exception {
        if (driver.getContext().contains(CONTEXT_NATIVE_APP)) {
            Logger.logWarning("Already native context");
        } else {
            driver.context(CONTEXT_NATIVE_APP);
            getNativeContext();
        }
    }

    /**
     * Switch to web view. Use before interacting with web app
     * @throws Exception
     */
    public static void useWebContext() throws Exception {
        if (driver.getContext().contains(CONTEXT_WEB)) {
            Logger.logWarning("Already web context");
        } else {
            driver.context(CONTEXT_WEB);
            getNativeContext();
        }
    }

    /**
     * Get the native context flag.
     * @return true if native context, ele false
     * @throws Exception
     */
    public static boolean getNativeContext() throws Exception {
        return nativeContext = driver.getContext().contains(CONTEXT_NATIVE_APP);
    }

    /**
     * Gets package name of app
     * @return
     */
    public static String getPackageName()  {
        return packageName;
    }

    /**
     * Gets the package + id portion of resource ID
     * @return
     */
    public static String getResourceID() {
        return getPackageName() + ":id/";
    }


}
