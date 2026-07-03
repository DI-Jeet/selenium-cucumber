package com.sky.web.utils;

import com.sky.web.config.FrameworkConstants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Date helpers for test data generation.
 * Always use future dates to avoid consuming real inventory.
 */
public final class DateUtil {

    public static final DateTimeFormatter MM_DD_YYYY = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtil() {}

    /** Returns today + daysAhead, formatted as MM/dd/yyyy (common UI input format). */
    public static String futureDate(int daysAhead) {
        return LocalDate.now().plusDays(daysAhead).format(MM_DD_YYYY);
    }

    /** Default future check-in date (10 days out per CLAUDE.md rule). */
    public static String defaultCheckIn() {
        return futureDate(FrameworkConstants.DEFAULT_FUTURE_CHECKIN_DAYS);
    }

    /** Default check-out is check-in + 1 night. */
    public static String defaultCheckOut() {
        return futureDate(FrameworkConstants.DEFAULT_FUTURE_CHECKIN_DAYS + 1);
    }

    public static String futureDate(int daysAhead, DateTimeFormatter formatter) {
        return LocalDate.now().plusDays(daysAhead).format(formatter);
    }
}
