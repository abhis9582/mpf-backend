package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.ProjectWalkthroughDto;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectWalkthroughService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-walkthrough")
public class ProjectWalkthroughController {
    @Autowired
    private ProjectWalkthroughService projectWalkthroughService;
    @GetMapping("/get")
    public ResponseEntity<List<ProjectWalkthroughDto>> getAllWalkthrough(){
        return new ResponseEntity<>(this.projectWalkthroughService.getAllWalkthrough(), HttpStatus.OK);
    }
    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdate(@RequestBody ProjectWalkthroughDto projectWalkthroughDto){
        return new ResponseEntity<>(this.projectWalkthroughService.addUpdate(projectWalkthroughDto), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteWalkthrough(@PathVariable("id") int id){
        return new ResponseEntity<>(this.projectWalkthroughService.deleteWalkthrough(id), HttpStatus.OK);
    }
}
