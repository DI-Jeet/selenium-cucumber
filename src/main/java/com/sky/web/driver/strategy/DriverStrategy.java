package com.sky.web.driver.strategy;

import com.sky.web.driver.BrowserType;
import org.openqa.selenium.WebDriver;

/** Strategy contract for creating a WebDriver instance. */
public interface DriverStrategy {
    WebDriver createDriver(BrowserType browserType);
}
