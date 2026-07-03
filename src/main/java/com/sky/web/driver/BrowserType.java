package com.sky.web.driver;

public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE,
    SAFARI;

    public static BrowserType fromString(String value) {
        return switch (value.trim().toLowerCase()) {
            case "chrome" -> CHROME;
            case "firefox" -> FIREFOX;
            case "edge" -> EDGE;
            case "safari" -> SAFARI;
            default -> throw new IllegalArgumentException("Unsupported browser: " + value);
        };
    }
}
