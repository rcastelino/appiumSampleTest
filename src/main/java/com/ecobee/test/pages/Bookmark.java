package com.ecobee.test.pages;

import com.ecobee.test.AutomationConstants;
import com.ecobee.test.BaseTest;
import com.ecobee.test.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by RolandC on 2017-09-09.
 * This class holds all id's, classname's, methods related to Bookmarking a page
 */
public class Bookmark extends BaseTest {

    private static String menu_button_id = "More options";
    private static String bookmark_page_id = "Bookmark this page";
    private static String bookmark_option_id = "Bookmarks";
    private static String mobile_bookmark_title_id = getResourceID() + "title";
    private static String bookmark_list_id = getResourceID() + "bookmark_row";
    private static String bookmark_action_bar_id = getResourceID() + "action_bar";


    private static String mobile_bookmark_text = "Mobile bookmarks";



    /**
     * Book mark a webpage that is currently in view
     * @throws Exception if bookmarks option not found
     */
    public static void bookmarkCurrentPage() throws Exception {
        Logger.logAction("Bookmark current page");
        invokeMenuOptions();
        //TODO : add check to see if page is already bookmarked. Alternatively, we can also remove all book marks at start of test
        findElementAndClickByID(bookmark_page_id);
        HomePage.waitForHomePageToLoad();
    }


    public static void printRecentBookmarkTitle() throws Exception {
        Logger.logComment(String.format("Title of the created bookmark: %s", getRecentBookmarkTitle()));
    }

    /**
     * Gets most recent bookmark title
     * @return String title
     * @throws Exception
     */
    public static String getRecentBookmarkTitle() throws Exception {
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
    private static void invokeMenuOptions() throws Exception {
        useNativeContext();
        findElementAndClickByID(menu_button_id);
    }


    /**
     * Navigate to Bookmarks list.
     * @throws Exception
     */
    private static void navigateToBookmarks() throws Exception {
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
     * Check if Mobile Bookmark list if launched
     * @return true if list in view, else false
     * @throws Exception
     */
    private static boolean isMobileBookmarkAlreadyLaunched() throws Exception {
        return findElementByID(bookmark_action_bar_id).isDisplayed();
    }
}
