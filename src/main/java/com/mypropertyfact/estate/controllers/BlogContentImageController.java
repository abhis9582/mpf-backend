package com.mypropertyfact.estate.controllers;

import com.mypropertyfact.estate.entities.BlogContentImage;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.interfaces.BlogContentImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/blog-image")
public class BlogContentImageController {

    @Autowired
    private BlogContentImageService blogContentImageService;

    @GetMapping("/get")
    public List<BlogContentImage> getAllContentImageList() {
        return this.blogContentImageService.getAllImageList();
    }

    @PostMapping("/post")
    public ResponseEntity<Response> addUpdateContentImage(@RequestParam(required = false)
                                                          MultipartFile file,
                                                          @ModelAttribute BlogContentImage blogContentImage) {
        return new ResponseEntity<>(blogContentImageService.addUpdateContentImage(file, blogContentImage), HttpStatus.OK);
    }
}
