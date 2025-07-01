package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.LocalityDto;
import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.entities.Locality;
import com.mypropertyfact.estate.interfaces.LocalityService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CityRepository;
import com.mypropertyfact.estate.repositories.LocalityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocalityServiceImpl implements LocalityService {

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private CityRepository cityRepository;

    @Override
    public List<LocalityDto> getAllLocalities() {
        List<Locality> localities = localityRepository.findAllWithCity();
        return localities.stream().map(locality-> {
            LocalityDto localityDto = new LocalityDto();
            localityDto.setId(locality.getId());
            localityDto.setLocalityName(locality.getLocalityName());
            localityDto.setDescription(locality.getDescription());
            localityDto.setLatitude(locality.getLatitude());
            localityDto.setSlug(locality.getSlug());
            localityDto.setLongitude(locality.getLongitude());
            localityDto.setPinCode(locality.getPinCode());
            if(locality.getCity() != null) {
                localityDto.setCityId(locality.getCity().getId());
                localityDto.setCityName(locality.getCity().getName());
                if(locality.getCity().getState() != null) {
                    localityDto.setStateName(locality.getCity().getState().getStateName());
                }
            }
            return localityDto;
        }).toList();
    }

    @Override
    public Response addUpdateLocality(LocalityDto localityDto) {
        if(localityDto.getId() != 0) {
            Optional<Locality> localityById = localityRepository.findById(localityDto.getId());
            localityById.ifPresent(locality-> {

            });
        }else{

        }
        return null;
    }

    @Override
    public Response deleteLocality(long id) {
        return null;
    }

     public Response mapToEntity(Locality locality, LocalityDto localityDto) {
        if(localityDto != null) {
            Optional<City> city = cityRepository.findById(localityDto.getCityId());
            locality.setAveragePricePerSqFt(localityDto.getAveragePricePerSqFt());
            locality.setLocalityName(localityDto.getLocalityName());
            city.ifPresent(locality::setCity);
            locality.setDescription(localityDto.getDescription());
            return new Response(1, "Mapped with entity");
        }
        return new Response(0, "Error Occurred");
     }
}
