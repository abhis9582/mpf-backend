package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.FloorPlanDto;
import com.mypropertyfact.estate.entities.FloorPlan;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.FloorPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/floor-plans")
public class FloorPlanController {
    @Autowired
    private FloorPlanService floorPlanService;
    @GetMapping("/get-all")
    public ResponseEntity<List<Map<String, Object>>> getAllFloorPlans(){
        return new ResponseEntity<>(this.floorPlanService.getAllPlans(), HttpStatus.OK);
    }

    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdateFloorPlan(@RequestBody FloorPlanDto floorPlan){
        return new ResponseEntity<>(this.floorPlanService.addUpdatePlan(floorPlan), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteFloorPlan(@PathVariable("id")int id){
        return new ResponseEntity<>(this.floorPlanService.deleteFloorPlan(id), HttpStatus.OK);
    }
}
