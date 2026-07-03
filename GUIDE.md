# Selenium Cucumber Web Framework — Team Guide

A step-by-step guide for anyone joining the project: how the framework is structured, how to write a new test from scratch, and how to run tests and open the Allure report locally.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Project Structure](#2-project-structure)
3. [How the Framework Works — Big Picture](#3-how-the-framework-works--big-picture)
4. [Configuration — Environments & Browsers](#4-configuration--environments--browsers)
5. [Writing a New Test — Step-by-Step](#5-writing-a-new-test--step-by-step)
   - [Step 1 — Write the Feature File (Gherkin)](#step-1--write-the-feature-file-gherkin)
   - [Step 2 — Create the Page Object](#step-2--create-the-page-object)
   - [Step 3 — Write the Step Definitions](#step-3--write-the-step-definitions)
   - [Step 4 — Add a Key to ScenarioContext (if needed)](#step-4--add-a-key-to-scenariocontext-if-needed)
6. [Running Tests](#6-running-tests)
7. [Generating and Opening the Allure Report](#7-generating-and-opening-the-allure-report)
8. [Parallel Execution](#8-parallel-execution)
9. [Tagging Strategy](#9-tagging-strategy)
10. [Utilities Reference](#10-utilities-reference)
11. [Common Mistakes to Avoid](#11-common-mistakes-to-avoid)

---

## 1. Prerequisites

Install the following before cloning the project:

| Tool | Version | Install |
|------|---------|---------|
| Java JDK | 21 | `brew install openjdk@21` (Mac) |
| Maven | 3.9+ | `brew install maven` |
| Chrome | Latest | [google.com/chrome](https://www.google.com/chrome) |
| IntelliJ IDEA | Any | [jetbrains.com/idea](https://www.jetbrains.com/idea) |
| Allure CLI (optional) | Latest | `brew install allure` |

**Verify your setup:**
```bash
java -version    # should show 21.x
mvn -version     # should show 3.9+
```

**IntelliJ plugins to install** (File → Settings → Plugins):
- Cucumber for Java
- Gherkin
- Lombok

---

## 2. Project Structure

```
selenium-cucumber-web-framework/
│
├── pom.xml                          # Maven build — all dependencies & plugins
├── testng.xml                       # TestNG suite — controls parallelism (4 threads)
│
├── src/
│   ├── main/java/com/sky/web/
│   │   ├── config/
│   │   │   ├── ConfigLoader.java    # Singleton — reads config/{env}.properties
│   │   │   ├── ConfigReader.java    # Typed accessors (getBaseUrl, getBrowser, etc.)
│   │   │   └── FrameworkConstants.java  # Static constants (paths, formats)
│   │   ├── driver/
│   │   │   ├── BrowserType.java     # Enum: CHROME, FIREFOX, EDGE, SAFARI
│   │   │   ├── DriverFactory.java   # Creates WebDriver (local or remote)
│   │   │   ├── DriverManager.java   # ThreadLocal WebDriver holder (thread-safe)
│   │   │   └── strategy/
│   │   │       ├── DriverStrategy.java        # Interface
│   │   │       ├── LocalDriverStrategy.java   # ChromeDriver/FirefoxDriver locally
│   │   │       └── RemoteDriverStrategy.java  # Selenium Grid / Sauce Labs
│   │   ├── pages/
│   │   │   ├── BasePage.java        # All shared Selenium helpers — EXTEND THIS
│   │   │   ├── SkyHomePage.java     # Example page object
│   │   │   └── SkySeriesPage.java   # Example page object
│   │   ├── services/
│   │   │   └── ApiClient.java       # REST-Assured helper for API pre-conditions
│   │   └── utils/
│   │       ├── DateUtil.java        # Date formatting helpers
│   │       ├── FakerUtil.java       # Random test data (name, email, etc.)
│   │       ├── JavaScriptUtil.java  # JS execution helpers
│   │       ├── ScreenshotUtil.java  # Capture screenshot as byte[]
│   │       └── WaitUtil.java        # Static wait helpers
│   │
│   └── test/
│       ├── java/com/sky/web/
│       │   ├── hooks/
│       │   │   ├── Hooks.java           # @Before (open browser) / @After (close + screenshot)
│       │   │   └── ScenarioContext.java # Per-scenario data store (PicoContainer injected)
│       │   ├── runners/
│       │   │   └── TestNGRunner.java    # Cucumber entry point — DO NOT EDIT unless adding plugins
│       │   └── steps/
│       │       └── SkySteps.java        # Step definitions — ADD YOUR STEPS HERE (or new class)
│       └── resources/
│           ├── allure.properties        # allure.results.directory=target/allure-results
│           ├── log4j2.xml               # Log4j2 logging config
│           ├── config/
│           │   ├── dev.properties       # base.url, browser, timeouts for dev
│           │   ├── qa.properties        # base.url, browser, timeouts for qa (DEFAULT)
│           │   ├── uat.properties
│           │   └── prod.properties
│           ├── features/
│           │   └── home.feature         # ADD YOUR FEATURE FILES HERE
│           └── suites/
│               ├── smoke.xml            # Smoke-only TestNG suite (not wired by default)
│               └── regression.xml       # Regression-only TestNG suite
```

---

## 3. How the Framework Works — Big Picture

Understanding this flow will help you slot your new test in the right place:

```
testng.xml
  └─► TestNGRunner (@CucumberOptions)
        ├─► picks up all .feature files under src/test/resources/features/
        ├─► filters by tag (default: @smoke or @regression)
        └─► for each Scenario:
              ├─► Hooks.@Before  → DriverFactory.create() → DriverManager.set(driver)
              ├─► Step definitions (SkySteps, etc.) → Page Objects → WebDriver
              └─► Hooks.@After   → screenshot on failure → DriverManager.quit()
                                 → writes allure-results JSON to target/allure-results/
```

**Key design rules:**
- `DriverManager.get()` — call this anywhere in a step or page to get the current thread's driver. Never store the driver in a static field.
- `ScenarioContext` — use this to pass data between step definition methods. It is injected by PicoContainer (constructor injection), one fresh instance per scenario.
- `BasePage` — every page object extends this. Use its `clickElement(By)`, `typeText(By, text)`, `waitForVisible(By)`, `getText(By)` methods. They have built-in waits and retry on stale elements.
- `@Step("description")` — put this on page object methods to get step-level detail in the Allure report.

---

## 4. Configuration — Environments & Browsers

Config files live at `src/test/resources/config/{env}.properties`.

**`qa.properties` (the default):**
```properties
base.url=https://sky.com
browser=chrome
execution.mode=local
implicit.wait.seconds=10
explicit.wait.seconds=20
page.load.timeout.seconds=60
```

**Priority order** (highest wins):
1. JVM system property: `-Dbase.url=https://staging.sky.com`
2. OS environment variable: `BASE_URL=https://staging.sky.com`
3. The `.properties` file for the active env

**Switching environment or browser at runtime:**
```bash
mvn clean test -Denv=uat
mvn clean test -Denv=qa -Dbrowser=firefox
mvn clean test -Dbrowser=edge
```

**Supported browsers** (defined in `BrowserType.java`): `chrome`, `firefox`, `edge`, `safari`

---

## 5. Writing a New Test — Step-by-Step

Let's say you want to add a test: *"User navigates to the Sky Movies page and verifies the banner title"*.

### Step 1 — Write the Feature File (Gherkin)

Create a new file under `src/test/resources/features/` — one file per feature area.

**`src/test/resources/features/movies.feature`:**
```gherkin
Feature: Sky Movies page
  As a visitor
  I want to navigate to the Sky Movies page
  So that I can see the featured content

  @smoke
  Scenario: Movies page banner displays a title
    Given the user opens Sky.com home page
    When the user navigates to the Movies section
    Then the movies banner title is displayed
```

**Rules:**
- Tag every scenario with at least one of `@smoke` or `@regression` — otherwise the default tag filter (`@smoke or @regression`) will skip it.
- Use `Given` for preconditions (state setup), `When` for actions, `Then` for assertions.
- Reuse existing step text exactly (copy-paste from `home.feature` or `SkySteps.java`) to avoid duplicate step definitions.

**Parameterised steps** — use `{string}` in the step text to pass values:
```gherkin
When the user searches for "peaky blinders" in the search bar
```
This matches the existing step `@When("the user searches for {string} in the search bar")` — no new code needed.

---

### Step 2 — Create the Page Object

One class per page or major UI component. Place it in `src/main/java/com/sky/web/pages/`.

**`src/main/java/com/sky/web/pages/SkyMoviesPage.java`:**
```java
package com.sky.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SkyMoviesPage extends BasePage {

    // Always use private static final By for locators — never inline
    private static final By BANNER_TITLE = By.cssSelector("[data-test-id='hero-banner-title']");

    public SkyMoviesPage(WebDriver driver) {
        super(driver);   // REQUIRED — initialises waits and PageFactory
    }

    @Step("Get banner title text")
    public String getBannerTitle() {
        return getText(BANNER_TITLE);   // inherited from BasePage — waits for visibility
    }

    public boolean isBannerTitleDisplayed() {
        return isVisible(BANNER_TITLE); // inherited from BasePage — returns true/false
    }
}
```

**Page Object rules:**
- Always call `super(driver)` in the constructor.
- Declare all locators as `private static final By` constants at the top of the class.
- Use `By.cssSelector` or `By.xpath` — prefer CSS selectors.
- Use `@Step("description")` on public methods — these appear as nested steps in the Allure report.
- Use `BasePage` methods (`clickElement`, `typeText`, `getText`, `waitForVisible`, etc.) — they have built-in retry logic. Do not call `driver.findElement()` directly unless unavoidable.
- Page object methods return `void`. Instantiate the next page object in the step definition when navigation happens.

**BasePage methods you can use directly:**

| Method | What it does |
|--------|-------------|
| `clickElement(By)` | Waits for clickability, clicks, retries on stale/missing |
| `typeText(By, text)` | Waits for enabled, clears field, types text |
| `waitForVisible(By)` | Waits until visible, returns the `WebElement` |
| `waitForClickable(By)` | Waits until clickable, returns the `WebElement` |
| `getText(By)` | Waits for visibility, returns trimmed text |
| `isVisible(By)` | Returns `true`/`false` — never throws |
| `jsClick(By)` | Clicks via JavaScript (use when element has zero size) |
| `switchToNewTab()` | Waits for new tab to open, switches to it |
| `waitForAttributeContains(By, attr, val)` | Waits until an attribute contains a value |
| `navigateTo(url)` | Navigates to URL |
| `getPageTitle()` | Returns current page title |

---

### Step 3 — Write the Step Definitions

Either add methods to the existing `SkySteps.java` (for sky.com tests) or create a new step class for a different feature area.

**Adding to existing `SkySteps.java`** (for steps that reuse `SkyHomePage`):

Find `src/test/java/com/sky/web/steps/SkySteps.java` and add:

```java
@When("the user navigates to the Movies section")
public void theUserNavigatesToMoviesSection() {
    skyHomePage.clickMoviesLink();  // void — add this method to SkyHomePage
    skyMoviesPage = new SkyMoviesPage(DriverManager.get());
}

@Then("the movies banner title is displayed")
public void theMoviesBannerTitleIsDisplayed() {
    Assert.assertTrue(
        skyMoviesPage.isBannerTitleDisplayed(),
        "Expected movies banner title to be visible"
    );
}
```

Also add `private SkyMoviesPage skyMoviesPage;` as a field at the top of `SkySteps`.

**Creating a new step class** (when the feature area is completely separate):

```java
package com.sky.web.steps;

import com.sky.web.driver.DriverManager;
import com.sky.web.hooks.ScenarioContext;
import com.sky.web.pages.SkyMoviesPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class MoviesSteps {

    private final ScenarioContext ctx;       // PicoContainer injects this
    private SkyMoviesPage skyMoviesPage;

    public MoviesSteps(ScenarioContext ctx) { // constructor injection — REQUIRED pattern
        this.ctx = ctx;
    }

    @When("the user navigates to the Movies section")
    public void theUserNavigatesToMoviesSection() {
        skyMoviesPage = new SkyMoviesPage(DriverManager.get());
        // add navigation logic
    }

    @Then("the movies banner title is displayed")
    public void theMoviesBannerTitleIsDisplayed() {
        Assert.assertTrue(skyMoviesPage.isBannerTitleDisplayed());
    }
}
```

**Step definition rules:**
- The package must be `com.sky.web.steps` — that's the glue package in `TestNGRunner.java`.
- Always inject `ScenarioContext` via constructor — PicoContainer creates one instance per scenario and shares it across all step classes.
- Use `DriverManager.get()` to get the WebDriver — never store it as a field (thread safety).
- Use `org.testng.Assert` for assertions, not `System.out.println`.
- Step text in annotations must exactly match the Gherkin step text (case-sensitive).

---

### Step 4 — Add a Key to ScenarioContext (if needed)

If your steps need to share data (e.g. a search term entered in one step is asserted in another), add an enum key to `ScenarioContext`.

**`src/test/java/com/sky/web/hooks/ScenarioContext.java`** — add your key to the `Key` enum:

```java
public enum Key {
    SEARCH_TERM,
    SERIES_TITLE,
    BANNER_TITLE    // <-- add here
}
```

Then in your step:
```java
// Store
ctx.set(ScenarioContext.Key.BANNER_TITLE, skyMoviesPage.getBannerTitle());

// Retrieve in another step
String title = ctx.get(ScenarioContext.Key.BANNER_TITLE);
```

---

## 6. Running Tests

All commands run from the project root: `/path/to/selenium-cucumber-web-framework`

**Run all default tests (QA env, Chrome, @smoke or @regression):**
```bash
mvn clean test
```

**Run only smoke tests:**
```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
```

**Run only regression tests:**
```bash
mvn clean test -Dcucumber.filter.tags="@regression"
```

**Run a specific tag:**
```bash
mvn clean test -Dcucumber.filter.tags="@movies"
```

**Run with a different environment:**
```bash
mvn clean test -Denv=uat
mvn clean test -Denv=dev
```

**Run with a different browser:**
```bash
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
```

**Combine options:**
```bash
mvn clean test -Denv=qa -Dbrowser=firefox -Dcucumber.filter.tags="@smoke"
```

**Run in headed (visible) browser — the default for `execution.mode=local`:**
Already the default. No extra flag needed.

**Run against Selenium Grid (remote):**
```bash
mvn clean test -Dexecution.mode=remote -Dgrid.url=http://localhost:4444
```

> Always use `mvn clean test` (not just `mvn test`) to clear results from previous runs before generating a fresh Allure report.

---

## 7. Generating and Opening the Allure Report

After `mvn clean test` finishes, JSON result files are written to `target/allure-results/`.

### Option A — Maven plugin (no extra install needed)

**Open in browser immediately (temp server):**
```bash
mvn allure:serve
```
A browser tab opens automatically. The server runs until you press `Ctrl+C`.

**Generate a static HTML report:**
```bash
mvn allure:report
open target/site/allure-maven-plugin/index.html
```

### Option B — Allure CLI (faster, recommended for daily use)

Install once:
```bash
brew install allure
```

Then after each test run:
```bash
allure serve target/allure-results
```

Or generate a persistent report folder:
```bash
allure generate target/allure-results -o target/allure-report --clean
open target/allure-report/index.html
```

### What you will see in the report

| Tab | What's shown |
|-----|-------------|
| **Overview** | Pass/fail pie chart, environment info, top failures |
| **Suites** | Tests grouped by TestNG suite → test → scenario |
| **Behaviors** | Tests grouped by Feature → Scenario (Cucumber view) — use this most |
| **Graphs** | Trend over time, severity breakdown |
| **Timeline** | Which scenarios ran on which thread, when |

**Inside a scenario** you will see:
- Each `@Step`-annotated method from your page objects as a nested step
- Screenshots automatically attached on failure (from `Hooks.java`)
- Full stack trace on failure

---

## 8. Parallel Execution

The framework runs scenarios in parallel out of the box via `testng.xml`:

```xml
<suite parallel="methods" thread-count="4">
```

`TestNGRunner.scenarios()` has `@DataProvider(parallel = true)` which feeds each scenario as a separate TestNG method — this is what makes scenario-level parallelism work.

**Thread safety is handled for you:**
- `DriverManager` uses `ThreadLocal` — each thread gets its own `WebDriver`.
- `ScenarioContext` is scoped per scenario by PicoContainer — no shared state between scenarios.
- Never use `static` fields to share state between step classes.

**Changing thread count:**
Edit `testng.xml` and change `thread-count="4"` to your desired value.

---

## 9. Tagging Strategy

Tags are defined on `Scenario` (or `Feature`) lines in `.feature` files with `@`.

| Tag | Purpose | How to run |
|-----|---------|------------|
| `@smoke` | Fast, critical path tests | `-Dcucumber.filter.tags="@smoke"` |
| `@regression` | Full regression suite | `-Dcucumber.filter.tags="@regression"` |
| `@wip` | Work in progress — exclude from CI | `-Dcucumber.filter.tags="not @wip"` |

**Combining tags:**
```bash
# Run smoke AND regression
-Dcucumber.filter.tags="@smoke or @regression"

# Run smoke but NOT wip
-Dcucumber.filter.tags="@smoke and not @wip"
```

**Default** (set in `pom.xml` profiles): `@smoke or @regression`

---

## 10. Utilities Reference

Ready-to-use utilities in `src/main/java/com/sky/web/utils/`:

**`FakerUtil`** — random test data:
```java
String name  = FakerUtil.randomFullName();
String email = FakerUtil.randomEmail();
```

**`DateUtil`** — date formatting:
```java
String today = DateUtil.today("dd/MM/yyyy");
```

**`WaitUtil`** — static waits (use sparingly — prefer BasePage's fluent waits):
```java
WaitUtil.hardWait(500);  // milliseconds
```

**`JavaScriptUtil`** — JS execution:
```java
JavaScriptUtil.scrollIntoView(driver, element);
JavaScriptUtil.highlightElement(driver, element);
```

**`ScreenshotUtil`** — capture screenshot as byte array (already wired in Hooks):
```java
byte[] bytes = ScreenshotUtil.capture();
```

**`ApiClient`** — REST-Assured wrapper for API pre-conditions in tests:
```java
ApiClient.get("/api/endpoint").then().statusCode(200);
```

---

## 11. Common Mistakes to Avoid

**Do not call `driver.findElement()` directly in step definitions.**
Use page objects and `BasePage` methods — they include waits and retry logic.

**Do not store `WebDriver` in a static field.**
Always use `DriverManager.get()`. Static driver fields break parallel execution.

**Do not use `Thread.sleep()` for waits.**
Use `BasePage.waitForVisible(By)`, `waitForClickable(By)`, or `waitForAttributeContains()`. Hard sleeps are slow and fragile.

**Do not forget `mvn clean` before generating a report.**
Running `mvn test` without `clean` merges old result files into the new report. Always use `mvn clean test`.

**Do not create step definition classes without constructor-injecting `ScenarioContext`.**
PicoContainer needs the constructor signature to inject dependencies. A no-arg constructor will break context sharing between step classes.

**Do not add steps to the wrong glue package.**
All step classes must be in `com.sky.web.steps` or `com.sky.web.hooks` — these are the only glue packages in `TestNGRunner.java`. Steps in any other package are silently ignored.

**Do not tag a scenario with neither `@smoke` nor `@regression`.**
The default tag filter (`@smoke or @regression`) will skip it. If it's genuinely work-in-progress, tag it `@wip` and run with `not @wip` in CI.

---

## Quick-Start Checklist for a New Test

```
[ ] 1. Create src/test/resources/features/yourfeature.feature
        — add @smoke or @regression tag to every scenario
[ ] 2. Create src/main/java/com/sky/web/pages/YourPage.java
        — extends BasePage, constructor calls super(driver)
        — locators as private static final By constants
        — public methods annotated with @Step
[ ] 3. Add step definitions
        — either new class in com.sky.web.steps, or add to SkySteps.java
        — inject ScenarioContext via constructor
        — use DriverManager.get() to get the driver
[ ] 4. Add enum keys to ScenarioContext.Key if sharing data between steps
[ ] 5. Run:  mvn clean test -Dcucumber.filter.tags="@smoke"
[ ] 6. View report:  mvn allure:serve
```
