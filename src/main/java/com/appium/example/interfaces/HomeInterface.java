package com.appium.example.interfaces;

/**
 * Created by RolandC on 2017-09-28.
 * Declare methods related to Home screen
 */
public interface HomeInterface {

    /**
     * Wait for Home page to load
     * @throws Exception
     */
    void waitForHomePageToLoad() throws Exception;

    /**
     * Print count of stories in list + carousal
     * @throws Exception
     */
    void printNumberOfArticlesOnPage() throws Exception;

    /**
     * Print unique list of icons on articles and how many times each was used.
     * @throws Exception
     */
    void printListOfUniqueArticleEditors()throws Exception;

    /**
     * Prince details of most discussed articles and their comment counts.
     * @throws Exception
     */
    void printMostDiscussedStories() throws Exception;

    /**
     * Scrolls to link 'desktop site' and selects it.
     * @throws Exception
     */
    void selectDesktopSite() throws Exception;

    /**
     * Scrolls to link 'Mobile View' and selects in
     * @throws Exception
     */
    void selectMobileSite() throws Exception;

    /**
     * Verify that page URL matches
     * @param url - expected url
     * @throws Exception
     */
    void verifyPageURL(String url) throws Exception;
}
