package com.pofa.ebcadmin.utils;

import org.testng.annotations.Test;

public class Convert {
    public static String camelToUnderScore(String camel) {
        var underscore = new StringBuilder();
        for (char c : camel.toCharArray()) {
            underscore.append(Character.isLowerCase(c) ? String.valueOf(c) : "_" + Character.toLowerCase(c));
        }
        return underscore.toString();
    }

    public static String underScoreToCamel(String underScore) {
        var builder = new StringBuilder();
        var firstFound = false;
        var capitalizeNext = false;
        for (char c : underScore.toCharArray()) {
            if (c == '_') {
                if (firstFound) capitalizeNext = true;
            } else {
                firstFound = true;
                char appendChar = capitalizeNext ? Character.toUpperCase(c) : c;
                builder.append(appendChar);
                capitalizeNext = false;
            }
        }
        return builder.toString();
    }
}
