package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.configs.dtos.LocalityDto;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public interface LocalityService {

    List<LocalityDto> getAllLocalities();
    Response addUpdateLocality(LocalityDto localityDto);
    Response deleteLocality(long id);
}
