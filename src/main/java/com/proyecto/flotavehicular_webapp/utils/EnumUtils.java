package com.proyecto.flotavehicular_webapp.utils;

import com.proyecto.flotavehicular_webapp.enums.ESTATES;

public class EnumUtils {
    public static boolean isValidState(String state) {
        return ESTATES.ACTIVE.name().equals(state) || ESTATES.INACTIVE.name().equals(state);
    }

}
