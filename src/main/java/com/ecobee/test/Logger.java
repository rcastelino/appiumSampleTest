package com.ecobee.test;

/**
 * Created by RolandC on 2017-09-09.
 * Helper methods to log console output better
 */
public class Logger {

    private static String suffix = "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ";
    private static String banner = "* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *";
    private static String warning = "#######################";


    public static void logAction(final String msg) {
        System.out.println("  " + msg);
        System.out.println("  " + suffix);
    }

    public static void logComment(final String msg) {
        System.out.println("        -> " + msg);
        System.out.println("  " + suffix);
    }

    public static void logWarning(final String msg) {
        System.out.println(warning);
        System.out.println("### WARNING: " + msg);
        System.out.println(warning);
    }

    public static void logStep(final String stepMsg) {
        System.out.println(banner);
        System.out.println(stepMsg);
        System.out.println(banner);

    }
}
