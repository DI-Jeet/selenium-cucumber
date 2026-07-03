package com.sky.web.steps;

import com.sky.web.constants.UserData;
import com.sky.web.pages.SkyHomePage;
import com.sky.web.pages.SkyLoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class LoginSteps {

    @When("the user clicks on the login button")
    public void theUserClicksOnTheLoginButton() {
        new SkyHomePage().clickLoginButton();
    }

    @And("the user enters an invalid username and clicks continue")
    public void theUserEntersInvalidUsernameAndClicksContinue() {
        new SkyLoginPage().submitInvalidLogin(UserData.INVALID_USER.getUsername());
    }

    @Then("an error message should be displayed")
    public void anErrorMessageShouldBeDisplayed() {
        Assert.assertTrue(
            new SkyLoginPage().isErrorMessageDisplayed(),
            "Expected a validation error for invalid credentials but none was displayed"
        );
    }

    @When("the user navigates back to Sky.com home page")
    public void theUserNavigatesBackToSkyHomePage() {
        new SkyHomePage().open();
    }

    @Then("search results are visible")
    public void searchResultsAreVisible() {
        Assert.assertTrue(
            new SkyHomePage().isSearchResultsVisible(),
            "Expected search suggestions to appear but none were visible"
        );
    }
}
