package com.mypropertyfact.estate.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProjectGalleryDto {
    private int id;
    private List<Integer> deletedImageIds;
    private List<MultipartFile> galleryImageList;
    private int projectId;
    private MultipartFile image;
    private String galleyImage;
}
