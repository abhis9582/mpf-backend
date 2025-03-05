package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.ProjectsAbout;
import com.mypropertyfact.estate.models.ProjectAboutResponse;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectAboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-about")
public class ProjectsAboutController {
    @Autowired
    private ProjectAboutService projectAboutService;
    @GetMapping("/get")
    public ResponseEntity<List<ProjectAboutResponse>> getAllProjectsAbout(){
        return new ResponseEntity<>(this.projectAboutService.getAllProjectsAbout(), HttpStatus.OK);
    }
    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdate(@RequestBody ProjectsAbout projectsAbout){
        return new ResponseEntity<>(this.projectAboutService.addUpdate(projectsAbout), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteProjectsAbout(@PathVariable("id")int id){
        return new ResponseEntity<>(this.projectAboutService.deleteProjectsAbout(id), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<ProjectsAbout> getBySlug(@PathVariable("url")String url){
        return new ResponseEntity<>(this.projectAboutService.getBySlug(url), HttpStatus.OK);
    }
}
