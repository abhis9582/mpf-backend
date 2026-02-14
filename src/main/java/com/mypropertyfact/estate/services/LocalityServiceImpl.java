package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.dtos.LocalityDto;
import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.entities.Locality;
import com.mypropertyfact.estate.entities.ProjectTypes;
import com.mypropertyfact.estate.interfaces.LocalityService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CityRepository;
import com.mypropertyfact.estate.repositories.LocalityRepository;
import com.mypropertyfact.estate.repositories.ProjectTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocalityServiceImpl implements LocalityService {

    private final LocalityRepository localityRepository;

    private final CityRepository cityRepository;

    private final ProjectTypeRepository projectTypeRepository;

    private final FileUtils fileUtils;

    @Transactional
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
            localityDto.setAveragePricePerSqFt(locality.getAveragePricePerSqFt());
            localityDto.setIsActive(locality.isActive());
            if(locality.getCity() != null) {
                City city = locality.getCity();
                localityDto.setCityId(city.getId());
                localityDto.setCityName(city.getName());
                if(city.getState() != null) {
                    localityDto.setStateId(city.getState().getId());
                    localityDto.setStateName(city.getState().getStateName());
                    if(city.getState().getCountry() != null) {
                        localityDto.setCountryId(city.getState().getCountry().getId());
                        localityDto.setCountryName(city.getState().getCountry().getCountryName());
                    }
                }
            }
            if(locality.getProjectTypes() != null) {
                localityDto.setLocalityCategory(locality.getProjectTypes().getId());
                localityDto.setLocalityCategoryName(locality.getProjectTypes().getProjectTypeName());
            }
            return localityDto;
        }).toList();
    }
    @Transactional
    @Override
    public Response addUpdateLocality(LocalityDto localityDto) {
        Response response = new Response();
        if(localityDto.getId() != 0) {
            Optional<Locality> localityById = localityRepository.findById(localityDto.getId());
            localityById.ifPresent(locality-> {
                Locality newLocality = mapToEntity(locality, localityDto);
                if(newLocality != null) {
                    localityRepository.save(newLocality);
                    response.setIsSuccess(1);
                    response.setMessage("Locality updated successfully...");
                }else{
                    response.setMessage("Data not saved");
                }
            });
        }else{
            Locality locality = new Locality();
            String slug = fileUtils.generateSlug(localityDto.getLocalityName());
            localityDto.setSlug(slug);
            Locality locality1 = mapToEntity(locality, localityDto);
            localityRepository.save(locality1);
            response.setIsSuccess(1);
            response.setMessage("Locality saved successfully...");
        }
        return response;
    }

    @Override
    public Response deleteLocality(long id) {
        localityRepository.deleteById(id);
        return new Response(1, "Locality deleted successfully...", 0);
    }

     public Locality mapToEntity(Locality locality, LocalityDto localityDto) {
        if(localityDto != null) {
            Optional<City> city = cityRepository.findById(localityDto.getCityId());
            Optional<ProjectTypes> projectTypes = projectTypeRepository.findById(localityDto.getLocalityCategory());
            locality.setAveragePricePerSqFt(localityDto.getAveragePricePerSqFt());
            locality.setLocalityName(localityDto.getLocalityName());
            city.ifPresent(locality::setCity);
            projectTypes.ifPresent(locality::setProjectTypes);
            locality.setDescription(localityDto.getDescription());
            if(!localityDto.getSlug().isEmpty()) {
                locality.setSlug(localityDto.getSlug());
            }
            return locality;
        }
        return null;
     }
}
