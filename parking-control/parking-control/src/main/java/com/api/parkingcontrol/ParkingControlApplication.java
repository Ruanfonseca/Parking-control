package com.api.parkingcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController //para explicitar a aplicação , que essa classe sera um bean do tipo controller
public class ParkingControlApplication {

	//link do javadoc ,file:///C:/parking-control/index.html
	public static void main(String[] args) {
		SpringApplication.run(ParkingControlApplication.class, args);
	}
	@GetMapping("/") //para expor na porta 8080
	public String index(){
		return "hello word !";
	}


}
