package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.ConstantMessages;
import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.CityDto;
import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.entities.State;
import com.mypropertyfact.estate.models.ProjectDto;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.projections.CityView;
import com.mypropertyfact.estate.repositories.CityRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import com.mypropertyfact.estate.repositories.StateRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final StateRepository stateRepository;

    private final FileUtils fileUtils;
    public CityService(CityRepository cityRepository, StateRepository stateRepository,
                       FileUtils fileUtils) {
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
        this.fileUtils = fileUtils;
    }

    public List<CityView> getAllCities() {
        return cityRepository.findAllProjectedBy(Sort.by(Sort.Direction.ASC, "name"));
    }

    public List<Map<String, Object>> getAllCityList() {
        List<City> cities = cityRepository.findAll();
        return cities.stream().map(city-> {
            Map<String, Object> cityObj = new HashMap<>();
            cityObj.put("id", city.getId());
            cityObj.put("name", city.getName());
            if(city.getState() != null) {
                if(city.getState().getCountry() != null) {
                    cityObj.put("countryName", city.getState().getCountry().getCountryName());
                    cityObj.put("countryId", city.getState().getCountry().getId());
                }
                cityObj.put("stateName", city.getState().getStateName());
                cityObj.put("stateId", city.getState().getId());
            }
            cityObj.put("metaDescription", city.getMetaDescription());
            cityObj.put("metaTitle", city.getMetaTitle());
            cityObj.put("metaKeyWords", city.getMetaKeyWords());
            cityObj.put("cityDisc", city.getCityDisc());
            return cityObj;
        }).toList();
    }

    public Response postNewCity(CityDto cityDto) {
        Response response = new Response();
        try {
            City existingCity = this.cityRepository.findByName(cityDto.getName());
            if (existingCity != null && existingCity.getId() != cityDto.getId()) {
                response.setMessage(ConstantMessages.CITY_EXISTS);
                return response;
            }
            cityDto.setSlugUrl(fileUtils.generateSlug(cityDto.getName()));
            Optional<State> state = stateRepository.findById(cityDto.getStateId());
            if (cityDto.getId() != 0) {
                Optional<City> savedCity = cityRepository.findById(cityDto.getId());
                savedCity.ifPresent(city-> {
                    state.ifPresent(city::setState);
                    city.setName(cityDto.getName());
                    city.setSlugUrl(cityDto.getSlugUrl());
                    city.setMetaTitle(cityDto.getMetaTitle());
                    city.setMetaKeyWords(cityDto.getMetaKeyWords());
                    city.setMetaDescription(cityDto.getMetaDescription());
                    city.setCityDisc(cityDto.getCityDisc());
                    cityRepository.save(city);
                    response.setIsSuccess(1);
                    response.setMessage(ConstantMessages.CITY_UPDATED);
                });
            } else {
                City city = new City();
                state.ifPresent(city::setState);
                city.setName(cityDto.getName());
                city.setSlugUrl(cityDto.getSlugUrl());
                city.setMetaTitle(cityDto.getMetaTitle());
                city.setMetaKeyWords(cityDto.getMetaKeyWords());
                city.setMetaDescription(cityDto.getMetaDescription());
                city.setCityDisc(cityDto.getCityDisc());
                cityRepository.save(city);
                response.setIsSuccess(1);
                response.setMessage(ConstantMessages.CITY_ADDED);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setIsSuccess(0);
        }
        return response;
    }

    public Response updateCity(int id, City city) {
        Response response = new Response();
        try {
            City cityById = (City) this.cityRepository.findById(id).get();
            if (cityById != null) {
                cityById.setName(city.getName());
                cityById.setState(city.getState());
                this.cityRepository.save(cityById);
                response.setIsSuccess(1);
                response.setMessage(ConstantMessages.CITY_UPDATED);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setIsSuccess(0);
        }
        return response;
    }

    public Response deleteCity(int id) {
        Response response = new Response();
        try {
            City city = (City) this.cityRepository.findById(id).get();
            if (city != null) {
                this.cityRepository.deleteById(id);
                response.setIsSuccess(1);
                response.setMessage(ConstantMessages.CITY_DELETED);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setIsSuccess(0);
        }
        return response;
    }

    public City getSingleCity(int id) {
        City city = new City();
        try {
            city = (City) this.cityRepository.findById(id).get();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return city;
    }

    @Transactional
    public Map<String, Object> getBySlug(String url) {
        Optional<City> dbCity = this.cityRepository.findBySlugUrl(url);
        Map<String, Object> resObj = new HashMap<>();
        dbCity.ifPresent(city -> {
            resObj.put("id", city.getId());
            resObj.put("name", city.getName());
            resObj.put("metaTitle", city.getMetaTitle());
            resObj.put("metaKeywords", city.getMetaKeyWords());
            resObj.put("metaDescription", city.getMetaDescription());
            if(city.getState() != null) {
                resObj.put("stateId", city.getState().getId());
                resObj.put("stateName", city.getState().getStateName());
                if(city.getState().getCountry() != null) {
                    resObj.put("countryId", city.getState().getCountry().getId());
                    resObj.put("countryName", city.getState().getCountry().getCountryName());
                }
            }
            List<Map<String, Object>> projects = new ArrayList<>();
            projects = city.getProjects().stream().map(project -> {
                Map<String, Object> projectObj = new HashMap<>();
                projectObj.put("projectId", project.getId());
                projectObj.put("projectName", project.getProjectName());
                projectObj.put("projectAddress", project.getProjectLocality().concat(", ").concat(city.getName()));
                projectObj.put("projectThumbnail", project.getProjectThumbnail());
                projectObj.put("projectPrice", project.getProjectPrice());
                projectObj.put("slugURL", project.getSlugURL());
                if(project.getProjectTypes() != null) {
                    projectObj.put("typeName", project.getProjectTypes().getProjectTypeName());
                }
                return projectObj;
            }).toList();
            resObj.put("projects", projects);
            resObj.put("cityDesc", city.getCityDisc());
            resObj.put("cityImage", city.getCityImage());
        });
        return resObj;
    }

//    public List<Project> getByCityName(String cityName) {
//        return this.projectRepository.getAllByCity(cityName);
//    }

    public Response addUpdateCity(MultipartFile cityImage, City city) {
        Response response = new Response();
        return response;
    }
}
