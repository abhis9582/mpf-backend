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
            if(webStory.getWebStoryCategory() != null) {
                webStoryDto.setCategoryName(webStory.getWebStoryCategory().getCategoryName());
                webStoryDto.setCategoryId(webStory.getWebStoryCategory().getId());
            }
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
        if (storyImage != null && !storyImage.isEmpty()) {
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
                webStory.setStoryDescription(webStoryDto.getStoryDescription());
                if (!savedImageName.isEmpty() && !webStory.getStoryImage().isEmpty()) {
                    fileUtils.deleteFileFromDestination(webStory.getStoryImage(), webStoryPath);
                    webStory.setStoryImage(savedImageName);
                }
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
    public Response deleteWebStory(int id) {
        Optional<WebStory> webStory = webStoryRepository.findById(id);
        String webStoryPath = uploadDir.concat("/web-story");
        try {
            if (webStory.isPresent()) {
                if (!webStory.get().getStoryImage().isEmpty()) {
                    fileUtils.deleteFileFromDestination(webStory.get().getStoryImage(), webStoryPath);
                }
            }
            webStoryRepository.deleteById(id);
            return new Response(1, "Story deleted successfully...", 0);
        }catch (Exception e){
            return new Response(0, e.getMessage(), 0);
        }
    }

    @Override
    @Transactional
    public String webStory(String slug) {
        Optional<WebStoryCategory> webStoryCategory = webStoryCategoryRepository.findByCategoryName(slug);
        List<WebStory> storyPages = new ArrayList<>();
        String categoryTitle = "Web Story"; // fallback title

        if (webStoryCategory.isPresent()) {
            WebStoryCategory category = webStoryCategory.get();
            storyPages = category.getWebStories();
            categoryTitle = category.getCategoryName();
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
                    
                        <!-- Logo Layer -->
                        <amp-story-grid-layer template="vertical" class="logo-layer">
                          <a href="https://mypropertyfact.in/" target="_blank">
                            <amp-img src="https://mypropertyfact.in/logo.png" width="60" height="60" layout="fixed" alt="Logo"></amp-img>
                          </a>
                        </amp-story-grid-layer>
                    
                        <!-- Text Layer -->
                        <amp-story-grid-layer template="vertical" class="text-layer">
                          <div class="text-overlay">
                            <div animate-in="fade-in" animate-in-duration="1s">
                              <h1>%3$s</h1>
                            </div>
                            <div animate-in="fade-in" animate-in-delay="0.5s" animate-in-duration="1s">
                              <p>%4$s</p>
                            </div>
                          </div>
                        </amp-story-grid-layer>
                      </amp-story-page>
                    """.formatted(pageCount, imageUrl, page.getStoryTitle(), page.getStoryDescription()));

            pageCount++;
        }

        return """
                    <!doctype html>
                    <html>
                    <head>
                      <meta charset="utf-8">
                      <title>%1$s</title>
                      <link rel="canonical" href="self.html" />
                      <meta name="viewport" content="width=device-width,minimum-scale=1,initial-scale=1">
                      <link rel="icon" href="https://mypropertyfact.in/favicon.ico" type="image/x-icon">
                      <style amp-boilerplate>body{visibility:hidden}</style>
                      <script async src="https://cdn.ampproject.org/v0.js"></script>
                      <script async custom-element="amp-story" src="https://cdn.ampproject.org/v0/amp-story-1.0.js"></script>
                
                      <style amp-custom>
                                @import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@700&family=Raleway:wght@300&display=swap');
                
                                 .text-overlay h1 {
                                   font-family: 'Montserrat', sans-serif;
                                   font-weight: 700;
                                   font-size: 24px;
                                   color: white;
                                   margin-bottom: 10px;
                                 }
                
                                 .text-overlay p {
                                   font-family: 'Raleway', sans-serif;
                                   font-weight: 300;
                                   font-size: 16px;
                                   color: white;
                                 }
                                 .logo-layer {
                                   align-items: flex-start;
                                   justify-content: flex-start;
                                   padding: 20px;
                                 }
                
                                 .logo-layer amp-img {
                                   border-radius: 8px;
                                   box-shadow: 0 0 8px rgba(0,0,0,0.3);
                                 }
                
                                .text-layer {
                                   align-items: flex-end;
                                   justify-content: flex-start;
                                   padding: 0 20px 40px 20px;
                                 }
                
                                 .text-overlay {
                                 position: absolute;
                                 bottom: 0px;
                                   background: rgba(0, 0, 0, 0.5);
                                   padding: 16px 20px;
                                   max-width: 100%%;
                                 animation: fadeInUp 1s ease-out forwards;
                                 animation-delay: 0.3s;
                                 }
                
                                 .text-overlay h1 {
                                   font-size: 24px;
                                   margin-bottom: 10px;
                                   color: #fff;
                                 }
                
                                 .text-overlay p {
                                   font-size: 16px;
                                   color: #fff;
                                 }
                              </style>
                
                    </head>
                    <body>
                      <amp-story standalone title="%1$s" publisher="You"
                        publisher-logo-src="https://mypropertyfact.in/logo.png"
                        poster-portrait-src="https://mypropertyfact.in/logo.png">
                        %2$s
                      </amp-story>
                    </body>
                    </html>
                """.formatted(categoryTitle, storyContent.toString());
    }
}
