package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO pour une personne résidant à une adresse donnée (utilisé via /fire).
 * Fournit les informations utiles pour l’intervention en cas d’incendie.
 */
@Data
@Builder
public class FireAddressResidentDTO {

    /**
     * Nom de famille de la personne.
     */
    private String lastName;

    /**
     * Prénom de la personne.
     */
    private String firstName;

    /**
     * Numéro de téléphone de la personne.
     */
    private String phone;

    /**
     * Âge de la personne.
     */
    private int age;

    /**
     * Liste des médicaments pris par la personne.
     */
    private List<String> medications;

    /**
     * Liste des allergies connues de la personne.
     */
    private List<String> allergies;
}
