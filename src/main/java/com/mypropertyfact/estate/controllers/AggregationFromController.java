package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.AggregationFrom;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.AggregationFromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aggregationFrom")
public class AggregationFromController {
    @Autowired
    private AggregationFromService aggregationFromService;
    @GetMapping("/get")
    public ResponseEntity<List<AggregationFrom>> getAllAggregationFrom(){
        return new ResponseEntity<>(aggregationFromService.getAllAggregationFrom(), HttpStatus.OK);
    }
    @PostMapping("/post")
    public ResponseEntity<Response> addUpdateAggregationFrom(@RequestBody AggregationFrom aggregationFrom){
        return new ResponseEntity<>(aggregationFromService.addUpdateAggregationFrom(aggregationFrom), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteAggregationFrom(@PathVariable("id")int id){
        return new ResponseEntity<>(aggregationFromService.deleteAggregationFrom(id), HttpStatus.OK);
    }
}
