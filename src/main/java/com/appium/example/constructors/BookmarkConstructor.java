package com.appium.example.constructors;

import com.appium.example.BaseTest;
import com.appium.example.pages.bookmark.*;

/**
 * Created by RolandC on 2017-09-28.
 * Initialize the bookmark screen page object based on platform
 */
public class BookmarkConstructor {

    private BookmarkBase myBookmark;

    public BookmarkConstructor(String platform) throws Exception {
        if (platform.toLowerCase().equals(BaseTest.ANDROID_PLATFORM.toLowerCase())) {
            myBookmark = new BookmarkAndroid();
        } else if (platform.toLowerCase().equals(BaseTest.iOS_PLATFORM.toLowerCase())) {
            myBookmark = new BookmarkiOS();
        } else {
            throw new Exception(String.format("Not a valid platform to execute tests: %s", platform));
        }
    }

    public BookmarkBase getMyBookmark() { return myBookmark; }
}
