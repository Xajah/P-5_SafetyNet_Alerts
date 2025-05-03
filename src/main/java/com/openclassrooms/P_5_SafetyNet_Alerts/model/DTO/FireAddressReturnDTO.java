package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import java.util.List;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class FireAddressReturnDTO {
    private int stationNumber;
    private List<FireAddressResidentDTO> residents;

}
