package com.sky.web.driver;

import com.sky.web.config.ConfigReader;
import com.sky.web.driver.strategy.DriverStrategy;
import com.sky.web.driver.strategy.LocalDriverStrategy;
import com.sky.web.driver.strategy.RemoteDriverStrategy;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

/**
 * Creates a configured WebDriver using the strategy selected by execution.mode config.
 * Call DriverFactory.create() from Cucumber @Before hook; store result in DriverManager.
 */
public class DriverFactory {

    private DriverFactory() {}

    public static WebDriver create() {
        BrowserType browserType = BrowserType.fromString(ConfigReader.getBrowser());
        DriverStrategy strategy = "remote".equalsIgnoreCase(ConfigReader.getExecutionMode())
                ? new RemoteDriverStrategy()
                : new LocalDriverStrategy();

        WebDriver driver = strategy.createDriver(browserType);
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()))
                .pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        driver.manage().window().maximize();
        return driver;
    }
}
