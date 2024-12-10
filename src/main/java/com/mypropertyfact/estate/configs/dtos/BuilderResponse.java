package com.mypropertyfact.estate.configs.dtos;

import com.mypropertyfact.estate.entities.Builder;
import com.mypropertyfact.estate.models.Response;
import lombok.Data;

import java.util.List;
@Data
public class BuilderResponse {
    private List<Builder> builders;
    private Response response;
}
