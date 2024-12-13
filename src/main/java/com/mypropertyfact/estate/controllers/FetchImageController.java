package com.mypropertyfact.estate.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/get/images")
@CrossOrigin("*")
public class FetchImageController {
    @Value("${uploads_path}")
    private String uploadDir;
    @Value("${upload_amenity_path}")
    private String amenityPath;
    @Value("${upload_icon_path}")
    private String iconPath;
    @GetMapping("/properties/{projectname}/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename, @PathVariable String projectname) {
        try {
            Path imagePath = Paths.get(uploadDir, projectname+"/"+filename);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // or determine the correct type dynamically
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/amenity/{filename}")
    public ResponseEntity<Resource> getAmenityImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get(amenityPath, filename);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // or determine the correct type dynamically
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/icon/{filename}")
    public ResponseEntity<Resource> getIcon(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get(iconPath, filename);
            Resource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // or determine the correct type dynamically
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
