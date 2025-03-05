package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectTypes;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-types")
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
    @GetMapping("/{url}")
    public ResponseEntity<List<Project>> getPropertiesBySlug(@PathVariable("url")String url){
        return new ResponseEntity<>(this.projectTypesService.getPropertiesBySlug(url), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteProjectType(@PathVariable("id")int id){
        return new ResponseEntity<>(this.projectTypesService.deleteProjectType(id), HttpStatus.OK);
    }
}
