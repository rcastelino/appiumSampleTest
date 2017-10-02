================================================================================================
- WorkFlow
================================================================================================
- Write a simple application per the following instruction:
	- Use Appium Webdriver
	- Open Mobile Browser. (Chrome for Android, Safari for iOS)
	- Browse to http://m.slashdot.org/
	- Print how many articles are on the page
	- Print a list of unique (different) icons used on article titles and how many times was it used
	- Create a bookmark for this page
	- Return the title of the created bookmark
	- Scroll to end of page and navigate to Desktop Site
	- Verify URL of desktop site, scroll down page and select Mobile Site

================================================================================================

[] To Get this project running on an OSX machine, install the following:
- java jdk 8
- maven
- IntelliJ IDE (community edition) (Any other Java IDE like eclipse should also be fine, but I haven't tried it)


[] After installation:
- launch IntelliJ IDE
- select 'File -> Open'
- select the 'pom.xml' file of this project.


[] Project layout:
- BaseTest.java is the Base class that initiates AppiumDriver, sets capabilities, contains helper methods.
- 'pages' package holds all classes related to the application / URL pages
- 'tests' package holds the JUnit test to interact with Chrome browser.
- the console output is available to view in 'sampleConsoleLogtxt'

- NOTE - Should work both on emulator and device. However, I have tried it only on device since my machine slows down a lot when emulator is launched.
to run on emulator, uncomment the 'avd' capability in BaseTest.java



[] To execute the test:
- Start Appium server
- Connect Android device or launch Android emulator with Chrome application installed.
- From IntelliJ IDE
    - from 'Project' panel, expand the 'tests' package
    - right click on the junit test class Eg. 'Test_001_Browse_Print_Bookmark_Base_flows', and select 'Run'

NOTE - I had to downgrade Chromedriver to execute these tests. -> Download Chromium version 2.22 from http://chromedriver.storage.googleapis.com/index.html and save it in appium/node_modules/android-chrome-driver/ path
