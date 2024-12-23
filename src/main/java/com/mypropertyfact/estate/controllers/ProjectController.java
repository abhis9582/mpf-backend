package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.models.ProjectDto;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@CrossOrigin("*")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @GetMapping("/get-all")
    public ResponseEntity<List<Project>> getAllProjects(){
        return new ResponseEntity<>(this.projectService.getAllProjects(), HttpStatus.OK);
    }
    @PostMapping("/add-new")
    public ResponseEntity<Response> saveProject(@ModelAttribute ProjectDto projectDto){
        return new ResponseEntity<>(this.projectService.saveProject(projectDto), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<Project> getBySlug(@PathVariable("url")String url){
        return new ResponseEntity<>(this.projectService.getBySlugUrl(url), HttpStatus.OK);
    }
    @GetMapping("/builder/{id}")
    public ResponseEntity<List<Project>> getAllBuilderProjects(@PathVariable("id")int id){
        return new ResponseEntity<>(this.projectService.getAllBuilderProjects(id), HttpStatus.OK);
    }
}
