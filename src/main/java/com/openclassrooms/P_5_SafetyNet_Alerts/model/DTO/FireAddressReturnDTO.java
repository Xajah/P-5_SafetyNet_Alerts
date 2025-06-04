package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO utilisé pour la réponse à la requête /fire.
 * Fournit le numéro de la caserne et la liste des résidents présents à l'adresse.
 */
@Data
@Builder
public class FireAddressReturnDTO {

    /**
     * Numéro de la caserne de pompiers desservant cette adresse.
     */
    private int stationNumber;

    /**
     * Liste des résidents présents à cette adresse, avec détails utiles pour l'intervention.
     */
    private List<FireAddressResidentDTO> residents;

}
