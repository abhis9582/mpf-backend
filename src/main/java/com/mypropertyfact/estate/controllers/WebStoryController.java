package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.configs.dtos.WebStoryDto;
import com.mypropertyfact.estate.interfaces.WebStoryService;
import com.mypropertyfact.estate.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/web-story")
public class WebStoryController {

    @Autowired
    private WebStoryService webStoryService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllWebStories() {
        return ResponseEntity.ok(webStoryService.getAllWebStories());
    }

    @PostMapping("/add-update")
    public ResponseEntity<?> addUpdate(@RequestParam(required = false) MultipartFile image,
                                       @ModelAttribute WebStoryDto webStoryDto) {
        return ResponseEntity.ok(webStoryService.addUpdateWebStory(image, webStoryDto));
    }

    @DeleteMapping("/delete/{id}")
    public Response deleteWebStory(@PathVariable("id") int id) {
        return webStoryService.deleteWebStory(id);
    }

    @GetMapping(value = "/{slug}", produces = "text/html")
    public String getWebStory(@PathVariable("slug") String slug) {
        return webStoryService.webStory(slug);
    }
}
