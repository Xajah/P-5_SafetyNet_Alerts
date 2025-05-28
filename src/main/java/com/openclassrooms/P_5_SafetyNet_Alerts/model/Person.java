package com.openclassrooms.P_5_SafetyNet_Alerts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modèle représentant une personne dans le système SafetyNet Alerts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    /**
     * Prénom de la personne.
     */
    private String firstName;

    /**
     * Nom de famille de la personne.
     */
    private String lastName;

    /**
     * Adresse postale de la personne.
     */
    private String address;

    /**
     * Ville de résidence de la personne.
     */
    private String city;

    /**
     * Code postal de la personne.
     */
    private String zip;

    /**
     * Numéro de téléphone de la personne.
     */
    private String phone;

    /**
     * Email de la personne.
     */
    private String email;
}
