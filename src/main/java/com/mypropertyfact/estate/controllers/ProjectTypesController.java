package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.ProjectTypes;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.projections.ProjectTypeView;
import com.mypropertyfact.estate.services.ProjectTypesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project-types")
public class ProjectTypesController {
    @Autowired
    private ProjectTypesService projectTypesService;
    @GetMapping("/get-all")
    public ResponseEntity<List<ProjectTypeView>> getAllProjectTypes(){
        return new ResponseEntity<>(this.projectTypesService.getAllProjectTypes(), HttpStatus.OK);
    }

    @GetMapping("/get-all-types")
    public ResponseEntity<List<ProjectTypes>> getAllProjectTypesList(){
        return new ResponseEntity<>(this.projectTypesService.getAllProjectTypesList(), HttpStatus.OK);
    }
    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdateProjectType(@RequestBody ProjectTypes projectTypes){
        return new ResponseEntity<>(this.projectTypesService.addUpdateProjectType(projectTypes), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<Map<String, Object>> getBySlug(@PathVariable("url")String url){
        return new ResponseEntity<>(this.projectTypesService.getBySlug(url), HttpStatus.OK);
    }
//    @GetMapping("/{url}")
//    public ResponseEntity<List<Project>> getPropertiesBySlug(@PathVariable("url")String url){
//        return new ResponseEntity<>(this.projectTypesService.getPropertiesBySlug(url), HttpStatus.OK);
//    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteProjectType(@PathVariable("id")int id){
        return new ResponseEntity<>(this.projectTypesService.deleteProjectType(id), HttpStatus.OK);
    }
}
