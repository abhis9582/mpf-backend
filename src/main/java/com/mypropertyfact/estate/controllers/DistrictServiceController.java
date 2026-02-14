package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.interfaces.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/district")
@RequiredArgsConstructor
public class DistrictServiceController {

    private final DistrictService districtService;

    @PostMapping
    public ResponseEntity<?> addAllIndiaData(@RequestParam("file")MultipartFile multipartFile){
        return ResponseEntity.ok(districtService.addAllDetailsFromFile(multipartFile));
    }
}
