package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;
import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class PersonsByFirestationIDReturn{
    private List<PersonByFirestationID> persons;
    private int countOfAdults;
    private int countOfChilds;

}
