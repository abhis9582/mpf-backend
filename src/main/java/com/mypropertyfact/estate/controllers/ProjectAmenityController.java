package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.Amenity;
import com.mypropertyfact.estate.models.ProjectAmenityDto;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectAmenityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-amenity")
@CrossOrigin("*")
public class ProjectAmenityController {
    @Autowired
    private ProjectAmenityService projectAmenityService;
    @PostMapping("/add-update")
    public ResponseEntity<Response> postAmenity(@RequestBody ProjectAmenityDto projectAmenityDto){
        return new ResponseEntity<>(this.projectAmenityService.addProjectAmenity(projectAmenityDto), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<List<Amenity>> getBySlug(@PathVariable("url")String url){
        return new ResponseEntity<>(this.projectAmenityService.getBySlug(url), HttpStatus.OK);
    }
}
