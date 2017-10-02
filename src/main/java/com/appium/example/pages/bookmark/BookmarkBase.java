package com.appium.example.pages.bookmark;

import com.appium.example.AutomationConstants;
import com.appium.example.BaseTest;
import com.appium.example.Logger;
import com.appium.example.interfaces.BookmarkInterface;
import com.appium.example.pages.PageObject_Base;
import com.appium.example.pages.homepage.HomeBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by RolandC on 2017-09-09.
 * This class holds all id's, classname's, methods related to Bookmarking a page
 */
public class BookmarkBase extends PageObject_Base implements BookmarkInterface {

    // id's
    private static String menu_button_id = "More options";
    private static String bookmark_page_id = "Bookmark this page";
    private static String bookmark_option_id = "Bookmarks";
    private static String mobile_bookmark_title_id = getResourceID() + "title";
    private static String bookmark_list_id = getResourceID() + "bookmark_row";
    private static String bookmark_action_bar_id = getResourceID() + "action_bar";
    private static String bookmark_close_id = getResourceID() + "close_menu_id";

    // texts
    private static String mobile_bookmark_text = "Mobile bookmarks";

    /**
     * Book mark a webpage that is currently in view
     * @throws Exception if bookmarks option not found
     */
    public void bookmarkCurrentPage() throws Exception {
        Logger.logAction("bookmark current page");
        invokeMenuOptions();
        //TODO : add check to see if page is already bookmarked. Alternatively, we can also remove all book marks at start of example
        findElementAndClickByID(bookmark_page_id);
        homePage.waitForHomePageToLoad();
    }


    /**
     * Print bookmark title of the most recent bookmark
     * @throws Exception
     */
    public void printRecentBookmarkTitle() throws Exception {
        Logger.logComment(String.format("Title of the created bookmark: %s", getRecentBookmarkTitle()));
    }

    /**
     * Gets most recent bookmark title
     * @return String title
     * @throws Exception
     */
    public String getRecentBookmarkTitle() throws Exception {
        String bookmarkTitle;

        navigateToBookmarks();

        List<WebElement> allBookmarkListElement = findElementsByID(bookmark_list_id);
        if (allBookmarkListElement.size() == 0) {
            throw new Exception(String.format("Expected atleast 1 bookmark in list, but we got %d", allBookmarkListElement.size()));
        }
        WebElement bookMarkElement = allBookmarkListElement.get(allBookmarkListElement.size() - 1);
        bookmarkTitle = bookMarkElement.findElement(By.className(AutomationConstants.ANDROID_TEXTVIEW)).getText();

        return bookmarkTitle;
    }

    /**
     * To invoke menu options from browser app
     * @throws Exception if menu options not found
     */
    public void invokeMenuOptions() throws Exception {
        useNativeContext();
        findElementAndClickByID(menu_button_id);
    }


    /**
     * Navigate to Bookmarks list.
     * @throws Exception
     */
    public void navigateToBookmarks() throws Exception {
        invokeMenuOptions();
        findElementAndClickByID(bookmark_option_id);

        //Mobile Bookmarks on subsequent execution may still be in view.
        if (isMobileBookmarkAlreadyLaunched()) {
            Logger.logComment("Mobile Bookmarks are already launched.");
        } else {
            Logger.logComment("Selecting Mobile Bookmarks.");
            findAndClickElementByClassAndText(AutomationConstants.ANDROID_TEXTVIEW, mobile_bookmark_text);
        }
    }

    /**
     * Check if Mobile bookmark list if launched
     * @return true if list in view, else false
     * @throws Exception
     */
    public boolean isMobileBookmarkAlreadyLaunched() throws Exception {
        try {
            return findElementByClassAndText(AutomationConstants.ANDROID_TEXTVIEW, mobile_bookmark_text).isDisplayed();
        } catch (Exception ex) {
            Logger.logComment(String.format("We did not find header text '%s'", mobile_bookmark_text));
            return false;
        }
    }

    /**
     * Close Bookmarks screen
     * @throws Exception
     */
    public void closeBookmarks() throws Exception {
        findElementAndClickByID(bookmark_close_id);
    }
}
