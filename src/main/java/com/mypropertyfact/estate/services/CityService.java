package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.ConstantMessages;
import com.mypropertyfact.estate.common.CommonMapper;
import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.dtos.CityDto;
import com.mypropertyfact.estate.dtos.ProjectDetailDto;
import com.mypropertyfact.estate.entities.*;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.projections.CityView;
import com.mypropertyfact.estate.repositories.CityRepository;
import com.mypropertyfact.estate.repositories.DistrictRepository;
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
    private final DistrictRepository districtRepository;
    private final CommonMapper commonMapper;
    private final FileUtils fileUtils;

    public CityService(CityRepository cityRepository, StateRepository stateRepository,
                       FileUtils fileUtils, CommonMapper commonMapper, DistrictRepository districtRepository) {
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
        this.fileUtils = fileUtils;
        this.commonMapper = commonMapper;
        this.districtRepository = districtRepository;
    }

//    public List<CityView> getAllCities() {
//        return cityRepository.findAllProjectedBy(Sort.by(Sort.Direction.ASC, "name"));
//    }

    public List<CityDto> getAllCities(){
        List<City> cities = cityRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        return cities.stream().map(city -> {
            CityDto cityDto = new CityDto();
            commonMapper.mapCityDtoToCity(cityDto, city);
            return cityDto;
        }).toList();
    }

    public List<Map<String, Object>> getAllCityList() {
        List<City> cities = cityRepository.findAll();
        return cities.stream().map(city-> {
            Map<String, Object> cityObj = new HashMap<>();
            cityObj.put("id", city.getId());
            cityObj.put("name", city.getName());
            if(city.getDistrict().getState() != null) {
                if(city.getDistrict().getState().getCountry() != null) {
                    cityObj.put("countryName", city.getDistrict().getState().getCountry().getCountryName());
                    cityObj.put("countryId", city.getDistrict().getState().getCountry().getId());
                }
                cityObj.put("stateName", city.getDistrict().getState().getStateName());
                cityObj.put("stateId", city.getDistrict().getState().getId());
            }
            cityObj.put("metaDescription", city.getMetaDescription());
            cityObj.put("metaTitle", city.getMetaTitle());
            cityObj.put("metaKeyWords", city.getMetaKeyWords());
            cityObj.put("cityDisc", city.getCityDisc());
            return cityObj;
        }).toList();
    }

    public Response postNewCity(CityDto cityDto) {
            City existingCity = this.cityRepository.findByName(cityDto.getCityName());
            if (existingCity != null && existingCity.getId() != cityDto.getId()) {
                return new Response(0, ConstantMessages.CITY_EXISTS, 0);
            }
            cityDto.setSlugURL(fileUtils.generateSlug(cityDto.getCityName()));
            Optional<District> district = districtRepository.findById(cityDto.getStateId());
            if (cityDto.getId() != 0) {
                Optional<City> savedCity = cityRepository.findById(cityDto.getDistrictId());
                savedCity.ifPresent(city-> {
                    district.ifPresent(city::setDistrict);
                    commonMapper.mapCityToCityDto(city, cityDto);
                    cityRepository.save(city);
                });
                return new Response(1, ConstantMessages.CITY_UPDATED, 0);
            } else {
                City city = new City();
                district.ifPresent(city::setDistrict);
                commonMapper.mapCityToCityDto(city, cityDto);
                cityRepository.save(city);
                return new Response(1, ConstantMessages.CITY_ADDED, 0);
            }
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
    public CityDto getBySlug(String url) {
        Optional<City> dbCity = this.cityRepository.findBySlugUrl(url);
        Map<String, Object> resObj = new HashMap<>();
        CityDto cityDetailDto = new CityDto();
        dbCity.ifPresent(city -> {
            cityDetailDto.setId(city.getId());
            cityDetailDto.setCityName(city.getName());
            cityDetailDto.setMetaTitle(city.getMetaTitle());
            cityDetailDto.setMetaKeywords(city.getMetaKeyWords());
            cityDetailDto.setMetaDescription(city.getMetaDescription());
            if(city.getDistrict().getState() != null) {
                State state = city.getDistrict().getState();
                cityDetailDto.setStateId(state.getId());
                cityDetailDto.setStateName(state.getStateName());
                if(state.getCountry() != null) {
                    Country country = state.getCountry();
                    cityDetailDto.setCountryId(country.getId());
                    cityDetailDto.setCountryName(country.getCountryName());
                }
            }
            List<ProjectDetailDto> projectDetailDtoList = new ArrayList<>();
            if(city.getProjects() != null) {
                List<Project> projects = city.getProjects();
                projectDetailDtoList = projects.stream()
                        .sorted(Comparator.comparing(Project::getProjectName, String.CASE_INSENSITIVE_ORDER))
                        .map(project-> {
                    ProjectDetailDto projectDetailDto = new ProjectDetailDto();
                    commonMapper.mapProjectToProjectDto(project, projectDetailDto);
                    return projectDetailDto;
                }).toList();
            }
            cityDetailDto.setProjectList(projectDetailDtoList);
            cityDetailDto.setCityDescription(city.getCityDisc());
            cityDetailDto.setCityImage(city.getCityImage());
        });
        return cityDetailDto;
    }

    public Response addUpdateCity(MultipartFile cityImage, City city) {
        Response response = new Response();
        return response;
    }
}
