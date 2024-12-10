package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.BuilderResponse;
import com.mypropertyfact.estate.entities.Builder;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.services.BuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/builders")
@CrossOrigin("*")
public class BuilderController {
    @Autowired
    private BuilderService builderService;

    @GetMapping("/get-all")
    public ResponseEntity<BuilderResponse> getAllBuilders() {
        BuilderResponse builderResponse = this.builderService.getAllBuilders();
        if (builderResponse.getResponse().getIsSuccess() == 1) {
            return new ResponseEntity<>(builderResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(builderResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-update")
    public ResponseEntity<Response> AddUpdateBuilder(@RequestBody Builder builder) {
        Response response = this.builderService.addUpdateBuilder(builder);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteBuilder(@PathVariable("id") int id){
        return new ResponseEntity<>(this.builderService.deleteBuilder(id), HttpStatus.OK);
    }
    @GetMapping("/get/{url}")
    public ResponseEntity<Builder> deleteBuilder(@PathVariable("url") String url){
        return new ResponseEntity<>(this.builderService.getBySlug(url), HttpStatus.OK);
    }
}
