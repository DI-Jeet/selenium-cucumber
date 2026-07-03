package com.sky.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class SkyLoginPage extends BasePage {

    // Real selectors from https://id.sky.com
    private static final By EMAIL_INPUT    = By.cssSelector("[data-testid='IDENTIFIER_INPUT']");
    private static final By CONTINUE_BTN   = By.cssSelector("[data-testid='IDENTIFIER_SUBMIT']");
    private static final By ERROR_MESSAGE  = By.cssSelector("[data-testid='IDENTIFIER_ERROR'], p[class*='error'], [aria-invalid='true'] ~ p");

    @Step("Enter email/username: {username}")
    public void enterUsername(String username) {
        typeText(EMAIL_INPUT, username);
    }

    @Step("Click Continue")
    public void clickContinue() {
        clickElement(CONTINUE_BTN);
    }

    @Step("Submit invalid credentials")
    public void submitInvalidLogin(String username) {
        enterUsername(username);
        clickContinue();
    }

    @Step("Verify error message is displayed")
    public boolean isErrorMessageDisplayed() {
        // After submitting unknown username, the input gets aria-invalid and an error paragraph appears
        return isVisible(By.cssSelector("[data-testid='IDENTIFIER_INPUT'][aria-invalid='true']"))
            || isVisible(By.cssSelector("p[class*='error']"))
            || isVisible(By.xpath("//p[contains(text(), \"can't find an account\")]"));
    }
}
