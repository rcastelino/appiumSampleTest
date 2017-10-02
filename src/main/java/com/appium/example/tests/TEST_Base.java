package com.appium.example.tests;

import com.appium.example.BaseTest;
import com.appium.example.pages.PageObject_Base;
import org.junit.BeforeClass;

/**
 * Created by RolandC on 2017-09-24.
 * Base class for all tests, initializes setUp.
 */
public class TEST_Base extends PageObject_Base {

    @BeforeClass
    public static void setUp() throws Exception {

        // initialize setUp() methods
        BaseTest.setUp();
        PageObject_Base.setUp();
    }
}
