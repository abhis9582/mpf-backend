package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.ProjectTypes;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectTypesService;
import com.mypropertyfact.estate.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-types")
@CrossOrigin("*")
public class ProjectTypesController {
    @Autowired
    private ProjectTypesService projectTypesService;
    @GetMapping("/get-all")
    public ResponseEntity<List<ProjectTypes>> getAllProjectTypes(){
        return new ResponseEntity<>(this.projectTypesService.getAllProjectTypes(), HttpStatus.OK);
    }
    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdateProjectType(@RequestBody ProjectTypes projectTypes){
        return new ResponseEntity<>(this.projectTypesService.addUpdateProjectType(projectTypes), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<ProjectTypes> getBySlug(@PathVariable("url")String url){
        return new ResponseEntity<>(this.projectTypesService.getBySlug(url), HttpStatus.OK);
    }
}
