Feature: Sky.com search
  As a visitor
  I want to search for a TV series on Sky.com
  So that I can navigate to its series page


 @smoke
  Scenario: Search for Suits webseries and navigate to its series page
    Given the user opens Sky.com home page
    When the user searches for "Suits" in the search bar
    And the user selects "Suits" from the search suggestions
    Then the user is navigated to the "Suits" series page

  @smoke
  Scenario: Search for Suits webseries and navigate to its series page(negative)
    Given the user opens Sky.com home page
    When the user searches for "Suits" in the search bar
    And the user selects "Suits" from the search suggestions
    Then the user is navigated to the "Bang" series page

  @smoke
  Scenario: Verify Different brands are visible in Phone and tablets
    Given the user opens Sky.com home page
    When user navigate to tablets and laptops
    Then verify different brands visible

