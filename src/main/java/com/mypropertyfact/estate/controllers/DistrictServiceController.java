package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.interfaces.DistrictService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/district")
public class DistrictServiceController {

    private final DistrictService districtService;
    
    public DistrictServiceController(DistrictService districtService){
        this.districtService = districtService;
    }

    @PostMapping
    public ResponseEntity<?> addAllIndiaData(@RequestParam("file")MultipartFile multipartFile){
        return ResponseEntity.ok(districtService.addAllDetailsFromFile(multipartFile));
    }
}
