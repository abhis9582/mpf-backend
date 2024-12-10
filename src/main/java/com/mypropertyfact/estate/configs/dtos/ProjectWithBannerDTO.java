package com.mypropertyfact.estate.configs.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProjectWithBannerDTO {
    private int id;
    private String projectName;
    private String price;
    private String location;
    private String image;
    private String slugURL;
}
