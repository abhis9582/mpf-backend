package com.mypropertyfact.estate.configs.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FloorPlansDto {
    private int projectId;
    private String pName;
    private String type;
    private Double areaSq;
    private Double areaMt;
    private int floorId;
}
