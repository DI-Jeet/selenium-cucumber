package com.sky.web.steps;

import com.sky.web.hooks.ScenarioContext;
import com.sky.web.pages.SkyHomePage;
import com.sky.web.pages.SkySeriesPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class SkySteps {

  @Given("the user opens Sky.com home page")
  public void theUserOpensSkyHomePage() {
    new SkyHomePage().open();
  }

  @When("the user searches for {string} in the search bar")
  public void theUserSearchesFor(String searchTerm) {
    new ScenarioContext().set(ScenarioContext.Key.SEARCH_TERM, searchTerm);
    new SkyHomePage().openSearchBar();
    new SkyHomePage().typeSearchTerm(searchTerm);
  }

  @And("the user selects {string} from the search suggestions")
  public void theUserSelectsFromSuggestions(String suggestionText) {
    new ScenarioContext().set(ScenarioContext.Key.SERIES_TITLE, suggestionText);
    new SkyHomePage().selectSuggestion(suggestionText);
  }

  @Then("the user is navigated to the {string} series page")
  public void theUserIsNavigatedToTheSuitsSeriesPage(String titleToCheck) {
    Assert.assertTrue(new SkySeriesPage().isOnSeriesPage(titleToCheck),
        "Expected series page for '" + titleToCheck + "' but got title: " + new SkySeriesPage().title() + " / heading: "
            + new SkySeriesPage().getSeriesHeading());
  }

  @When("the user on search result page")
  public void verifyTheUserIsOnSRP() {
    Assert.assertTrue(new SkySeriesPage().isOnSRPPage());
  }

  @When("user navigate to tablets and laptops")
  public void userNavigateToTabletsAndLaptops() {
    new SkySeriesPage().clickOnMobile();
    new SkySeriesPage().clickOnTabletsAndLaptops();
  }

  @Then("verify different brands visible")
  public void isBrandsVisible() {
    Assert.assertTrue(new SkySeriesPage().isBrandsVisible(),
        "Expected brand chips to be visible on Tablets & Laptops page");
  }
}
