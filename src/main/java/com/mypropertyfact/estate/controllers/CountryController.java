package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.dtos.CountryDto;
import com.mypropertyfact.estate.interfaces.CountryService;
import com.mypropertyfact.estate.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/country")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @GetMapping("/get-all-countries")
    public ResponseEntity<?> getAllCountries() {
        return ResponseEntity.ok(countryService.getAllCountry());
    }

    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdate(@RequestBody CountryDto countryDto){
        return ResponseEntity.ok(countryService.addUpdate(countryDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteCountry(@PathVariable("id") int id){
        return ResponseEntity.ok(countryService.deleteCountry(id));
    }
}
