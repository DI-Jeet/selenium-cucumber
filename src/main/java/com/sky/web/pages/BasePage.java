package com.sky.web.pages;

import com.sky.web.driver.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.sky.web.config.ConfigReader;

import java.time.Duration;

/**
 * Base class for all page objects.
 *
 * By-based methods (preferred):
 *   clickElement(By)                       — waits for clickability, retries on stale/missing
 *   typeText(By, text)                     — waits for enabled, clears, types
 *   waitForVisible(By)                     — waits until visible, returns element
 *   waitForClickable(By)                   — waits until clickable, returns element
 *   waitForAttributeContains(By, attr, val)— waits until attribute has expected value
 *   getText(By)                            — waits for visibility, returns trimmed text
 *   isVisible(By)                          — returns true/false, never throws
 *   switchToNewTab()                       — waits for new tab, switches to it
 *
 * Element-based methods (backward compat for @FindBy fields):
 *   click(WebElement), type(WebElement, text), getText(WebElement), isDisplayed(WebElement)
 */
public abstract class BasePage {

    protected final WebDriverWait wait;
    private final FluentWait<WebDriver> fluentWait;

    protected BasePage() {
        int timeoutSecs = ConfigReader.getExplicitWait();
        this.wait = new WebDriverWait(DriverManager.get(), Duration.ofSeconds(timeoutSecs));
        this.fluentWait = new FluentWait<>(DriverManager.get())
                .withTimeout(Duration.ofSeconds(timeoutSecs))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementNotInteractableException.class);
    }

    // ── By-based methods (preferred) ────────────────────────────────────────

    /**
     * Waits until the element is clickable (visible + enabled + JS handlers attached),
     * then clicks it. Retries on StaleElementReferenceException and NoSuchElementException.
     */
    @Step("Click element: {locator}")
    protected void clickElement(By locator) {
        fluentWait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * Waits until the element is visible and enabled, clears it, then types the text.
     * Retries on stale/missing/not-interactable element.
     */
    @Step("Type '{text}' into: {locator}")
    protected void typeText(By locator, String text) {
        fluentWait.until(d -> {
            WebElement el = d.findElement(locator);
            if (!el.isDisplayed() || !el.isEnabled()) return null;
            el.clear();
            el.sendKeys(text);
            return true;
        });
    }

    /** Waits until the element is visible and returns it. */
    protected WebElement waitForVisible(By locator) {
        return fluentWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits until the element is clickable and returns it. */
    protected WebElement waitForClickable(By locator) {
        return fluentWait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits until an element's attribute contains the expected value.
     * Useful for confirming JS-driven state changes (e.g. aria-expanded="true").
     */
    protected void waitForAttributeContains(By locator, String attribute, String value) {
        fluentWait.until(ExpectedConditions.attributeContains(locator, attribute, value));
    }

    /** Waits until element is visible and returns its trimmed text. */
    protected String getText(By locator) {
        return fluentWait
                .until(ExpectedConditions.visibilityOfElementLocated(locator))
                .getText()
                .trim();
    }

    /** Waits until at least one of the two locators becomes visible. */
    protected void waitForEither(By first, By second) {
        fluentWait.until(d -> {
            try { if (d.findElement(first).isDisplayed()) return true; } catch (Exception ignored) {}
            try { if (d.findElement(second).isDisplayed()) return true; } catch (Exception ignored) {}
            return null;
        });
    }

    /** Returns true if element becomes visible within timeout; false otherwise. Never throws. */
    protected boolean isVisible(By locator) {
        try {
            fluentWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Finds an element and clicks it via JavaScript.
     * Use when the element is present but has zero size so a native click is rejected.
     */
    @Step("JS-click element: {locator}")
    protected void jsClick(By locator) {
        WebElement el = fluentWait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) DriverManager.get()).executeScript("arguments[0].click();", el);
    }

    /** Waits for a second window handle, then switches to it. */
    protected void switchToNewTab() {
        String original = DriverManager.get().getWindowHandle();
        fluentWait.until(d -> d.getWindowHandles().size() > 1);
        for (String handle : DriverManager.get().getWindowHandles()) {
            if (!handle.equals(original)) {
              DriverManager.get().switchTo().window(handle);
                return;
            }
        }
    }

    // ── Element-based methods (backward compat for @FindBy fields) ───────────

    @Step("Click element")
    protected void click(WebElement element) {
        fluentWait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    @Step("Type '{text}' into element")
    protected void type(WebElement element, String text) {
        WebElement el = fluentWait.until(ExpectedConditions.visibilityOf(element));
        el.clear();
        el.sendKeys(text);
    }

    protected String getText(WebElement element) {
        return fluentWait.until(ExpectedConditions.visibilityOf(element)).getText().trim();
    }

    protected boolean isDisplayed(WebElement element) {
        try {
            return fluentWait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    protected boolean isDisplayed(By locator) {
        return isVisible(locator);
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    @Step("Navigate to: {url}")
    protected void navigateTo(String url) {
        try {
          DriverManager.get().get(url);
        } catch (TimeoutException e) {
            // Page-load timeout fired before the load event — DOM is still usable.
            // Heavy third-party scripts on sites like sky.com can delay the load event
            // beyond our timeout while the DOM itself is fully interactive.
        }
    }

    protected String getPageTitle() {
        return DriverManager.get().getTitle();
    }
}
