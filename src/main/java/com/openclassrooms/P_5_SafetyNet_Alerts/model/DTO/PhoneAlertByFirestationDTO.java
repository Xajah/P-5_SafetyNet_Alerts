package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import lombok.Data;
import java.util.List;
import lombok.Builder;

/**
 * DTO utilisé pour transmettre la liste des numéros de téléphone relevés pour une caserne donnée.
 * Réponse à l'endpoint /phoneAlert.
 */
@Data
@Builder
public class PhoneAlertByFirestationDTO {

    /**
     * Liste des numéros de téléphone des personnes couvertes par la caserne.
     */
    private List<String> phoneNumbers;
}
