package com.appium.example.constructors;

import com.appium.example.BaseTest;
import com.appium.example.pages.homepage.*;

/**
 * Created by RolandC on 2017-09-28.
 * Initialize the home screen page object based on platform
 */
public class HomeConstructor {

    private HomeBase myHomePage;

    public HomeConstructor(String platform) throws Exception {
        if (platform.toLowerCase().equals(BaseTest.ANDROID_PLATFORM.toLowerCase())) {
            myHomePage = new HomeAndroid();
        } else if (platform.toLowerCase().equals(BaseTest.iOS_PLATFORM.toLowerCase())) {
            myHomePage = new HomeiOS();
        } else {
            throw new Exception(String.format("Not a valid platform to execute tests: %s", platform));
        }
    }

    public HomeBase getMyHomePage() { return myHomePage; }
}
