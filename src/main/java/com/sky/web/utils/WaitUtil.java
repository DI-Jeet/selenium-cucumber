package com.sky.web.utils;

import com.sky.web.config.ConfigReader;
import com.sky.web.driver.DriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Convenience wait factory for use in step definitions or helpers when not inside a page object. */
public final class WaitUtil {

    private WaitUtil() {}

    public static WebDriverWait standardWait() {
        return new WebDriverWait(DriverManager.get(), Duration.ofSeconds(ConfigReader.getExplicitWait()));
    }

    public static WebElement waitForVisibility(By locator) {
        return standardWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(By locator) {
        return standardWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Polls every 500 ms, ignores NoSuchElementException, up to maxWaitSeconds. */
    public static FluentWait<org.openqa.selenium.WebDriver> fluentWait(int maxWaitSeconds) {
        return new FluentWait<>(DriverManager.get())
                .withTimeout(Duration.ofSeconds(maxWaitSeconds))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(org.openqa.selenium.NoSuchElementException.class);
    }
}
