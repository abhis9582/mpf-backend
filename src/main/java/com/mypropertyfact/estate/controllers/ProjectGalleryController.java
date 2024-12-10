package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.ProjectGalleryDto;
import com.mypropertyfact.estate.configs.dtos.ProjectGalleryResponse;
import com.mypropertyfact.estate.entities.ProjectGallery;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectGalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-gallery")
@CrossOrigin("*")
public class ProjectGalleryController {
    @Autowired
    private ProjectGalleryService projectGalleryService;

    @GetMapping("/get-all")
    public ResponseEntity<List<ProjectGalleryResponse>> getAllProjectGallery() {
        return new ResponseEntity<>(this.projectGalleryService.getAllGalleryImages(), HttpStatus.OK);
    }
    @PostMapping("/add-new")
    public ResponseEntity<Response> postGalleryImage(@ModelAttribute ProjectGalleryDto projectGalleryDto){
        return new ResponseEntity<>(this.projectGalleryService.postGalleryImage(projectGalleryDto), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<List<ProjectGallery>> getBySlugUrl(@PathVariable("url")String url){
        return new ResponseEntity<>(this.projectGalleryService.getBySlugUrl(url), HttpStatus.OK);
    }
}
