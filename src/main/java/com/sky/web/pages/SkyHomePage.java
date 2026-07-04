package com.sky.web.pages;

import com.sky.web.config.ConfigReader;
import com.sky.web.driver.DriverManager;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class SkyHomePage extends BasePage {

  private static final By SEARCH_TOGGLE  = By.cssSelector("[data-test-id='masthead-search-toggle-button']");
  private static final By SEARCH_INPUT   = By.cssSelector("[data-test-id='input-box']");
  private static final By SUGGESTIONS    = By.cssSelector("[role='list'][aria-label='Section containing links to TV, Films & Series']");
  private static final By NO_RESULTS     = By.xpath("//*[contains(text(),'no results')]");
  private static final By LOGIN_BUTTON   = By.cssSelector("[data-test-id='sign-in-link']");
  private static final By ACCEPT_ALL = By.cssSelector("[title='Accept all']");

  @Step("Open Sky.com home page")
  public void open() {
      navigateTo(ConfigReader.getBaseUrl());
      DriverManager.get().switchTo().frame(2);
      clickElement(ACCEPT_ALL);
      DriverManager.get().switchTo().defaultContent();
  }

  @Step("Click Login / Sign In button on home page")
  public void clickLoginButton() {
      clickElement(LOGIN_BUTTON);
  }

    @Step("Open search bar")
    public void openSearchBar() {
        clickElement(SEARCH_TOGGLE);
        // aria-expanded becomes "true" only after Sky's JS wires the panel
        waitForAttributeContains(SEARCH_TOGGLE, "aria-expanded", "true");
        waitForVisible(SEARCH_INPUT);
    }

    @Step("Type '{searchTerm}' into search bar and wait for suggestions")
    public void typeSearchTerm(String searchTerm) {
        typeText(SEARCH_INPUT, searchTerm);
        // Wait for either suggestions list or "no results" message — whichever Sky renders
        waitForEither(SUGGESTIONS, NO_RESULTS);
    }

    @Step("Search for series '{seriesName}' and open its page")
    public void searchSeries(String seriesName) {
        openSearchBar();
        typeSearchTerm(seriesName);
        selectSuggestion(seriesName);
    }

    @Step("Verify search panel responded (suggestions or no-results message)")
    public boolean isSearchResultsVisible() {
        return isVisible(SUGGESTIONS) || isVisible(NO_RESULTS);
    }

    @Step("Select suggestion '{suggestionText}' from search results")
    public void selectSuggestion(String suggestionText) {
        // Sky's suggestion <a> elements have zero height — use data-tracking-text attribute
        // and JS click to bypass the zero-size constraint
        By suggestionLocator = By.cssSelector(
            "[role='list'][aria-label='Section containing links to TV, Films & Series']" +
            " a[data-tracking-text='" + suggestionText + "']"
        );
        jsClick(suggestionLocator);
        switchToNewTab();
    }
}
