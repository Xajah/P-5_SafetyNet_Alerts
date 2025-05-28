package com.openclassrooms.P_5_SafetyNet_Alerts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modèle représentant une caserne et l'adresse qu'elle couvre dans le système SafetyNet Alerts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Firestation {

    /**
     * Adresse couverte par la caserne.
     */
    private String address;

    /**
     * Numéro de la caserne.
     */
    private int station;
}
