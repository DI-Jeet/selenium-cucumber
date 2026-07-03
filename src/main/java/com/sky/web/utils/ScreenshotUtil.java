package com.sky.web.utils;

import com.sky.web.driver.DriverManager;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/** Captures screenshots and attaches them to Allure reports. */
public final class ScreenshotUtil {

    private ScreenshotUtil() {}

    @Attachment(value = "Screenshot on failure", type = "image/png")
    public static byte[] captureAndAttach() {
        return ((TakesScreenshot) DriverManager.get()).getScreenshotAs(OutputType.BYTES);
    }

    /** Returns raw bytes without Allure attachment — useful for custom attachment naming. */
    public static byte[] capture() {
        return ((TakesScreenshot) DriverManager.get()).getScreenshotAs(OutputType.BYTES);
    }
}
