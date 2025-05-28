package com.openclassrooms.P_5_SafetyNet_Alerts.utils;

import java.time.LocalDate;
import java.time.Period;


/**
 * Classe utilitaire pour la gestion des dates et calculs d'âge.
 * <p>
 * Fournit des méthodes statiques principalement utilisées pour déterminer l'âge
 * d'une personne à partir de sa date de naissance.
 */
public class DateUtils {

    /**
     * Calcule l'âge en années à partir d'une date de naissance donnée.
     *
     * @param birthdate date de naissance sous forme de {@link LocalDate}
     * @return l'âge en années, ou -1 si la date est nulle
     */
    public static int calculateAgeFromBirthdate(LocalDate birthdate) {
        if (birthdate == null) return -1;
        return Period.between(birthdate, LocalDate.now()).getYears();
    }
}
