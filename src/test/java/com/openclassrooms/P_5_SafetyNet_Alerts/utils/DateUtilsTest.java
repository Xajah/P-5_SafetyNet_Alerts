package com.openclassrooms.P_5_SafetyNet_Alerts.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;


public class DateUtilsTest {

    @Test
    public void calculateAgeFromBirthdateTest() {
        // Arrange : date de naissance connue
        LocalDate birthdate = LocalDate.of(1990, 5, 18);

        // Act : appel methode statique
        int result = DateUtils.calculateAgeFromBirthdate(birthdate);

        // Expected : age dynamique
        int expectedAge = LocalDate.now().getYear() - 1990;
        LocalDate anniversaireCetteAnnee = LocalDate.of(LocalDate.now().getYear(), 5, 18);
        if (LocalDate.now().isBefore(anniversaireCetteAnnee)) {
            expectedAge--;
        }

        // Assert
        assertEquals(expectedAge, result, "L'âge calculé est incorrect");
    }

    @Test
    public void calculateAgeFromBirthdateNullTest() {
        // Act
        int result = DateUtils.calculateAgeFromBirthdate(null);

        // Assert
        assertEquals(-1, result, "Doit retourner -1 si la date est nulle");
    }
}