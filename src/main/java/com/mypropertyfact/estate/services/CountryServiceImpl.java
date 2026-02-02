package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.LocalityDto;
import com.mypropertyfact.estate.dtos.CityDto;
import com.mypropertyfact.estate.dtos.CountryDto;
import com.mypropertyfact.estate.dtos.StateDto;
import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.entities.Country;
import com.mypropertyfact.estate.entities.Locality;
import com.mypropertyfact.estate.entities.State;
import com.mypropertyfact.estate.interfaces.CountryService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public Response addUpdate(CountryDto countryDto) {
        if (countryDto.getId() > 0) {
            Optional<Country> savedCountry = countryRepository.findById(countryDto.getId());
            savedCountry.ifPresent(dbCountry -> {
                dbCountry.setCountryName(countryDto.getCountryName());
                dbCountry.setContinent(countryDto.getContinent());
                dbCountry.setDescription(countryDto.getCountryDescription());
                countryRepository.save(dbCountry);
            });
            return new Response(1, "Country updated successfully...", 0);
        }
        Country country = new Country();
        country.setCountryName(countryDto.getCountryName());
        country.setContinent(countryDto.getContinent());
        country.setDescription(countryDto.getCountryDescription());
        countryRepository.save(country);
        return new Response(1, "Country saved successfully...", 0);
    }

    @Override
    public Response deleteCountry(int id) {
        countryRepository.deleteById(id);
        return new Response(1, "Country deleted successfully...", 0);
    }

    @Transactional
    @Override
    public List<CountryDto> getAllCountry() {
        List<Country> countries = countryRepository.findAll(Sort.by(Sort.Direction.ASC, "countryName"));
        List<CountryDto> countryDtoList = new ArrayList<>();
        countryDtoList = countries.stream().map(country -> {
            CountryDto countryDto = new CountryDto();
            countryDto.setId(country.getId());
            countryDto.setCountryName(country.getCountryName());
            countryDto.setContinent(country.getContinent());
            countryDto.setCountryDescription(country.getDescription());
            List<StateDto> stateDtoList = new ArrayList<>();
            if (country.getStates() != null) {
                List<State> states = country.getStates();
                stateDtoList = states.stream()
                        .sorted(Comparator.comparing(State::getStateName, String::compareToIgnoreCase))
                        .map(state -> {
                            StateDto stateDto = new StateDto();
                            stateDto.setId(state.getId());
                            stateDto.setStateName(state.getStateName());
                            stateDto.setStateDescription(state.getDescription());
                            List<CityDto> cityDtoList = new ArrayList<>();
                            if (state.getCities() != null) {
                                List<City> cities = state.getCities();
                                cityDtoList = cities.stream().map(city -> {
                                    CityDto cityDto = new CityDto();
                                    cityDto.setId(city.getId());
                                    cityDto.setCityName(city.getName());
                                    cityDto.setStateId(state.getId());
                                    cityDto.setStateName(state.getStateName());
                                    cityDto.setCountryId(country.getId());
                                    cityDto.setCountryName(country.getCountryName());
                                    
                                    // Include localities for each city
                                    List<LocalityDto> localityDtoList = new ArrayList<>();
                                    if (city.getLocalities() != null) {
                                        List<Locality> localities = city.getLocalities();
                                        localityDtoList = localities.stream()
                                                .sorted(Comparator.comparing(Locality::getLocalityName, String::compareToIgnoreCase))
                                                .map(locality -> {
                                                    LocalityDto localityDto = new LocalityDto();
                                                    localityDto.setId(locality.getId());
                                                    localityDto.setLocalityName(locality.getLocalityName());
                                                    localityDto.setSlug(locality.getSlug());
                                                    localityDto.setLatitude(locality.getLatitude());
                                                    localityDto.setLongitude(locality.getLongitude());
                                                    localityDto.setPinCode(locality.getPinCode());
                                                    localityDto.setDescription(locality.getDescription());
                                                    localityDto.setAveragePricePerSqFt(locality.getAveragePricePerSqFt());
                                                    localityDto.setIsActive(locality.isActive());
                                                    localityDto.setCityId(city.getId());
                                                    localityDto.setCityName(city.getName());
                                                    localityDto.setStateId(state.getId());
                                                    localityDto.setStateName(state.getStateName());
                                                    localityDto.setCountryId(country.getId());
                                                    localityDto.setCountryName(country.getCountryName());
                                                    if (locality.getProjectTypes() != null) {
                                                        localityDto.setLocalityCategory(locality.getProjectTypes().getId());
                                                        localityDto.setLocalityCategoryName(locality.getProjectTypes().getProjectTypeName());
                                                    }
                                                    return localityDto;
                                                }).toList();
                                    }
                                    cityDto.setLocalityList(localityDtoList);
                                    return cityDto;
                                }).toList();
                            }
                            stateDto.setCityList(cityDtoList);
                            return stateDto;
                        }).toList();
            }
            countryDto.setStateList(stateDtoList);
            return countryDto;
        }).toList();
        return countryDtoList;
    }
}
