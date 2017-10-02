package com.appium.example;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by RolandC on 2017-09-09.
 * This class initiates our Appium driver, holds setup / teadown, and all common methods that can be used across page classes.
 */
public class BaseTest {

    private static AppiumDriver driver;
    private static  WebDriverWait driverWait;

    private static DesiredCapabilities capabilities;

    private static String packageName;
    private static String mainActivity = "com.google.android.apps.chrome.Main";

    private static final String CONTEXT_NATIVE_APP = "NATIVE_APP";
    private static final String CONTEXT_CHROMIUM = "CHROMIUM";
    private static String CONTEXT_WEBVIEW;

    public static boolean nativeContext;

    private static JSONObject runConfigJson;
    private static final String RUN_CONFIG_FILENAME = "/runConfig.json";

    private static String PLATFORM_NAME;
    private static String APPIUM_PORT_NUMBER_DEFAULT;
    private static String APPIUM_SERVER_ADDRESS_DEFAULT;

    private static final String iOS_AUTOMATION_NAME = "xcuitest";
    private static final String APPIUM_COMMAND = "appium";

    private static String platform;
    private static String deviceName;
    private static String osVersion;
    private static String appPath;
    private static String appiumPort;

    public static final String iOS_PLATFORM = "iOS";
    public static final String ANDROID_PLATFORM = "Android";
    public static final String iPHONE = "iPhone";
    public static final String iPAD = "iPad";

    public static int deviceScreenHeight;
    public static int deviceScreenWidth;

    /**
     * @noReset essentially should only be used if you're intentionally making two tests dependent on one another
     */
    protected static boolean noReset = false;

    /**
     * @fullReset is mutually exclusive from noRest. So should be true when noReset = false.
     */
    protected static boolean fullReset = true;


    @BeforeClass
    public static void setUp() throws Exception {

        // Set run configurations
        initializeRunConfigurationSettings();

        try {
            PLATFORM_NAME = getPlatform();
            APPIUM_PORT_NUMBER_DEFAULT = getAppiumPort();
            APPIUM_SERVER_ADDRESS_DEFAULT = "http://localhost:" + APPIUM_PORT_NUMBER_DEFAULT + "/wd/hub";

            if (isAndroid()) {
                // Setting Android capabilities
                capabilities = DesiredCapabilities.android();

                capabilities.setCapability("app-package", packageName);
                capabilities.setCapability("app-activity", mainActivity);

            } else {
                // Setting iOS capabilities
                capabilities = DesiredCapabilities.iphone();
                capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, iOS_AUTOMATION_NAME);

                //For now the reset capabilities are changeble only for iOS. However, depending on our requirement, we can enable it for Android too by pulliing it out of if/else
                capabilities.setCapability(MobileCapabilityType.NO_RESET, noReset);
                capabilities.setCapability(MobileCapabilityType.FULL_RESET, fullReset);

                // need this capability to start the ios-webkit-debug-proxy
                capabilities.setCapability("startIWDP", true);
                capabilities.setCapability("bundleId", packageName);

                if (isDevice()) {
                    capabilities.setCapability(MobileCapabilityType.UDID, getUDID());
                }
            }

            // Capabilities common to both iOS and Android platforms
            capabilities.setCapability(MobileCapabilityType.PLATFORM, getPlatform());
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, PLATFORM_NAME);
            capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, getBrowserName());

            // Add capability of AVD if emulator else we consider it as device
            if (isEmulator()) {
                capabilities.setCapability("avd", getDeviceName());
            } else {
                capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, getDeviceName());
            }
            capabilities.setCapability(MobileCapabilityType.VERSION, getOSVersion());
            capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 600);
//            capabilities.setCapability(MobileCapabilityType.APP, getAppPath());
//            Logger.logComment(getAppPath());

            startAppiumServer(APPIUM_PORT_NUMBER_DEFAULT);

            /*
             * initialize driver
             */
            if (isAndroid()) {
                driver = new AndroidDriver(new URL(APPIUM_SERVER_ADDRESS_DEFAULT), capabilities);
            } else {
                driver = new IOSDriver(new URL(APPIUM_SERVER_ADDRESS_DEFAULT), capabilities);
            }

            driverWait = new WebDriverWait(driver, AutomationConstants.WAIT_TIME_3S);

            // Getting the Web View context
            CONTEXT_WEBVIEW = driver.getContextHandles().toArray()[1].toString();
            nativeContext = getNativeContext();

            // After driver initializes, we wait for a short while for app to launch and collect the screen dimensions.
            setDeviceHeightAndWidth();

            // Reset app to ensure tests start afresh.
            if (noReset) {
                driver.resetApp();
            }
        } catch (Exception ex) {
            throw new Exception(String.format("Exception caught within BaseTest.setup(). Message: %s", ex.getMessage()));
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        try {
            driver.quit();
            stopAppiumServer(APPIUM_PORT_NUMBER_DEFAULT);
        } catch (Exception ex) {
            throw new Exception(String.format("Depending on your device and timing, the driver might already have closed: %s", ex.getMessage()));
        }
    }

    /**
     * Initialize the application path for use within Base class and example scripts, and set run parameters (like
     * brand and platform) as well.
     * @throws Exception
     */
    protected static void initializeRunConfigurationSettings() throws Exception {
        loadTestConfigFile();
        setPlatform();
        setOSVersion();
        setDeviceName();
        setPackageName();
        setAppPath();
        setAppiumPort();
    }

    /**
     * Read the run configuration json file and return and initialize the json file object
     *
     * @throws Exception
     */
    public static void loadTestConfigFile() throws Exception {
        JSONParser parser = new JSONParser();
        InputStream stream = BaseTest.class.getResourceAsStream(RUN_CONFIG_FILENAME);
        if (stream == null) {
            throw new NullPointerException(String.format("Test config resource file not found: %s", RUN_CONFIG_FILENAME));
        }

        BufferedReader streamReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder strBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            if (inputStr.trim().startsWith("//")) {
                // Skip lines that were comments within the example config JSON file
                continue;
            }

            strBuilder.append(inputStr);
        }

        runConfigJson = (JSONObject) parser.parse(strBuilder.toString());
    }

    /**
     * Read example configuration setting from example config file.
     *
     * @param keyName The setting string name to lookup within the example config file
     * @return Value for the corresponding key (or null if key is not found)
     */
    private static String readConfigSetting(String keyName) {
        String testConfigValue = (String) runConfigJson.get(keyName);
        Logger.logComment(String.format("Test config lookup for key: %s, returning value: %s", keyName, testConfigValue));
        return testConfigValue;
    }

    /**
     * Set the OS platform e.g Android or iOS
     * @throws Exception
     */
    private static void setPlatform() throws Exception {
        String platformFromConfig = readConfigSetting("platform");
        if (platformFromConfig.equalsIgnoreCase(ANDROID_PLATFORM)) {
            Logger.logAction("*** Test script will run in 'Android' mode ***");
            platform = ANDROID_PLATFORM;
        } else if (platformFromConfig.equalsIgnoreCase(iOS_PLATFORM)) {
            Logger.logAction("*** Test script will run in 'iOS' mode ***");
            platform = iOS_PLATFORM;
        } else {
            throw new Exception(String.format("We did not recognize the platform %s.", platformFromConfig));
        }
    }

    /**
     * Sets the OS version of platform under execution
     * @throws Exception
     */
    private static void setOSVersion() throws Exception {
        osVersion = readConfigSetting("device_version_" + getPlatform().toLowerCase());
    }

    /**
     * Sets the device name of platform under execution
     * @throws Exception
     */
    private static void setDeviceName() throws Exception {
        deviceName = readConfigSetting("device_name_" + getPlatform().toLowerCase());
    }

    /**
     * Sets the device package name for Android and bundle id for iOS
     * @throws Exception
     */
    private static void setPackageName() throws Exception {
        packageName = readConfigSetting("package_name");
    }

    /**
     * Sets the app path on machine / node
     * @throws Exception
     */
    private static void setAppPath() throws Exception {
        String path;
        if (isAndroid()) {
            path = readConfigSetting("androidAppPackagePath");
        } else if (isiOS()) {
            if (isiOSSimluator()) {
                path = readConfigSetting("iosSimulatorAppPackagePath");
            } else {
                path = readConfigSetting("iosDeviceAppPackagePath");
            }
        } else {
            throw new Exception("Cannot identify Platform type to get correct app path");
        }
        appPath = path;
    }

    /**
     * To set Appium port on which server would start
     * @throws Exception
     */
    private static void setAppiumPort() throws Exception {
        appiumPort = readConfigSetting("default_appium_port_number");
    }

    /**
     * Gets package name of app
     * @return package name
     */
    public static String getPackageName()  {
        return packageName;
    }

    /**
     * Gets the package + id portion of resource ID
     * @return resource id
     */
    public static String getResourceID() {
        return getPackageName() + ":id/";
    }


    /**
     * Gets path to app installable file
     * @return String path to installable file
     * @throws Exception
     */
    public static String getAppPath() throws Exception { return appPath; }

    /**
     * Gets platform that is used to run tests
     * @return String platform name
     */
    public static String getPlatform() {
        return platform;
    }

    /**
     * Gets OS version of platform that is used to run tests
     * @return String OS version
     */
    public static String getOSVersion() {
        return osVersion;
    }

    /**
     * Gets Device name used to run tests
     * @return String device name
     */
    public static String getDeviceName() { return deviceName; }

    /**
     * Helper method to get 'target' from run config file
     * @return String target value
     * @throws Exception
     */
    private static String getTarget() throws Exception {
        return readConfigSetting("target");
    }

    /**
     * To get Appium port number on which the server starts
     * @return String appium port
     * @throws Exception
     */
    private static String getAppiumPort() throws Exception {
        return appiumPort;
    }

    /**
     * Gets iOS device UDID
     * @return String UDID of device
     * @throws Exception if UDID is empty
     */
    public static String getUDID() throws Exception {
        if (isiOS()) {
            String UDID = readConfigSetting("udid");
            if (UDID.equals("")) {
                throw new Exception("UDID in config file is empty");
            } else {
                return UDID;
            }
        } else {
            throw new Exception("UDID is only for iOS cannot get it for: " + getPlatform());
        }
    }

    /**
     * Get the Browser name to be used during execution
     * @return browser name, chrome for Android, safari for iOS
     * @throws Exception
     */
    public static String getBrowserName() throws Exception {
        if (isAndroid()) {
            return BrowserType.CHROME;
        } else {
            return BrowserType.SAFARI;
        }
    }

    /**
     * Checks to see if this is an Android device.
     *
     * @return true if Android platform
     */
    public static boolean isAndroid() {
        return getPlatform().equals(ANDROID_PLATFORM);
    }

    /**
     * Checks to see if this is an iOS device.
     *
     * @return true if iOS platform
     */
    public static boolean isiOS() {
        return getPlatform().equals(iOS_PLATFORM);
    }

    /**
     * Check if we are using a simulator
     * @return true if simulator else false
     * @throws Exception
     */
    public static boolean isiOSSimluator() throws Exception {
        return getTarget().equalsIgnoreCase("simulator");
    }

    /**
     * Check if we are using an android emulator
     * @return true if we use an emulator else false
     * @throws Exception
     */
    public static boolean isEmulator() throws Exception {
        return getTarget().equalsIgnoreCase("emulator");
    }

    /**
     * Check if we are using a device to run tests
     * @return true if device, else false
     * @throws Exception
     */
    public static boolean isDevice() throws Exception {
        return getTarget().equalsIgnoreCase("device");
    }

    /**
     * To start Appium server
     * @param port - port number on which the server starts
     * @throws Exception
     */
    private static void startAppiumServer(String port) throws Exception {
        Logger.logAction(String.format("Starting Appium server on port %s", port));
        if (!isAppiumServerRunning(port)) {
            // command to start Appium server --> appium -p 4273
            String completeAppiumCommand = String.format("%s -p %s", APPIUM_COMMAND, port);
            Logger.logComment("Starting Server");
            try {
                Logger.logComment("Appium server started with version: " + runCMD(completeAppiumCommand));
            } catch (Exception serverNotStarted) {
                Logger.logWarning("Could not start Appium Server");
                throw new Exception(serverNotStarted.getMessage());
            }
        } else {
            Logger.logComment("Appium server already started");
        }
    }

    /**
     * To check if Appium server is already up and running on the desired port
     * @param port desired port for server to start
     * @return true if server running, else false.
     * @throws Exception
     */
    private static boolean isAppiumServerRunning(String port) throws Exception {
        Logger.logAction(String.format("Checking if Appium server is executing on port %s", port));

        // command to check if Appium service running on port --> sh -c lsof -P | grep ':4723'
        String checkCommand[] = new String[]{"sh", "-c", String.format("lsof -P | grep :%s", port)};
        if (runCommandAndWaitToComplete(checkCommand).equals("")) {
            Logger.logWarning(String.format("Appium server is not running on port %s", port));
            return false;
        } else {
            Logger.logComment(String.format("Appium server is running on port %s", port));
            return true;
        }
    }

    /**
     * To stop appium server
     * @param port desired port for server to stop
     * @throws Exception
     */
    private static void stopAppiumServer(String port) throws Exception {
        Logger.logAction(String.format("Stopping Appium server on port %s", port));

        // command to stop Appium service running on port --> sh -c lsof -P | grep ':4723' | awk '{print $2}' | xargs kill -9
        String stopCommand[] = new String[]{"sh", "-c", String.format("lsof -P | grep ':%s' | awk '{print $2}' | xargs kill -9", port)};
        runCommandAndWaitToComplete(stopCommand);
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
        if (driver.getContext().contains(CONTEXT_WEBVIEW)) {
            Logger.logWarning(String.format("Already web context: %s", CONTEXT_WEBVIEW));
        } else {
            driver.context(CONTEXT_WEBVIEW);
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
     * Sets the device dimensions, that can be used for swiping / scrolling and other co-ordinate related calculations
     */
    private static void setDeviceHeightAndWidth() throws Exception {
        // During set up, we would like to get Device Screen Co-ordinates, that can be later used for Control Centre and Notification Drawer.
        // We want these here because there have been instances where getting the co-ordinates in between steps gives values as 0 (Appium issue)
        useNativeContext();
        deviceScreenWidth = driver.manage().window().getSize().getWidth();
        deviceScreenHeight = driver.manage().window().getSize().getHeight();
        Logger.logComment("Device Width: " + deviceScreenWidth + " : Device Height : " + deviceScreenHeight);
        useWebContext();
    }

    /**
     * Method to launch a website.
     * @param websiteURL - link that we want to open in browser
     * @throws Exception - if unable to launch site
     */
    public static void launchWebSite(String websiteURL) throws Exception {
        Logger.logAction(String.format("Launching URL: %s", websiteURL));
        useWebContext();
        driver.get(websiteURL);
    }

    /**
     * Gets current URL in browser
     * @return string url
     * @throws Exception
     */
    protected static String getBrowserURL() throws Exception {
        useWebContext();
        return driver.getCurrentUrl();
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
     * Finds element on screen with CSS.
     * @param elementCSS - string value of CSS
     * @return - WebElement
     * @throws Exception if CSS not located on screen
     */
    protected static WebElement findElementByCSS(String elementCSS) throws Exception {
        Logger.logAction(String.format("Finding Element with CSS '%s'", elementCSS));
        WebElement element ;
        try {
            element = driver.findElement(By.cssSelector(elementCSS));
        } catch (Exception elementNotFound) {
            throw new Exception(String.format("Element with CSS '%s' not found", elementCSS));
        }
        return element;
    }

    /**
     * Finds element on screen with CSS and clicks it
     * @param elementCSS element with CSS to be clicked.
     * @throws Exception if CSS not found
     */
    protected static void findElementAndClickByCSS(String elementCSS) throws Exception {
        WebElement element = findElementByCSS(elementCSS);
        element.click();
    }

    /**
     * Finds element on screen with xpath.
     * @param elementXpath - string value of xpath
     * @return - WebElement
     * @throws Exception if xpath not located on screen
     */
    protected static WebElement findElementByXpath(String elementXpath) throws Exception {
        Logger.logAction(String.format("Finding Element with Xpath '%s'", elementXpath));
        WebElement element ;
        try {
            element = driver.findElement(By.cssSelector(elementXpath));
        } catch (Exception elementNotFound) {
            throw new Exception(String.format("Element with Xpath '%s' not found", elementXpath));
        }
        return element;
    }

    /**
     * Finds element on screen with xpath and clicks it
     * @param elementXpath element with xpath to be clicked.
     * @throws Exception if xpath not found
     */
    protected static void findElementAndClickByXpath(String elementXpath) throws Exception {
        WebElement element = findElementByCSS(elementXpath);
        element.click();
    }

    /**
     * Finds element on screen with link text.
     * @param elementLinkText - string value of link text
     * @return - WebElement
     * @throws Exception if link text not located on screen
     */
    protected static WebElement findElementByLinkText(String elementLinkText) throws Exception {
        Logger.logAction(String.format("Finding Element with Link Text '%s'", elementLinkText));
        WebElement element ;
        try {
            element = driver.findElement(By.cssSelector(elementLinkText));
        } catch (Exception elementNotFound) {
            throw new Exception(String.format("Element with Link Text '%s' not found", elementLinkText));
        }
        return element;
    }

    /**
     * Finds element on screen with link text and clicks it
     * @param elementLinkText element with link text to be clicked.
     * @throws Exception if link text not found
     */
    protected static void findElementAndClickByLinkText(String elementLinkText) throws Exception {
        WebElement element = findElementByLinkText(elementLinkText);
        element.click();
    }

    /**
     * Find element by CSS that is within a parent element having an id.
     * @param elementByID id of parent element
     * @param elementCSS css of element to be found
     * @return WebElement
     * @throws Exception if element not found
     */
    protected static WebElement findElementByIDAndCSS(String elementByID, String elementCSS) throws Exception {
        WebElement parentElement = findElementByID(elementByID);
        WebElement element;

        try {
            element = parentElement.findElement(By.cssSelector(elementCSS));
            return element;
        } catch (Exception ex) {
            throw new Exception(String.format("Did not find any element with css '%s' within parent element ID '%s'", elementCSS, elementByID));
        }
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
     * To scroll to an element within a webview
     * @param element - expected element
     * @throws Exception
     */
    protected static void scrollToElementInBrowser(WebElement element) throws Exception {
        useWebContext();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);",element);
    }

    /**
     * Waits for web view page to load.
     * @throws Exception if page not loaded in 2 mins
     */
    protected static void waitForPageToLoad() throws Exception {
        useWebContext();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Date twoMinutesFromNow = new Date(Calendar.getInstance().getTimeInMillis() + AutomationConstants.WAIT_TIME_120_MS);

        while (!js.executeScript("return document.readyState").equals("complete")) {
            driverWait.withTimeout(3, TimeUnit.SECONDS);

            if (new Date().after(twoMinutesFromNow)) {
                throw new Exception(String.format("Waited for %d milli seconds but page load not complete", AutomationConstants.WAIT_TIME_120_MS));
            }
        }
    }

    /**
     * To execute a terminal command, and get the complete log response.
     *
     * @param command - command we intend to execute via terminal
     * @return - the execution log. We can scan this to check if the command executed was a success or failure.
     * @throws Exception
     */
    public static String runCommandAndWaitToComplete(String[] command) throws Exception {
        String completeCommand = String.join(" ", command);
        Logger.logAction("Executing command: " + completeCommand);
        String line;
        String returnValue = "";

        try {
            Process processCommand = Runtime.getRuntime().exec(command);
            BufferedReader response = new BufferedReader(new InputStreamReader(processCommand.getInputStream()));

            try {
                processCommand.waitFor();
            } catch (InterruptedException commandInterrupted) {
                throw new Exception("Were waiting for process to end but something interrupted it" + commandInterrupted.getMessage());
            }

            while ((line = response.readLine()) != null) {
                returnValue = returnValue + line + "\n";
            }

            response.close();

        } catch (Exception e) {
            throw new Exception("Unable to run command: " + completeCommand + ". Error: " + e.getMessage());
        }

        Logger.logComment("Response : runCMDAndWaitToComplete(" + completeCommand + ") : " + returnValue);
        return returnValue;
    }

    /**
     * Helper method to run an arbitrary command-line 'command', waits for few seconds after command executes
     * @param command string that will be sent to command-line
     * @return The first line response after executing command. (can be used to verify)
     */
    public static String runCMD(String command) throws Exception {
        Logger.logAction("Executing command: " + command);
        try {
            Process process = Runtime.getRuntime().exec((command));
            process.waitFor(AutomationConstants.WAIT_TIME_10S, TimeUnit.SECONDS);
            BufferedReader response = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return response.readLine();
        } catch (Exception e) {
            Logger.logWarning("Unable to run command: " + command + ". Error: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
