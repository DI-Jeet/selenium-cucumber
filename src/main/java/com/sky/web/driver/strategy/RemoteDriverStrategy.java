package com.sky.web.driver.strategy;

import com.sky.web.config.ConfigReader;
import com.sky.web.driver.BrowserType;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a RemoteWebDriver pointed at Sauce Labs (or any W3C-compatible grid).
 * Credentials are sourced from env vars SAUCE_USERNAME / SAUCE_ACCESS_KEY — never hardcoded.
 */
public class RemoteDriverStrategy implements DriverStrategy {

    @Override
    public WebDriver createDriver(BrowserType browserType) {
        MutableCapabilities caps = buildCapabilities(browserType);
        caps.setCapability("sauce:options", buildSauceOptions());
        try {
            return new RemoteWebDriver(new URL(ConfigReader.getGridUrl()), caps);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid grid URL: " + ConfigReader.getGridUrl(), e);
        }
    }

    private MutableCapabilities buildCapabilities(BrowserType browserType) {
        return switch (browserType) {
            case CHROME -> {
                ChromeOptions opts = new ChromeOptions();
                opts.setPlatformName(ConfigReader.getSaucePlatform());
                opts.setBrowserVersion(ConfigReader.getSauceBrowserVersion());
                yield opts;
            }
            case FIREFOX -> {
                FirefoxOptions opts = new FirefoxOptions();
                opts.setPlatformName(ConfigReader.getSaucePlatform());
                opts.setBrowserVersion(ConfigReader.getSauceBrowserVersion());
                yield opts;
            }
            case EDGE -> {
                EdgeOptions opts = new EdgeOptions();
                opts.setPlatformName(ConfigReader.getSaucePlatform());
                opts.setBrowserVersion(ConfigReader.getSauceBrowserVersion());
                yield opts;
            }
            default -> throw new IllegalArgumentException("Browser not supported on remote grid: " + browserType);
        };
    }

    private Map<String, Object> buildSauceOptions() {
        Map<String, Object> sauceOpts = new HashMap<>();
        sauceOpts.put("username", ConfigReader.getSauceUsername());
        sauceOpts.put("accessKey", ConfigReader.getSauceAccessKey());
        sauceOpts.put("tunnelName", ConfigReader.getSauceTunnelId());
        sauceOpts.put("build", "selenium-cucumber-web-framework");
        sauceOpts.put("name", Thread.currentThread().getName());
        return sauceOpts;
    }
}
