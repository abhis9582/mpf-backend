package com.mypropertyfact.estate.configs.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
@Data
public class ProjectGalleryDto {
    private int projectId;
    private MultipartFile image;
    private String galleyImage;
}
