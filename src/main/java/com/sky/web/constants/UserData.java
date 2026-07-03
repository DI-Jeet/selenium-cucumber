package com.sky.web.constants;

import com.sky.web.model.User;
import com.sky.web.utils.UserReader;

/** User credentials loaded from src/test/resources/config/users.json */
public final class UserData {

    private UserData() {}

    public static final User STANDARD_USER  = UserReader.getUser("standard_user");
    public static final User ADMIN_USER     = UserReader.getUser("admin_user");
    public static final User PREMIUM_USER   = UserReader.getUser("premium_user");
    public static final User INVALID_USER   = UserReader.getUser("invalid_user");
}
