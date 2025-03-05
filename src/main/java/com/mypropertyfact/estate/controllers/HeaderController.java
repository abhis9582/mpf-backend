package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.Headers;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.HeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/headers")
public class HeaderController {
    @Autowired
    private HeaderService headerService;
    @GetMapping("/get")
    public ResponseEntity<List<Headers>> getAllHeaders(){
        return new ResponseEntity<>(headerService.getAllHeaders(), HttpStatus.OK);
    }
    @PostMapping("/post")
    public ResponseEntity<Response> addUpdateHeader(@RequestBody Headers headers){
        return new ResponseEntity<>(headerService.addUpdateHeader(headers), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteHeader(@PathVariable("id")int id){
        return new ResponseEntity<>(headerService.deleteHeader(id), HttpStatus.OK);
    }
}
