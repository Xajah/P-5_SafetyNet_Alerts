package com.openclassrooms.P_5_SafetyNet_Alerts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class})
public class P5SafetyNetAlertsApplication {

	public static void main(String[] args) {
		SpringApplication.run(P5SafetyNetAlertsApplication.class, args);
	}

}
