package com.mypropertyfact.estate.interfaces;

import com.mypropertyfact.estate.dtos.CountryDto;
import com.mypropertyfact.estate.models.Response;

import java.util.List;

public interface CountryService {
    Response addUpdate(CountryDto countryDto);
    Response deleteCountry(int id);
    List<CountryDto> getAllCountry();
}
