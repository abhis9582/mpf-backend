package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.services.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/city")
public class CityController {
    private CityService cityService;
    public CityController(CityService cityService){
        this.cityService = cityService;
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllCities(){
        return new ResponseEntity<>(this.cityService.getAllCities(), HttpStatus.OK);
    }
    @PostMapping("/add-new")
    public ResponseEntity<?> postNewCity(@RequestBody City city){
        return new ResponseEntity<>(this.cityService.postNewCity(city), HttpStatus.CREATED);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCity(@PathVariable("id")int id, @RequestBody City city){
        return new ResponseEntity<>(this.cityService.updateCity(id, city), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable("id")int id){
        return new ResponseEntity<>(this.cityService.deleteCity(id), HttpStatus.OK);
    }
    @GetMapping("/get-single/{id}")
    public ResponseEntity<?> getSingleRecord(@PathVariable("id") int id){
        return new ResponseEntity<>(this.cityService.getSingleCity(id), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<City> getBySlug(@PathVariable("url")String url){
        return new ResponseEntity<>(this.cityService.getBySlug(url), HttpStatus.OK);
    }
    @GetMapping("/{city}")
    public ResponseEntity<?> getByCityName(@PathVariable("city")String city){
        return new ResponseEntity<>(this.cityService.getByCityName(city), HttpStatus.OK);
    }

}
