package com.sky.web.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.sky.web.steps",
                "com.sky.web.hooks"
        },
        plugin = {
                "pretty",
                "json:target/cucumber-reports/cucumber.json",
                "html:target/cucumber-reports/cucumber.html",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        tags = "${cucumber.filter.tags}"
)
public class TestNGRunner extends AbstractTestNGCucumberTests {

    /**
     * Enables scenario-level parallel execution via TestNG.
     * Thread count is controlled by testng.xml (parallel="methods" thread-count="N").
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
