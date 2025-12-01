package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.FeatureDto;
import com.mypropertyfact.estate.dtos.FeatureDetailedDto;
import com.mypropertyfact.estate.entities.Feature;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feature")
public class FeatureController {
    
    @Autowired
    private FeatureService featureService;
    
    @GetMapping("/get-all")
    public ResponseEntity<List<Feature>> getAllFeatures() {
        return new ResponseEntity<>(featureService.getAllFeatures(), HttpStatus.OK);
    }
    
    @PostMapping("/post")
    public ResponseEntity<Response> postFeature(@RequestBody FeatureDto featureDto) {
        Response response = featureService.postFeature(featureDto);
        if (response.getIsSuccess() == 1) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/post-multiple-features")
    public ResponseEntity<Response> postMultipleFeatures(@ModelAttribute FeatureDetailedDto dto) {
        return ResponseEntity.ok(featureService.postMultipleFeatures(dto));
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteFeature(@PathVariable("id") Long id) {
        return new ResponseEntity<>(featureService.deleteFeature(id), HttpStatus.OK);
    }
}

