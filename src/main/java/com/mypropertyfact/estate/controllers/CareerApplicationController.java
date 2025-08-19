package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.dtos.CareerApplicationDto;
import com.mypropertyfact.estate.interfaces.CareerApplicationService;
import com.mypropertyfact.estate.models.Response;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/career")
public class CareerApplicationController {

    @Autowired
    private CareerApplicationService careerApplicationService;

    @GetMapping
    public ResponseEntity<List<CareerApplicationDto>> getAllApplication() {
        return ResponseEntity.ok(careerApplicationService.getAllCareerApplication());
    }

    @PostMapping
    public ResponseEntity<Response> submitApplication(@Valid @ModelAttribute CareerApplicationDto careerApplicationDto){
        return ResponseEntity.ok(careerApplicationService.submitApplication(careerApplicationDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteApplication(@PathVariable Long id){
        return ResponseEntity.ok(careerApplicationService.deleteCareerApplication(id));
    }
}
