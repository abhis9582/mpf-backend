package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.WebStoryDto;
import com.mypropertyfact.estate.models.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WebStoryService {
    List<WebStoryDto> getAllWebStories();
    Response addUpdateWebStory(MultipartFile storyImage, WebStoryDto webStoryDto);
    Response deleteWebStory(int id);
    String webStory(String slug);
}
