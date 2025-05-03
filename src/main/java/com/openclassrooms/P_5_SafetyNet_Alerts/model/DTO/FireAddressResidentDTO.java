package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import java.util.List;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class FireAddressResidentDTO {
        private String lastName;
        private String firstName;
        private String phone;
        private int age;
        private List<String> medications;
        private List<String> allergies;

    }

