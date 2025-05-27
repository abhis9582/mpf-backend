package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.Enquery;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.EnquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enquiry")
public class EnquiryController {
    @Autowired
    private EnquiryService enquiryService;
    @GetMapping("/get-all")
    public ResponseEntity<List<Enquery>> getAll(){
        return new ResponseEntity<>(enquiryService.getAll(), HttpStatus.OK);
    }
    @PostMapping("/post")
    public ResponseEntity<Response> addUpdate(@RequestBody Enquery enquery){
        return new ResponseEntity<>(enquiryService.addUpdate(enquery), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteEnquiry(@PathVariable int id){
        return new ResponseEntity<>(enquiryService.deleteEnquiry(id), HttpStatus.OK);
    }
}
