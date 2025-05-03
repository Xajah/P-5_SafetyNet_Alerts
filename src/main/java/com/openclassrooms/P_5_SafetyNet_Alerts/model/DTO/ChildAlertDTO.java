package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class ChildAlertDTO {
    private String firstName;
    private String lastName;
    private int age;
    private List<HouseholdMemberDTO> householdMembers;
}
