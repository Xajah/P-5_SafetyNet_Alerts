package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import lombok.Data;
import java.util.List;
import lombok.Builder;


@Data
@Builder
public class PhoneAlertByFirestationDTO {
    private List<String> phoneNumbers;
}
