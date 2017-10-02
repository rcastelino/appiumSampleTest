package com.appium.example.interfaces;

/**
 * Created by RolandC on 2017-09-28.
 * Declare methods related to Bookmarks feature
 */
public interface BookmarkInterface {

    /**
     * Book mark a webpage that is currently in view
     * @throws Exception if bookmarks option not found
     */
    void bookmarkCurrentPage() throws Exception;

    /**
     * Print bookmark title of the most recent bookmark
     * @throws Exception
     */
    void printRecentBookmarkTitle() throws Exception;

    /**
     * Gets most recent bookmark title
     * @return String title
     * @throws Exception
     */
    String getRecentBookmarkTitle() throws Exception;

    /**
     * To invoke menu options from browser app
     * @throws Exception if menu options not found
     */
    void invokeMenuOptions() throws Exception;

    /**
     * Navigate to Bookmarks list.
     * @throws Exception
     */
    void navigateToBookmarks() throws Exception;

    /**
     * Check if Mobile bookmark list if launched
     * @return true if list in view, else false
     * @throws Exception
     */
    boolean isMobileBookmarkAlreadyLaunched() throws Exception;

    /**
     * Close bookmarks screen
     * @throws Exception
     */
    void closeBookmarks() throws Exception;
}
