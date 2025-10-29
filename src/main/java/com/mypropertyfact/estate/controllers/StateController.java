package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.dtos.StateDto;
import com.mypropertyfact.estate.interfaces.StateService;
import com.mypropertyfact.estate.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/state")
public class StateController {
    @Autowired
    private StateService stateService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllStates() {
        return ResponseEntity.ok(stateService.getAll());
    }

    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdate(@RequestBody StateDto stateDto){
        return ResponseEntity.ok(stateService.addUpdate(stateDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteState(@PathVariable("id") int id){
        stateService.deleteState(id);
        return ResponseEntity.ok().build();
    }
}
