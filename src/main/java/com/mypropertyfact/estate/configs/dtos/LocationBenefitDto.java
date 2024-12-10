package com.mypropertyfact.estate.configs.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationBenefitDto {
    private int id;
    private String distance;
    private String benefitName;
    private String image;
    private String projectName;
    private int projectId;
    private MultipartFile iconImage;
    private String slugUrl;

    public LocationBenefitDto(int id, String distance, String bName, String image, String pName){
        this.id = id;
        this.distance = distance;
        this.benefitName = bName;
        this.image = image;
        this.projectName = pName;
    }
}
