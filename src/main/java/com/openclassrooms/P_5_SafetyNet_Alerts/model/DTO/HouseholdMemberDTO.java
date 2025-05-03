package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class HouseholdMemberDTO {
    private String firstName;
    private String lastName;
}