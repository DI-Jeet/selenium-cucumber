package com.sky.web.utils;

import com.sky.web.driver.DriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

/** JavaScript execution helpers for edge cases where WebDriver clicks are unreliable. */
public final class JavaScriptUtil {

    private JavaScriptUtil() {}

    private static JavascriptExecutor js() {
        return (JavascriptExecutor) DriverManager.get();
    }

    public static void click(WebElement element) {
        js().executeScript("arguments[0].click();", element);
    }

    public static void scrollIntoView(WebElement element) {
        js().executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public static void highlightElement(WebElement element) {
        js().executeScript("arguments[0].style.border='3px solid red';", element);
    }

    public static Object executeScript(String script, Object... args) {
        return js().executeScript(script, args);
    }
}
