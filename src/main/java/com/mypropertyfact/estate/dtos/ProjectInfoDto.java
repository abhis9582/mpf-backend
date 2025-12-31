package com.mypropertyfact.estate.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectInfoDto {
    private int id;
    private String builderName;
    private String projectName;
    private String projectType;
    private String projectStatus;
    private String projectPrice;
    private String projectAddress;
    private String slugURL;
    private String projectConfiguration;
    private String projectThumbnailImage;
    private boolean status;
    private String thumbNailAltTag;
}
