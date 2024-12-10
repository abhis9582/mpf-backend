package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.FaqResponse;
import com.mypropertyfact.estate.entities.ProjectFaqs;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectFaqsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project-faqs")
@CrossOrigin("*")
public class ProjectFaqsController {
    @Autowired
    private ProjectFaqsService projectFaqsService;
    @GetMapping("/get-all")
    public ResponseEntity<List<FaqResponse>> getAllFaq(){
        return new ResponseEntity<>(this.projectFaqsService.getAllFaqs(), HttpStatus.OK);
    }
    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdateFaq(@RequestBody ProjectFaqs projectFaqs){
        return new ResponseEntity<>(this.projectFaqsService.addUpdateFaqs(projectFaqs), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<List<ProjectFaqs>> getBySlug(@PathVariable("url")String url){
        return new ResponseEntity<>(this.projectFaqsService.getBySlug(url), HttpStatus.OK);
    }
}
