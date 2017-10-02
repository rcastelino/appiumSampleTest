package com.appium.example.pages.homepage;

import com.appium.example.BaseTest;
import com.appium.example.Logger;
import com.appium.example.interfaces.HomeInterface;
import com.appium.example.pages.PageObject_Base;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.*;

/**
 * Created by RolandC on 2017-09-09.
 * This class holds all id's, classname's, methods related to Home page
 */
public class HomeBase extends PageObject_Base implements HomeInterface{

    // id's
    private static String stories_id = "stories";
    private static String main_id = "main";
    private static String featured_id = "featured";
    private static String footer_id = "footer";

    // texts
    private static String mobile_view_text = "Mobile View";
    private static String desktop_view_text = "Desktop Site";

    // class names
    private static String river_story_classname = "river-story  ";
    private static String featured_story_classname = "featured-story";
    private static String next_classname = "next";
    private static String previous_classname = "prev";
    private static String storyText_classname = "details";
    private static String comments_classname = "comment-count ";
    private static String editor_classname_1 = "a.nickname";
    private static String editor_classname_2 = "span.nickname";

    // css
    private static String desktop_site_css = ".skip-ad.desktop-site";

    /**
     * Wait for Home page to load
     * @throws Exception
     */
    public void waitForHomePageToLoad() throws Exception {
        useWebContext();
        waitForElementToLoadByID(stories_id);
    }

    /**
     * Print count of stories in list + carousal
     * @throws Exception
     */
    public void printNumberOfArticlesOnPage() throws Exception {
        Logger.logComment(String.format("Total number of Articles on Page: %d", getNumberOfArticlesOnPage()));
    }

    /**
     * Print unique list of icons on articles and how many times each was used.
     * @throws Exception
     */
    public void printListOfUniqueArticleEditors()throws Exception {
        List<String> editorList = getListOfArticleEditors();

        //Getting unique list of editors
        Set<String> uniqueEditorList = new HashSet<>(editorList);

        //Using collection to check number of articles written by an editor
        for (String editorName : uniqueEditorList) {
            Logger.logComment(String.format("Editor '%s' has %d articles", editorName, Collections.frequency(editorList, editorName)));
        }
    }

    /**
     * Gets list of Article Editors on page
     * @return - List of String - editor names
     * @throws Exception
     */
    private static List<String> getListOfArticleEditors() throws Exception {
        Logger.logAction("Getting list of article editors");
        List<String> editorList = new ArrayList<>();

        WebElement storyElement = findElementByID(stories_id).findElement(By.id(main_id));
        List<WebElement> storyList = storyElement.findElements(By.className(river_story_classname));
        String editorName;
        for (WebElement story : storyList) {
            //CSS for an Anonymous is different, so catching it here.
            try {
                editorName = story.findElement(By.className(storyText_classname)).findElement(By.cssSelector(editor_classname_1)).getText();
            } catch (Exception editorElementNotFound) {
                editorName = story.findElement(By.className(storyText_classname)).findElement(By.cssSelector(editor_classname_2)).getText();
            }

            //Checking for empty editor names and ignoring from list.
            if (editorName.isEmpty() || editorName.equals("")) {
                continue;
            }
            editorList.add(editorName);
        }
            return editorList;
    }

    /**
     * Prince details of most discussed articles and their comment counts.
     * @throws Exception
     */
    public void printMostDiscussedStories() throws Exception {
        Logger.logAction("Printing most discussed articles and their number of comments");
        List<WebElement> carousalList = findElementByID(featured_id).findElements(By.className(featured_story_classname));
        WebElement storyTextElement;
        WebElement nextElement;
        WebElement commentsElement;

        int articleCount = 0;

        //TODO - for now assuming the carousal is always on first story, need to make this better by selecting previous button before start of this loop.
        //Looping to navigate to each article in carousal to get its title and comments.
        for (WebElement element : carousalList) {
            nextElement = element.findElement(By.className(next_classname));
            storyTextElement = element.findElement(By.className(storyText_classname));
            commentsElement = element.findElement(By.className(comments_classname));

            Logger.logComment(String.format(" %d) Title %s : Comments : %s", articleCount+1, storyTextElement.getText(), commentsElement.getText()));
            if (nextElement.isDisplayed()) {
                nextElement.click();
            }
            articleCount++;
        }
    }

    /**
     * Get total count of stories in list + carousal
     * @return integer number of total stories
     * @throws Exception
     */
    private int getNumberOfArticlesOnPage() throws Exception {
        return getNumberOfStoriesOnPage() + getNumberOfCarousalArticlesOnPage();
    }

    /**
     * Get count of stories in page list
     * @return integer number of stories
     * @throws Exception
     */
    private int getNumberOfStoriesOnPage() throws Exception {
        Logger.logAction("Getting number of stories available on page");

        WebElement storyElement = findElementByID(stories_id).findElement(By.id(main_id));
        List<WebElement> storyList = storyElement.findElements(By.className(river_story_classname));
        int countEmptyStories = 0;

        //Some stories are empty elements, ignoring them for our count.
        for (WebElement element : storyList) {
            if ((element.getText().isEmpty()) || element.getText().equals("")){
                countEmptyStories++;
            }
        }
        Logger.logAction("Number of stories on page: " + (storyList.size()  - countEmptyStories));

        return (storyList.size() - countEmptyStories);
    }

    /**
     * Get count of stories in carousal
     * @return integer number of stories
     * @throws Exception
     */
    private int getNumberOfCarousalArticlesOnPage() throws Exception {
        Logger.logAction("Getting number of Carousal articles available on page");

        List<WebElement> carousalList = findElementByID(featured_id).findElements(By.className(featured_story_classname));
        Logger.logAction("Number of carousal articles on page: " + carousalList.size());
        return carousalList.size();
    }


    /**
     * Select 'Desktop Site' option from the footer of mobile site
     * @throws Exception
     */
    public void selectDesktopSite() throws Exception {
        Logger.logAction(String.format("Trying to select '%s' on page", desktop_view_text));
        useWebContext();
        WebElement desktopSiteElement = findElementByCSS(desktop_site_css);
        scrollToElementInBrowser(desktopSiteElement);
        desktopSiteElement.click();
        waitForPageToLoad();
    }

    /**
     * Select 'Mobile View' option from the footer of desktop site.
     * @throws Exception
     */
    public void selectMobileSite() throws Exception {
        Logger.logAction(String.format("Trying to select '%s' on page", mobile_view_text));
        useWebContext();
        WebElement footerElement = findElementByID("ft");
        scrollToElementInBrowser(footerElement);
        List<WebElement> footerList = footerElement.findElement(By.xpath("//*[@role='footer']")).findElements(By.tagName("a"));
        for (WebElement element : footerList) {
            if (element.getText().equals(mobile_view_text)) {
                element.click();
                waitForPageToLoad();
                return;
            }
        }
        throw new Exception(String.format("Could not find link %s", mobile_view_text));
    }

    /**
     * Verify url of web / mobile page currently in view
     * @param url - expected url
     * @throws Exception
     */
    public void verifyPageURL(String url) throws Exception {
        Logger.logAction(String.format("Verifying that page url is : %s", url));
        if (getBrowserURL().equals(url)) {
            Logger.logComment(String.format("Browser page url matches - %s", url));
        } else {
            throw new Exception(String.format("Browser page url expected <%s> but found <%s>", url, getBrowserURL()));
        }
    }
}

