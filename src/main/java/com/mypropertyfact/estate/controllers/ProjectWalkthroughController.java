package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.ProjectWalkthrough;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectWalkthroughService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-walkthrough")
@CrossOrigin("*")
public class ProjectWalkthroughController {
    @Autowired
    private ProjectWalkthroughService projectWalkthroughService;
    @GetMapping("/get")
    public ResponseEntity<List<ProjectWalkthrough>> getAllWalkthrough(){
        return new ResponseEntity<>(this.projectWalkthroughService.getAllWalkthrough(), HttpStatus.OK);
    }
    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdate(@RequestBody ProjectWalkthrough projectWalkthrough){
        return new ResponseEntity<>(this.projectWalkthroughService.addUpdate(projectWalkthrough), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteWalkthrough(@PathVariable("id") int id){
        return new ResponseEntity<>(this.projectWalkthroughService.deleteWalkthrough(id), HttpStatus.OK);
    }

    @GetMapping("/get/{url}")
    public ResponseEntity<ProjectWalkthrough> getBySlug(@PathVariable("url")String url){
        return new ResponseEntity<>(this.projectWalkthroughService.getBySlug(url), HttpStatus.OK);
    }
}
