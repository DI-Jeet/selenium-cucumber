package com.sky.web.hooks;

import com.sky.web.driver.DriverFactory;
import com.sky.web.driver.DriverManager;
import com.sky.web.utils.ScreenshotUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;

public class Hooks {

    private static final Logger log = LogManager.getLogger(Hooks.class);

    @Before(order = 1)
    public void setUp(Scenario scenario) {
        log.info("Starting scenario: [{}] {}", scenario.getId(), scenario.getName());
        DriverManager.set(DriverFactory.create());
    }

    @After(order = 1)
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            log.warn("Scenario FAILED: {}", scenario.getName());
            byte[] screenshot = ScreenshotUtil.capture();
            // Attach via Allure directly (works alongside allure-cucumber7-jvm adapter)
            Allure.addAttachment("Screenshot on failure", "image/png", new ByteArrayInputStream(screenshot), "png");
            // Also attach to Cucumber report
            scenario.attach(screenshot, "image/png", "Screenshot on failure");
        }
        log.info("Tearing down scenario: {}", scenario.getName());
        DriverManager.quit();
    }
}
