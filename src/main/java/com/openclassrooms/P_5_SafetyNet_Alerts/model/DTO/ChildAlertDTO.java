package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import lombok.Data;
import lombok.Builder;
import java.util.List;

/**
 * DTO utilisé pour la route /childAlert. Contient les informations d’un enfant résidant
 * à une adresse donnée, ainsi que la liste des autres membres du foyer.
 */
@Data
@Builder
public class ChildAlertDTO {

    /**
     * Prénom de l’enfant.
     */
    private String firstName;

    /**
     * Nom de famille de l’enfant.
     */
    private String lastName;

    /**
     * Âge de l’enfant (calculé/saisi, généralement <18).
     */
    private int age;

    /**
     * Liste des membres du foyer (autres personnes habitant à la même adresse).
     */
    private List<HouseholdMemberDTO> householdMembers;
}
