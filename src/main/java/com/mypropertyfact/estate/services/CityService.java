package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.ConstantMessages;
import com.mypropertyfact.estate.configs.dtos.ProjectWithBannerDTO;
import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CityRepository;
import com.mypropertyfact.estate.repositories.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {

    private CityRepository cityRepository;
    private PropertyRepository propertyRepository;

    public CityService(CityRepository cityRepository, PropertyRepository propertyRepository) {
        this.cityRepository = cityRepository;
        this.propertyRepository = propertyRepository;
    }

    public List<City> getAllCities() {
        List<City> cityList = new ArrayList<>();
        try {
            cityList = this.cityRepository.findAll();
        } catch (Exception e) {
            cityList = new ArrayList<>();
        }
        return cityList;
    }

    public Response postNewCity(City city) {
        Response response = new Response();
        try {
            City existingCity = this.cityRepository.findByName(city.getName());
            if (existingCity != null && existingCity.getName().equals(city.getName()) && existingCity.getState().equals(city.getState())) {
                response.setMessage(ConstantMessages.CITY_EXISTS);
                return response;
            }
            String slugUrl = city.getName().toLowerCase(); // Convert to lowercase
            String[] words = slugUrl.split(" "); // Split the string by spaces
            StringBuilder result = new StringBuilder();

            // Iterate over the words
            for (int i = 0; i < words.length; i++) {
                result.append(words[i]); // Add the current word to the result
                // If it's not the last word, add a hyphen
                if (i < words.length - 1) {
                    result.append("-");
                }
            }
            // The result is the slug URL
            String finalSlug = result.toString();
            city.setSlugUrl(finalSlug);
            if (city.getId() != 0) {
                City dbCity = this.cityRepository.findById(city.getId()).get();
                if (dbCity != null) {
                    dbCity.setState(city.getState());
                    dbCity.setName(city.getName());
                    dbCity.setSlugUrl(finalSlug);
                    this.cityRepository.save(dbCity);
                    response.setIsSuccess(1);
                    response.setMessage(ConstantMessages.CITY_UPDATED);
                }
            } else {
                this.cityRepository.save(city);
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
    public City getBySlug(String url){
        return this.cityRepository.findBySlugUrl(url);
    }
    public List<ProjectWithBannerDTO> getByCityName(String cityName){
        List<Object[]> response = this.propertyRepository.getAllByCity(cityName);
        return response.stream().map(result -> new ProjectWithBannerDTO(
                (int) result[0],
                (String) result[1], // projectName
                (String) result[2],  // price
                (String) result[3],  // location
                (String) result[4],  // image (bannerUrl)
                (String) result[5]   // (slug)
        )).collect(Collectors.toList());
    }
}
