package com.mypropertyfact.estate.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FeatureDetailedDto {

    private Long id;
    private String featureName;
    private String featureAltTag;
    private String featureImage;
    private List<MultipartFile> featuresFiles;
    private List<Long> deletedFeaturesIds;

}
