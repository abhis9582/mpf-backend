package com.mypropertyfact.estate.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AmenityDetailedDto {

    private int id;
    private String amenityName;
    private String amenityAltTag;
    private String amenityImage;
    private List<MultipartFile> amenitiesFiles;
    private List<Integer> deletedAmenitiesIds;

}
