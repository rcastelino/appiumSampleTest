package com.appium.example.pages;

import com.appium.example.BaseTest;
import com.appium.example.constructors.*;
import com.appium.example.interfaces.*;

/**
 * Created by RolandC on 2017-09-28.
 */
public abstract  class PageObject_Base extends BaseTest {

    // declare page objects here
    protected static HomeInterface homePage;
    protected static BookmarkInterface bookmark;

    /**
     * initialize the page objects in setUp() method.
     * @throws Exception
     */
    public static void setUp() throws Exception {
        homePage = new HomeConstructor(getPlatform()).getMyHomePage();
        bookmark = new BookmarkConstructor(getPlatform()).getMyBookmark();
    }
}
