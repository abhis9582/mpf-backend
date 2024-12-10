package com.mypropertyfact.estate.configs.dtos;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
@Data
public class PropertyDto {
    private String prop_name;
    private String type;
    private String about;
    private String floor_desc;
    private String location_desc;
    private String location_map_link;
    private String logoPath;
    private MultipartFile file;
}
