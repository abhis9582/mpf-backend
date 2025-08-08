package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.dtos.ProjectDetailDto;
import com.mypropertyfact.estate.models.ProjectAmenityDto;
import com.mypropertyfact.estate.models.ProjectDto;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDetailDto>> fetchAllProjects(){
        return ResponseEntity.ok(projectService.fetchAllProjects());
    }

    @Transactional
    @GetMapping("/get-all-projects-list")
    public ResponseEntity<List<Map<String, Object>>> getAllProjectsList() {
        return ResponseEntity.ok(projectService.getAllProjectsList());
    }

    @PostMapping("/add-new")
    public ResponseEntity<Response> saveProject(
            @RequestParam(required = false) MultipartFile projectLogo,
            @RequestParam(required = false) MultipartFile locationMap,
            @RequestParam(required = false) MultipartFile projectThumbnail,
            @ModelAttribute ProjectDto projectDto) {
        return new ResponseEntity<>(this.projectService.saveProject(
                projectLogo,
                locationMap,
                projectThumbnail,
                projectDto
        ), HttpStatus.OK);
    }

    @GetMapping("/get/{url}")
    public ResponseEntity<Map<String, Object>> getBySlug(@PathVariable("url") String url) {
        return new ResponseEntity<>(this.projectService.getBySlugUrl(url), HttpStatus.OK);
    }

    //    @GetMapping("/builder/{id}")
//    public ResponseEntity<List<Project>> getAllBuilderProjects(@PathVariable("id")int id){
//        return new ResponseEntity<>(this.projectService.getAllBuilderProjects(id), HttpStatus.OK);
//    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteProject(@PathVariable("id") int id) {
        return new ResponseEntity<>(this.projectService.deleteProject(id), HttpStatus.OK);
    }

    @GetMapping("/search-by-type-city-budget")
    public ResponseEntity<?> searchByPropertyTypeLocationBudget(@RequestParam("propertyType") String propertyType,
                                                                @RequestParam("propertyLocation") String propertyLocation,
                                                                @RequestParam("budget") String budget,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "9") int limit) {
        return new ResponseEntity<>(projectService.searchByPropertyTypeLocationBudget(propertyType, propertyLocation, budget), HttpStatus.OK);
    }

    @PostMapping("/add-update-amenity")
    public ResponseEntity<Response> addUpdateAmenity(@RequestBody ProjectAmenityDto projectAmenityDto) {
        return new ResponseEntity<>(projectService.addUpdateAmenity(projectAmenityDto), HttpStatus.OK);
    }
}
