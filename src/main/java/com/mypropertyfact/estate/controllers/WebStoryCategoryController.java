package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.WebStoryCategoryDto;
import com.mypropertyfact.estate.interfaces.WebStoryCategoryService;
import com.mypropertyfact.estate.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/web-story-category")
public class WebStoryCategoryController {

    @Autowired
    private WebStoryCategoryService webStoryCategoryService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(webStoryCategoryService.getAllCategories());
    }

    @PostMapping("/add-update")
    public ResponseEntity<?> addUpdate(@RequestBody WebStoryCategoryDto webStoryCategoryDto) {
        return ResponseEntity.ok(webStoryCategoryService.addUpdate(webStoryCategoryDto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Response> deleteCategory(@PathVariable("id") int id) {
        return ResponseEntity.ok(webStoryCategoryService.deleteCategory(id));
    }
}
