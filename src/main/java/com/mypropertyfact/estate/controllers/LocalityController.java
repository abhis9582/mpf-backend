package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.LocalityDto;
import com.mypropertyfact.estate.interfaces.LocalityService;
import com.mypropertyfact.estate.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locality")
public class LocalityController {

    @Autowired
    private LocalityService localityService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllLocalities() {
        return ResponseEntity.ok(localityService.getAllLocalities());
    }

    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdate(@RequestBody LocalityDto localityDto){
        return ResponseEntity.ok(localityService.addUpdateLocality(localityDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteLocality(@PathVariable("id") long id) {
        return ResponseEntity.ok(localityService.deleteLocality(id));
    }
}
