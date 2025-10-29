package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.TopLocationsByTransaction;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.models.TopLocationByTransactionResponse;
import com.mypropertyfact.estate.services.TopLocationsByTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/top-locations-by-transaction")
public class TopLocationsByTransactionController {
    private TopLocationsByTransactionService topLocationsByTransactionService;

    public TopLocationsByTransactionController(TopLocationsByTransactionService topLocationsByTransactionService){
        this.topLocationsByTransactionService = topLocationsByTransactionService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<TopLocationsByTransaction>> getAllTopLocationsByTransaction(){
        return new ResponseEntity<>(topLocationsByTransactionService.getAllTopLocationsByTransaction(), HttpStatus.OK);
    }
    @PostMapping("/post")
    public ResponseEntity<Response> addUpdateTopLocationsByTransaction(@RequestBody TopLocationsByTransaction topLocationsByTransaction){
        return new ResponseEntity<>(topLocationsByTransactionService.addUpdateTopLocationsByTransaction(topLocationsByTransaction), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteTopLocationsByTransaction(@PathVariable("id")int id){
        return new ResponseEntity<>(topLocationsByTransactionService.deleteTopLocationsByTransaction(id), HttpStatus.OK);
    }

    @GetMapping("/top-location-by-transaction")
    public ResponseEntity<List<TopLocationByTransactionResponse>> getTopLocationByTransaction(){
        return new ResponseEntity<>(topLocationsByTransactionService.getAllCategoryWiseData(), HttpStatus.OK);
    }
}
