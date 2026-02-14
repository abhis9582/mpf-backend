package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.dtos.LocalityDto;
import com.mypropertyfact.estate.dtos.CityDto;
import com.mypropertyfact.estate.dtos.StateDto;
import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.entities.Country;
import com.mypropertyfact.estate.entities.Locality;
import com.mypropertyfact.estate.entities.State;
import com.mypropertyfact.estate.interfaces.StateService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CountryRepository;
import com.mypropertyfact.estate.repositories.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StateServiceImpl implements StateService {

    private final StateRepository stateRepository;

    private final CountryRepository countryRepository;

    @Override
    public Response addUpdate(StateDto stateDto) {
        Optional<Country> countryById = countryRepository.findById(stateDto.getCountryId());
        if (stateDto.getId() > 0) {
            Optional<State> savedState = stateRepository.findById(stateDto.getId());
            savedState.ifPresent(state -> {
                state.setStateName(stateDto.getStateName());
                state.setDescription(stateDto.getStateDescription());
                countryById.ifPresent(state::setCountry);
                stateRepository.save(state);
            });
            return new Response(1, "State updated successfully...", 0);
        }
        State state = new State();
        state.setStateName(stateDto.getStateName());
        state.setDescription(stateDto.getStateDescription());
        countryById.ifPresent(state::setCountry);
        stateRepository.save(state);
        return new Response(1, "State saved successfully...", 0);
    }

    @Override
    public void deleteState(int id) {
        stateRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<StateDto> getAll() {
        List<State> stateList = stateRepository.findAll();
        List<StateDto> stateDtoList;
        stateDtoList = stateList.stream()
                .filter(Objects::nonNull).map(state -> {
                    StateDto stateDto = new StateDto();
                    stateDto.setId(state.getId());
                    stateDto.setStateName(state.getStateName());
                    stateDto.setStateDescription(state.getDescription());
                    List<CityDto> cityDtoList = new ArrayList<>();
                    if (state.getCities() != null) {
                        List<City> cities = state.getCities();
                        cityDtoList = cities.stream().map(city-> {
                            CityDto cityDto = new CityDto();
                            cityDto.setId(city.getId());
                            cityDto.setCityName(city.getName());
                            cityDto.setCityDescription(city.getCityDisc());
                            cityDto.setStateId(state.getId());
                            cityDto.setStateName(state.getStateName());
                            
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
                                            if (state.getCountry() != null) {
                                                localityDto.setCountryId(state.getCountry().getId());
                                                localityDto.setCountryName(state.getCountry().getCountryName());
                                            }
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
                        stateDto.setCityList(cityDtoList); // finally set the cities
                    }
                    if (state.getCountry() != null) {
                        Country country = state.getCountry();
                        stateDto.setCountryId(country.getId());
                        stateDto.setCountryName(country.getCountryName());
                    }
                    return stateDto;
                }).toList();
        return stateDtoList;
    }
}
