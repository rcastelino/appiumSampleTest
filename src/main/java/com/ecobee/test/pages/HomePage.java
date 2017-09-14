package com.ecobee.test.pages;

import com.ecobee.test.BaseTest;
import com.ecobee.test.Logger;
import com.sun.imageio.plugins.wbmp.WBMPImageReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.*;

/**
 * Created by RolandC on 2017-09-09.
 * This class holds all id's, classname's, methods related to Home page
 */
public class HomePage extends BaseTest{

    private static String stories_id = "stories";
    private static String main_id = "main";
    private static String featured_id = "featured";


    private static String river_story_classname = "river-story  ";
    private static String featured_story_classname = "featured-story";
    private static String next_classname = "next";
    private static String previous_classname = "prev";
    private static String storyText_classname = "details";
    private static String comments_classname = "comment-count ";
    private static String editor_classname_1 = "a.nickname";
    private static String editor_classname_2 = "span.nickname";

    /**
     * Wait for Home page to load
     * @throws Exception
     */
    public static void waitForHomePageToLoad() throws Exception {
        useWebContext();
        waitForElementToLoadByID(stories_id);
    }

    /**
     * Print count of stories in list + carousal
     * @throws Exception
     */
    public static void printNumberOfArticlesOnPage() throws Exception {
        Logger.logComment(String.format("Total number of Articles on Page: %d", getNumberOfArticlesOnPage()));
    }

    /**
     * Print unique list of icons on articles and how many times each was used.
     * @throws Exception
     */
    public static void printListOfUniqueArticleEditors()throws Exception {
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
    public static void printMostDiscussedStories() throws Exception {
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
    private static int getNumberOfArticlesOnPage() throws Exception {
        return getNumberOfStoriesOnPage() + getNumberOfCarousalArticlesOnPage();
    }


    /**
     * Get count of stories in page list
     * @return integer number of stories
     * @throws Exception
     */
    private static int getNumberOfStoriesOnPage() throws Exception {
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
    private static int getNumberOfCarousalArticlesOnPage() throws Exception {
        Logger.logAction("Getting number of Carousal articles available on page");

        List<WebElement> carousalList = findElementByID(featured_id).findElements(By.className(featured_story_classname));
        Logger.logAction("Number of carousal articles on page: " + carousalList.size());
        return carousalList.size();
    }
}

