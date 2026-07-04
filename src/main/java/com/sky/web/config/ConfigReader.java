package com.sky.web.config;

/**
 * Typed accessors for all framework config keys.
 * Use this instead of raw ConfigLoader.get() calls in production code.
 */
public class ConfigReader {

    private static final ConfigLoader loader = ConfigLoader.getInstance();

    private ConfigReader() {}

    public static String getBaseUrl() {
        return loader.get("base.url");
    }

    public static String getBrowser() {
        return loader.get("browser", "chrome");
    }

    public static String getExecutionMode() {
        return loader.get("execution.mode", "local");
    }

    public static int getImplicitWait() {
        return loader.getInt("implicit.wait.seconds", 10);
    }

    public static int getExplicitWait() {
        return loader.getInt("explicit.wait.seconds", 60);
    }

    public static int getPageLoadTimeout() {
        return loader.getInt("page.load.timeout.seconds", 30);
    }

    public static String getGridUrl() {
        return loader.get("grid.url");
    }

    public static String getSauceUsername() {
        return loader.get("sauce.username");
    }

    public static String getSauceAccessKey() {
        return loader.get("sauce.access.key");
    }

    public static String getSaucePlatform() {
        return loader.get("sauce.platform.name", "Windows 10");
    }

    public static String getSauceBrowserVersion() {
        return loader.get("sauce.browser.version", "latest");
    }

    public static String getSauceTunnelId() {
        return loader.get("sauce.tunnel.identifier");
    }
}
