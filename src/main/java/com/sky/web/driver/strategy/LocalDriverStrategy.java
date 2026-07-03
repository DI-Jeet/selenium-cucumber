package com.sky.web.driver.strategy;

import com.sky.web.driver.BrowserType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

/**
 * Creates a local WebDriver instance using Selenium Manager (Selenium 4.6+).
 * No manual driver binary downloads required.
 * Uses NORMAL page-load strategy (default) so JS frameworks finish initialising
 * before Selenium proceeds — required for SPA sites like sky.com.
 */
public class LocalDriverStrategy implements DriverStrategy {

    @Override
    public WebDriver createDriver(BrowserType browserType) {
        return switch (browserType) {
            case CHROME -> {
                ChromeOptions opts = new ChromeOptions();
                opts.addArguments(
                    "--start-maximized",
                    "--disable-notifications",
                    "--disable-popup-blocking",
                    "--remote-allow-origins=*"
                );
                yield new ChromeDriver(opts);
            }
            case FIREFOX -> {
                FirefoxOptions opts = new FirefoxOptions();
                yield new FirefoxDriver(opts);
            }
            case EDGE -> {
                EdgeOptions opts = new EdgeOptions();
                opts.addArguments("--start-maximized");
                yield new EdgeDriver(opts);
            }
            case SAFARI -> new SafariDriver();
        };
    }
}
