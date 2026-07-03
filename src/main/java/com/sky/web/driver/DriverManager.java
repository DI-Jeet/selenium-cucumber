package com.sky.web.driver;

import org.openqa.selenium.WebDriver;

/**
 * Thread-safe WebDriver holder using ThreadLocal.
 * One driver per Cucumber scenario (each scenario runs on one TestNG thread).
 */
public class DriverManager {

    private static final ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();

    private DriverManager() {}

    public static void set(WebDriver driver) {
        driverHolder.set(driver);
    }

    public static WebDriver get() {
        WebDriver driver = driverHolder.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver has not been initialised for this thread. Check your @Before hook.");
        }
        return driver;
    }

    public static void quit() {
        WebDriver driver = driverHolder.get();
        if (driver != null) {
            driver.quit();
            driverHolder.remove();
        }
    }
}
