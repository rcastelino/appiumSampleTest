package com.appium.example.tests.regressionTests;

import com.appium.example.Logger;
import com.appium.example.tests.TEST_Base;
import org.junit.Test;

/**
 * Created by RolandC on 2017-09-09.
 * JUnit example that will launch URL on chrome browser and perform operations.
 */

/**
 * * WorkFlow
 * Write a simple application per the following instruction:
 *   Use Appium Webdriver
 *   Open Mobile Browser. (Chrome for Android, Safari for iOS)
 *   Browse to http://m.slashdot.org/
 *   Print how many articles are on the page
 *   Print a list of unique (different) icons used on article titles and how many times was it used
 *   Create a bookmark for this page
 *   Return the title of the created bookmark
 *   Scroll to end of page and navigate to Desktop Site
 *   Verify URL of desktop site, scroll down page and select Mobile Site
 */

public class Test_001_Browse_Print_Bookmark_Base_Base_flows extends TEST_Base {

    private static final String URL_TEST_MOBILE_WEBSITE = "https://m.slashdot.org/";
    private static final String URL_TEST_WEBSITE = "https://slashdot.org/";

    @Test
    public void testWorkFlowSteps() throws Exception {

        Logger.logAction("Begin: testWorkFlowSteps() - Test_001_Browse_Print_Bookmark_Base_Base_flows");

        Logger.logAction("Open Chrome in simulator/emulator\n" +
                "\tBrowse to a mobile website\n" +
                "\tPrint how many articles are on the page\n" +
                "\tPrint a list of unique (different) icons used on article titles and how many times was it used\n" +
                "\tCreate a bookmark for this page\n" +
                "\tReturn the title of the created bookmark\n" +
                "\tScroll to end of page and navigate to Desktop Site\n" +
                "\tVerify URL of desktop site, scroll down page and select Mobile Site");

        Logger.logStep("Open Chrome, browse to: " + URL_TEST_MOBILE_WEBSITE);
        launchWebSite(URL_TEST_MOBILE_WEBSITE);
        homePage.waitForHomePageToLoad();

        Logger.logStep("Print how many articles are on the page");
        homePage.printNumberOfArticlesOnPage();

        Logger.logStep("Print a list of unique (different) icons used on article titles and how many times was it used");
        homePage.printListOfUniqueArticleEditors();

        Logger.logStep("Create a bookmark for this page");
        bookmark.bookmarkCurrentPage();

        Logger.logStep("Return the title of the created bookmark");
        bookmark.printRecentBookmarkTitle();
        bookmark.closeBookmarks();

        Logger.logStep("Navigate to Desktop Site");
        homePage.selectDesktopSite();

        Logger.logStep("Verify correct URL of desktop site launched");
        homePage.verifyPageURL(URL_TEST_WEBSITE);

        Logger.logStep("Navigate back to Mobile Site");
        homePage.selectMobileSite();

        Logger.logStep("Verify correct URL of mobile site launched");
        homePage.verifyPageURL(URL_TEST_MOBILE_WEBSITE);
    }
}
