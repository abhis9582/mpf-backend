package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.FloorPlansDto;
import com.mypropertyfact.estate.entities.FloorPlan;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.FloorPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/floor-plans")
public class FloorPlanController {
    @Autowired
    private FloorPlanService floorPlanService;
    @GetMapping("/get-all")
    public ResponseEntity<List<FloorPlansDto>> getAllFloorPlans(){
        return new ResponseEntity<>(this.floorPlanService.getAllPlans(), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<List<FloorPlan>> getAllFloorPlans(@PathVariable("url") String url){
        return new ResponseEntity<>(this.floorPlanService.getBySlugUrl(url), HttpStatus.OK);
    }
    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdateFloorPlan(@RequestBody FloorPlan floorPlan){
        return new ResponseEntity<>(this.floorPlanService.addUpdatePlan(floorPlan), HttpStatus.OK);
    }
}
