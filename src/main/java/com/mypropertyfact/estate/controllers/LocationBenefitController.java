package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.LocationBenefitDto;
import com.mypropertyfact.estate.entities.LocationBenefit;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.LocationBenefitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location-benefit")
@CrossOrigin("*")
public class LocationBenefitController {
    @Autowired
    private LocationBenefitService locationBenefitService;
    @GetMapping("/get-all")
    public ResponseEntity<List<LocationBenefitDto>> getAllBenefits(){
        return new ResponseEntity<>(this.locationBenefitService.getAllBenefits(), HttpStatus.OK);
    }
    @PostMapping("/add-new")
    public ResponseEntity<Response> addUpdate(@ModelAttribute LocationBenefitDto locationBenefitDto){
        return new ResponseEntity<>(this.locationBenefitService.addUpdateBenefit(locationBenefitDto), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<List<LocationBenefit>> getBySlug(@PathVariable("url")String url){
        return new ResponseEntity<>(this.locationBenefitService.getBySlug(url), HttpStatus.OK);
    }
}
