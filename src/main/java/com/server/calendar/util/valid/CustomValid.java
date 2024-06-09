package com.server.calendar.util.valid;

import java.util.regex.Pattern;

public class CustomValid {

    static String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    static String phoneRegex = "^010[0-9]{8}$";
    static String userIdRegex = "^[a-zA-Z0-9]+$";

    public static boolean isEmailValid(String email) {
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    public static boolean isPhoneValid(String phone) {
        return Pattern.compile(phoneRegex).matcher(phone).matches();
    }

    public static boolean isUserIdValid(String userId) {
        return Pattern.compile(userIdRegex).matcher(userId).matches();
    }

}
