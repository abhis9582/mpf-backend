package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.ProjectBannerDto;
import com.mypropertyfact.estate.entities.ProjectBanner;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/project-banner")
public class ProjectBannerController {
    @Autowired
    private ProjectBannerService projectBannerService;
    @GetMapping("/get-all")
    public ResponseEntity<List<ProjectBanner>> getAllBanners(){
        return new ResponseEntity<>(this.projectBannerService.getAllBanners(), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<ProjectBanner> getAllMobileBanners(@PathVariable("url")String url){
        return new ResponseEntity<>(this.projectBannerService.getBySlug(url), HttpStatus.OK);
    }
    @PostMapping("/add-banner")
    public ResponseEntity<Response> postBanner(
            @RequestParam(required = false) MultipartFile mobileBanner,
            @RequestParam(required = false) MultipartFile desktopBanner,
            @ModelAttribute ProjectBannerDto projectBannerDto){
        return new ResponseEntity<>(this.projectBannerService.postBanner(mobileBanner, desktopBanner, projectBannerDto), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteBanner(@PathVariable("id")int id){
        return new ResponseEntity<>(projectBannerService.deleteBanner(id), HttpStatus.OK);
    }
}
