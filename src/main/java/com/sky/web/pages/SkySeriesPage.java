package com.sky.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class SkySeriesPage extends BasePage {

  private static final By SERIES_HEADING = By.cssSelector("h1");

    @Step("Get series page heading")
    public String getSeriesHeading() {
        return getText(SERIES_HEADING);
    }

    public String title() {
        return getPageTitle();
    }

    public boolean isOnSeriesPage(String expectedTitle) {
        return title().equalsIgnoreCase(expectedTitle)
            || getSeriesHeading().equalsIgnoreCase(expectedTitle);
    }

  public boolean isOnSRPPage() {
    return isVisible(By.cssSelector("[data-test-id=\"search-heading\"]"));
  }

  @Step("Click on Mobile nav link")
  public void clickOnMobile() {
    clickElement(By.cssSelector("[data-tracking-label=\"masthead_visit_primary_mobile_link\"]"));
  }

  @Step("Click on Tablets & Laptops")
  public void clickOnTabletsAndLaptops() {
    clickElement(By.xpath("//span[normalize-space()='Tablets & Laptops']"));
  }

  @Step("Check brand chips are visible")
  public boolean isBrandsVisible() {
    return isVisible(By.cssSelector("[data-testid=\"brand-chips\"]"));
  }


}
