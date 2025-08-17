package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.dtos.CityDto;
import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/city")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCities() {
        return new ResponseEntity<>(cityService.getAllCities(), HttpStatus.OK);
    }

    @PostMapping("/add-new")
    public ResponseEntity<?> postNewCity(@RequestBody CityDto cityDto) {
        return new ResponseEntity<>(this.cityService.postNewCity(cityDto), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCity(@PathVariable("id") int id, @RequestBody City city) {
        return new ResponseEntity<>(this.cityService.updateCity(id, city), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable("id") int id) {
        return new ResponseEntity<>(this.cityService.deleteCity(id), HttpStatus.OK);
    }

    @GetMapping("/get-single/{id}")
    public ResponseEntity<?> getSingleRecord(@PathVariable("id") int id) {
        return new ResponseEntity<>(this.cityService.getSingleCity(id), HttpStatus.OK);
    }

    @GetMapping("/get/{url}")
    public ResponseEntity<CityDto> getBySlug(@PathVariable("url") String url) {
        return new ResponseEntity<>(this.cityService.getBySlug(url), HttpStatus.OK);
    }

//    @GetMapping("/{city}")
//    public ResponseEntity<?> getByCityName(@PathVariable("city") String city) {
//        return new ResponseEntity<>(this.cityService.getByCityName(city), HttpStatus.OK);
//    }

    public ResponseEntity<Response> addUpdateCity(@RequestParam(required = false) MultipartFile cityImage,
                                                  @RequestBody City city) {
        return new ResponseEntity<>(cityService.addUpdateCity(cityImage, city), HttpStatus.OK);
    }
    @GetMapping("/get-all-cities")
    public ResponseEntity<List<Map<String, Object>>> getAllCityList() {
        return ResponseEntity.ok(cityService.getAllCityList());
    }

}
