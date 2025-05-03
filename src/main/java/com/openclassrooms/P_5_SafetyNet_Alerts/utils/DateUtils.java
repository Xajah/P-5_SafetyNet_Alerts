package com.openclassrooms.P_5_SafetyNet_Alerts.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static int calculateAgeFromBirthdate(LocalDate birthdate) {
        if (birthdate == null) return -1;
        return Period.between(birthdate, LocalDate.now()).getYears();
    }
}