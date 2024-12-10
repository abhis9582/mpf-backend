package com.mypropertyfact.estate.configs.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class AmenityDto {
    private String title;
    private String altTag;
    private MultipartFile amenityImage;
}
