package com.openclassrooms.P_5_SafetyNet_Alerts.model;



import lombok.AllArgsConstructor;
import lombok.Builder;
import  lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Firestation {

    private String address;
    private int station;



}
