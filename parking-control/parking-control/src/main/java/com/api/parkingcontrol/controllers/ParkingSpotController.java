package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.DTO.ParkingSpotDTO;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600) //Permite ser acessado de qualquer fonte
@RequestMapping("/parking-spot")
public class ParkingSpotController {
    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }


    /* retorna um objeto genérico,e recebe os dados vindo de um JSON
    * BeanUtills converte os dados do DTO para o model
    *
    *  */
    @PostMapping //uri definida a nivel de classe
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDTO parkingSpotDTO){
       //algumas verificações
        if (parkingSpotService.existsByLicensePlateCar(parkingSpotDTO.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Licensa de placa do carro ja em uso !");
        }
        if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Vaga ja em uso !");
        }
        if (parkingSpotService.existsByApartamentAndBlock(parkingSpotDTO.getApartament(),parkingSpotDTO.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflito: Vaga ja registrada por este apartamento !");
        }

        var parkingSpotModel = new ParkingSpotModel();//identifica o tipo da variavel

        BeanUtils.copyProperties(parkingSpotDTO,parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }


    @GetMapping
    public ResponseEntity<Page<ParkingSpotModel>> getALLParkingSpots(@PageableDefault(page = 0,size=10,sort="id",direction = Sort.Direction.ASC ) Pageable pageable){
        //traz toda a listagem de dados do banco,com paginação de 10 e 10 ordenado por ID
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findALL(pageable));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value ="id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findALL(id);
        if (!parkingSpotModelOptional.isPresent()){ //senão existir
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esta vaga de estacionamento não foi encontrada !");
        }
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
    }

   @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id")UUID id){
       Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findALL(id);
       if (!parkingSpotModelOptional.isPresent()){ //senão existir
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esta vaga de estacionamento não foi encontrada !");
       }
       parkingSpotService.delete(parkingSpotModelOptional.get());
       return ResponseEntity.status(HttpStatus.OK).body("Vaga deletada com sucesso!");
   }

   @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id")UUID id,
                                                    @RequestBody @Valid ParkingSpotDTO parkingSpotDTO){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findALL(id);
       if (!parkingSpotModelOptional.isPresent()){ //senão existir
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Esta vaga de estacionamento não foi encontrada !");
       }

       var parkingSpotModel =  parkingSpotModelOptional.get();

       parkingSpotModel.setParkingSpotNumber(parkingSpotModel.getParkingSpotNumber());
       parkingSpotModel.setLicensePlateCar(parkingSpotModel.getLicensePlateCar());
       parkingSpotModel.setModelCar(parkingSpotModel.getModelCar());
       parkingSpotModel.setBrandCar(parkingSpotModel.getBrandCar());
       parkingSpotModel.setColorCar(parkingSpotModel.getColorCar());
       parkingSpotModel.setResponsibleName(parkingSpotModel.getResponsibleName());
       parkingSpotModel.setApartament(parkingSpotModel.getApartament());
       parkingSpotModel.setBlock(parkingSpotModel.getBlock());


       return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));

   }

}
