package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.FaqResponse;
import com.mypropertyfact.estate.configs.dtos.ProjectFaqDto;
import com.mypropertyfact.estate.entities.ProjectFaqs;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.ProjectFaqsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project-faqs")
public class ProjectFaqsController {
    @Autowired
    private ProjectFaqsService projectFaqsService;
    @GetMapping("/get-all")
    public ResponseEntity<List<Map<String, Object>>> getAllFaq(){
        return new ResponseEntity<>(this.projectFaqsService.getAllFaqs(), HttpStatus.OK);
    }
    @PostMapping("/add-update")
    public ResponseEntity<Response> addUpdateFaq(@RequestBody ProjectFaqDto projectFaqsDto){
        return new ResponseEntity<>(this.projectFaqsService.addUpdateFaqs(projectFaqsDto), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<List<ProjectFaqs>> getBySlug(@PathVariable("url")String url){
        return new ResponseEntity<>(this.projectFaqsService.getBySlug(url), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteFaq(@PathVariable("id")int id){
        return new ResponseEntity<>(projectFaqsService.deleteFaq(id), HttpStatus.OK);
    }
}
