package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.ProjectWithBannerDTO;
import com.mypropertyfact.estate.configs.dtos.PropertyDetailDto;
import com.mypropertyfact.estate.entities.Property;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/properties")
public class PropertyController {
    @Autowired
    private PropertyService propertyService;
    @Value("${uploads_path}")
    private String uploadDir;

    @GetMapping("/get-all")
    public ResponseEntity<List<Property>> getAllProperties() {
        return new ResponseEntity<>(this.propertyService.getAllProperties(), HttpStatus.OK);
    }
    @GetMapping("/get-brief-detail")
    public ResponseEntity<List<ProjectWithBannerDTO>> getBriefDetail() {
        return new ResponseEntity<>(this.propertyService.getAllProjectsWithMobileBanners(), HttpStatus.OK);
    }
    @PostMapping("/post")
    public ResponseEntity<Response> postProperty(@ModelAttribute PropertyDetailDto propertyDetailDto) {
        Response response = this.propertyService.postProperty(propertyDetailDto);
        if(response.getIsSuccess() == 1){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/{slug-url}")
    public ResponseEntity<?> getBySlugUrl(@PathVariable("slug-url")String slugUrl){
        Property property = this.propertyService.getBySlugUrl(slugUrl);
        if(property == null){
            return new ResponseEntity<>("No data found", HttpStatus.NOT_FOUND);
        }
        if (property.getProjectName().isEmpty()){
            return new ResponseEntity<>("No data found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(this.propertyService.getBySlugUrl(slugUrl), HttpStatus.OK);
    }
}
