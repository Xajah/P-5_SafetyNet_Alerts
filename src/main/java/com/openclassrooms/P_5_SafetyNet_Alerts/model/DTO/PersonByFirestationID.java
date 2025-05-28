package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;
import lombok.Data;
import lombok.Builder;

/**
 * DTO représentant une personne desservie par une caserne de pompiers donnée.
 * Utilisé dans les réponses pour /firestation.
 */
@Data
@Builder
public class PersonByFirestationID {

    /**
     * Nom de famille de la personne.
     */
    private String lastName;

    /**
     * Prénom de la personne.
     */
    private String firstName;

    /**
     * Adresse de la personne.
     */
    private String adress;

    /**
     * Numéro de téléphone de la personne.
     */
    private String phoneNumber;
}
