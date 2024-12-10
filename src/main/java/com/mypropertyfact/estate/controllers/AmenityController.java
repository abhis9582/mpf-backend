package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.AmenityDto;
import com.mypropertyfact.estate.entities.Amenity;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.AmenityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/amenity")
@CrossOrigin("*")
public class AmenityController {
    @Autowired
    private AmenityService amenityService;

    @GetMapping("/get-all")
    public ResponseEntity<List<Amenity>> getAllAmenities(){
        return new ResponseEntity<>(this.amenityService.getAllAmenities(), HttpStatus.OK);
    }

    @PostMapping("/post")
    public ResponseEntity<Response> postNewAmenity(@ModelAttribute AmenityDto amenityDto){
        Response response = this.amenityService.postAmenity(amenityDto);
        if(response.getIsSuccess() == 1){
           return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
