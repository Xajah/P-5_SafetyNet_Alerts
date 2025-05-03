package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class PersonByFirestationID{
    private String lastName;
    private String firstName;
    private String adress;
    private String phoneNumber;
}
