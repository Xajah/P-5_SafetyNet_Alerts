package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * DTO utilisé pour la réponse à /firestation.
 * Contient la liste des personnes desservies par une caserne ainsi que le décompte des adultes et des enfants.
 */
@Data
@Builder
public class PersonsByFirestationIDReturn {

    /**
     * Liste des personnes couvertes par la caserne.
     */
    private List<PersonByFirestationID> persons;

    /**
     * Nombre d'adultes présents à cette adresse/couvert par cette caserne.
     */
    private int countOfAdults;

    /**
     * Nombre d'enfants présents à cette adresse/couvert par cette caserne.
     */
    private int countOfChilds;
}
