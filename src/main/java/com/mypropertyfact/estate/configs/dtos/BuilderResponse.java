package com.mypropertyfact.estate.configs.dtos;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.projections.BuilderView;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class BuilderResponse {
    private List<BuilderView> builders;
    private Response response;
}
