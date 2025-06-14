package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.WebStoryDto;
import com.mypropertyfact.estate.entities.WebStory;
import com.mypropertyfact.estate.entities.WebStoryCategory;
import com.mypropertyfact.estate.interfaces.WebStoryService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.WebStoryCategoryRepository;
import com.mypropertyfact.estate.repositories.WebStoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WebStoryServiceImpl implements WebStoryService {

    @Autowired
    private WebStoryRepository webStoryRepository;

    @Autowired
    private WebStoryCategoryRepository webStoryCategoryRepository;

    @Autowired
    private FileUtils fileUtils;

    @Value("${upload_dir}")
    private String uploadDir;

    @Value("${baseUrl}")
    private String baseUrl;

    @Override
    public List<WebStoryDto> getAllWebStories() {
        List<WebStory> webStories = webStoryRepository.findAll();
        return webStories.stream().map(webStory -> {
            WebStoryDto webStoryDto = new WebStoryDto();
            webStoryDto.setId(webStory.getId());
            webStoryDto.setStoryTitle(webStory.getStoryTitle());
            webStoryDto.setStoryDescription(webStory.getStoryDescription());
            webStoryDto.setCategoryName(webStory.getWebStoryCategory().getCategoryName());
            webStoryDto.setCategoryId(webStory.getWebStoryCategory().getId());
            webStoryDto.setStoryImage(webStory.getStoryImage());
            return webStoryDto;
        }).toList();
    }

    @Override
    public Response addUpdateWebStory(MultipartFile storyImage, WebStoryDto webStoryDto) {
        Response response = new Response();
        String webStoryPath = uploadDir.concat("/web-story");
        String savedImageName = "";
        Optional<WebStoryCategory> webStoryCategory = webStoryCategoryRepository.findById(webStoryDto.getCategoryId());
        if (!storyImage.isEmpty()) {
            if (!fileUtils.isTypeImage(storyImage)) {
                response.setMessage("File should be type of image only");
                return response;
            }
            String renameImage = fileUtils.renameFile(storyImage, UUID.randomUUID().toString());
            savedImageName = fileUtils.saveFile(storyImage, renameImage, webStoryPath, 720, 1280, 1.0f);
        }
        if (webStoryDto.getId() > 0) {
            Optional<WebStory> savedWebStory = webStoryRepository.findById(webStoryDto.getId());
            if (savedWebStory.isPresent()) {
                WebStory webStory = savedWebStory.get();
                webStory.setStoryTitle(webStoryDto.getStoryTitle());
                webStory.setStoryDescription(webStory.getStoryDescription());
                if (!savedImageName.isEmpty() && !webStory.getStoryImage().isEmpty()) {
                    fileUtils.deleteFileFromDestination(webStory.getStoryImage(), webStoryPath);
                }
                webStory.setStoryImage(savedImageName);
                webStoryCategory.ifPresent(webStory::setWebStoryCategory);
                webStoryRepository.save(webStory);
                response.setMessage("Web story updated successfully...");
                response.setIsSuccess(1);
            }
        } else {
            WebStory webStory = new WebStory();
            webStory.setStoryTitle(webStoryDto.getStoryTitle());
            webStory.setStoryDescription(webStoryDto.getStoryDescription());
            webStoryCategory.ifPresent(webStory::setWebStoryCategory);
            webStory.setStoryImage(savedImageName);
            webStoryRepository.save(webStory);
            response.setIsSuccess(1);
            response.setMessage("Web story saved successfully...");
        }
        return response;
    }

    @Override
    public void deleteWebStory(int id) {

    }

    @Override
    @Transactional
    public String webStory(String slug) {
        Optional<WebStoryCategory> webStoryCategory = webStoryCategoryRepository.findByCategoryName(slug);
        List<WebStory> storyPages = new ArrayList<>();
        if (webStoryCategory.isPresent()) {
            WebStoryCategory category = webStoryCategory.get();
            storyPages = category.getWebStories();
        }

        StringBuilder storyContent = new StringBuilder();
        int pageCount = 1;

        for (WebStory page : storyPages) {
            String imageUrl = baseUrl + "web-story/" + page.getStoryImage();
            storyContent.append("""
                    <amp-story-page id="page%1$d" auto-advance-after="7s">
                      <amp-story-grid-layer template="fill">
                        <amp-img src="%2$s" layout="fill" object-fit="cover" alt=""></amp-img>
                      </amp-story-grid-layer>
                      <amp-story-grid-layer template="vertical">
                        <h1>%3$s</h1>
                        <p>%4$s</p>
                      </amp-story-grid-layer>
                    </amp-story-page>
                    """.formatted(pageCount, imageUrl, page.getStoryTitle(), page.getStoryDescription()));
            pageCount++;
        }

        return """
                <!doctype html>
                <html ⚡>
                <head>
                  <meta charset="utf-8">
                  <title>Dynamic Web Story</title>
                  <link rel="canonical" href="self.html" />
                  <meta name="viewport" content="width=device-width,minimum-scale=1,initial-scale=1">
                  <style amp-boilerplate>body{visibility:hidden}</style>
                  <script async src="https://cdn.ampproject.org/v0.js"></script>
                  <script async custom-element="amp-story" src="https://cdn.ampproject.org/v0/amp-story-1.0.js"></script>
                </head>
                <body>
                  <amp-story standalone title="Web Story" publisher="You"
                    publisher-logo-src="https://example.com/logo.png"
                    poster-portrait-src="https://example.com/poster.jpg">
                    %s
                  </amp-story>
                </body>
                </html>
                """.formatted(storyContent.toString());
    }
}
