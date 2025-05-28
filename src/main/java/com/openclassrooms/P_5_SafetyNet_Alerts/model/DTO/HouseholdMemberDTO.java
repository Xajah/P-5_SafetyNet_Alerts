package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import lombok.Data;
import lombok.Builder;
import java.util.List;

/**
 * DTO représentant un membre du foyer, utilisé notamment dans les réponses de type childAlert.
 */
@Data
@Builder
public class HouseholdMemberDTO {

    /**
     * Prénom du membre du foyer.
     */
    private String firstName;

    /**
     * Nom de famille du membre du foyer.
     */
    private String lastName;
}
