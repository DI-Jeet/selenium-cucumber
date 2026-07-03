Feature: Sky account login and search
  As a visitor
  I want to verify login validation and search functionality on Sky.com

  @regression
  Scenario: Login with invalid credentials and then search for a series
    Given the user opens Sky.com home page
    When the user clicks on the login button
    And the user enters an invalid username and clicks continue
    Then an error message should be displayed
    When the user navigates back to Sky.com home page
    And the user searches for "Suits" in the search bar
    Then search results are visible
