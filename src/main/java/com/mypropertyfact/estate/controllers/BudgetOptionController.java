package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.BudgetOption;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.BudgetOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budget-option")
public class BudgetOptionController {
    @Autowired
    private BudgetOptionService budgetOptionService;
    @GetMapping("/get-all")
    public ResponseEntity<List<BudgetOption>> getAll(){
        return new ResponseEntity<>(budgetOptionService.getAll(), HttpStatus.OK);
    }
    @PostMapping("/post")
    public ResponseEntity<Response> addUpdateBudgetOption(@RequestBody BudgetOption budgetOption){
        return new ResponseEntity<>(budgetOptionService.addUpdate(budgetOption), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteBudgetOption(@PathVariable("id")int id){
        return new ResponseEntity<>(budgetOptionService.deleteBudgetOption(id), HttpStatus.OK);
    }
}
