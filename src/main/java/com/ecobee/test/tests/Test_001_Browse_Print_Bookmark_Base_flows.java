package com.ecobee.test.tests;

import com.ecobee.test.BaseTest;
import com.ecobee.test.Logger;
import com.ecobee.test.pages.Bookmark;
import com.ecobee.test.pages.HomePage;
import org.junit.Test;

/**
 * Created by RolandC on 2017-09-09.
 * JUnit test that will launch URL on chrome browser and perform operations.
 */

/**
 * * Test 2
 * Write a simple application per the following instruction:
 *   Use Appium Webdriver
 *   Open Chrome in simulator/emulator
 *   Browse to http://m.slashdot.org/
 *   Print how many articles are on the page
 *   Print a list of unique (different) icons used on article titles and how many times was it used
 *   Create a bookmark for this page
 *   Return the title of the created bookmark
 */

public class Test_001_Browse_Print_Bookmark_Base_flows extends BaseTest{

    private static final String URL_TEST_WEBSITE = "https://m.slashdot.org";

    @Test
    public void testAssignmentSteps() throws Exception {

        Logger.logAction("Begin: testAssignmentSteps() - Test_001_Browse_Print_Bookmark_Base_flows");

        Logger.logAction("Test 2\n" +
                "Write a simple application per the following instruction:\n" +
                "\tUse Appium Webdriver\n" +
                "\tOpen Chrome in simulator/emulator\n" +
                "\tBrowse to http://m.slashdot.org/\n" +
                "\tPrint how many articles are on the page\n" +
                "\tPrint a list of unique (different) icons used on article titles and how many times was it used\n" +
                "\tCreate a bookmark for this page\n" +
                "\tReturn the title of the created bookmark");

        Logger.logStep("Open Chrome, browse to: " + URL_TEST_WEBSITE);
        launchWebSite(URL_TEST_WEBSITE);
        HomePage.waitForHomePageToLoad();

        Logger.logStep("Print how many articles are on the page");
        HomePage.printNumberOfArticlesOnPage();

        //TODO - clarify requirements, could not find icons for articles on mobile website
        Logger.logStep("Print a list of unique (different) icons used on article titles and how many times was it used");
        HomePage.printListOfUniqueArticleEditors();

        Logger.logStep("Create a bookmark for this page");
        Bookmark.bookmarkCurrentPage();

        Logger.logStep("Return the title of the created bookmark");
        Bookmark.printRecentBookmarkTitle();
    }
}
