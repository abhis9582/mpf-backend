package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.Enquery;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.EnqueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enquiry")
public class EnqueryController {
    @Autowired
    private EnqueryService enqueryService;
    @GetMapping("/get-all")
    public ResponseEntity<List<Enquery>> getAll(){
        return new ResponseEntity<>(enqueryService.getAll(), HttpStatus.OK);
    }
    @PostMapping("/post")
    public ResponseEntity<Response> addUpdate(@RequestBody Enquery enquery){
        return new ResponseEntity<>(enqueryService.addUpdate(enquery), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteEnquery(@PathVariable int id){
        return new ResponseEntity<>(enqueryService.deleteEnquery(id), HttpStatus.OK);
    }
}
