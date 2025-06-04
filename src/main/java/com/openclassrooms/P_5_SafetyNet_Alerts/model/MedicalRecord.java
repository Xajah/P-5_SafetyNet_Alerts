package com.openclassrooms.P_5_SafetyNet_Alerts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Modèle représentant le dossier médical d'une personne dans le système SafetyNet Alerts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    /**
     * Prénom de la personne concernée par ce dossier médical.
     */
    private String firstName;

    /**
     * Nom de famille de la personne concernée par ce dossier médical.
     */
    private String lastName;

    /**
     * Date de naissance de la personne au format MM/dd/yyyy.
     */
    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate birthdate;

    /**
     * Liste des médicaments pris par la personne.
     */
    private List<String> medications;

    /**
     * Liste des allergies connues de la personne.
     */
    private List<String> allergies;
}
