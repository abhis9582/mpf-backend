package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.BlogContentImage;
import com.mypropertyfact.estate.models.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogContentImageService {
    Response addUpdateContentImage(MultipartFile file, BlogContentImage blogContentImage);
    List<BlogContentImage> getAllImageList();
    Response deleteImage(int id);
}
