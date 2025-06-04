package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO servant à transmettre toutes les informations détaillées d'une personne (via son nom/prénom).
 * Utilisé pour la réponse à l'endpoint /personInfo.
 */
@Data
@Builder
public class PersonInfoByNameDTO {

    /**
     * Prénom de la personne.
     */
    private String firstName;

    /**
     * Nom de famille de la personne.
     */
    private String lastName;

    /**
     * Adresse postale complète de la personne.
     */
    private String address;

    /**
     * Adresse email de la personne.
     */
    private String email;

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
