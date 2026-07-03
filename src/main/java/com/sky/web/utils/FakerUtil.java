package com.sky.web.utils;

import com.github.javafaker.Faker;

import java.util.Locale;

/** Test data generation using JavaFaker. One shared Faker instance per class-load. */
public final class FakerUtil {

    private static final Faker FAKER = new Faker(Locale.US);

    private FakerUtil() {}

    public static String randomFirstName() {
        return FAKER.name().firstName();
    }

    public static String randomLastName() {
        return FAKER.name().lastName();
    }

    public static String randomEmail() {
        return FAKER.internet().emailAddress();
    }

    public static String randomPhoneNumber() {
        return FAKER.phoneNumber().phoneNumber();
    }

    public static String randomCity() {
        return FAKER.address().city();
    }

    public static String randomZipCode() {
        return FAKER.address().zipCode();
    }
}
