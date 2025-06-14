package com.mypropertyfact.estate.configs.dtos;

import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class CityDto {
    private int id;
    private int stateId;
    private String metaTitle;
    private String metaKeyWords;
    private String metaDescription;
    private String name;
    private String cityDisc;
    private String cityImage;
    private String slugUrl;
}
