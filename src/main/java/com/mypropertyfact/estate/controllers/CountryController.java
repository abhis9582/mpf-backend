package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.Country;
import com.mypropertyfact.estate.interfaces.CountryService;
import com.mypropertyfact.estate.interfaces.StateService;
import com.mypropertyfact.estate.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/country")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllStates() {
        return ResponseEntity.ok(countryService.getAll());
    }

    @GetMapping("/get-all-countries")
    public ResponseEntity<?> getAllCountries() {
        return ResponseEntity.ok(countryService.getAllCountry());
    }

    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdate(@RequestBody Country country){
        return ResponseEntity.ok(countryService.addUpdate(country));
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCountry(@PathVariable("id") int id){
        countryService.deleteCountry(id);
    }
}
