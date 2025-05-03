package com.openclassrooms.P_5_SafetyNet_Alerts.model.DTO;

import java.util.List;
import lombok.Data;
import lombok.Builder;


@Data
@Builder
public class PersonInfoByNameDTO {
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private int age;
    private List<String> medications;
    private List<String> allergies; }


