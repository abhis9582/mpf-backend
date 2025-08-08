package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.common.FileUtils;
import com.mypropertyfact.estate.configs.dtos.LocalityDto;
import com.mypropertyfact.estate.entities.City;
import com.mypropertyfact.estate.entities.Locality;
import com.mypropertyfact.estate.entities.ProjectTypes;
import com.mypropertyfact.estate.interfaces.LocalityService;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CityRepository;
import com.mypropertyfact.estate.repositories.LocalityRepository;
import com.mypropertyfact.estate.repositories.ProjectTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LocalityServiceImpl implements LocalityService {

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    @Autowired
    private FileUtils fileUtils;

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
            if(locality.getCity() != null) {
                localityDto.setCityId(locality.getCity().getId());
                localityDto.setCityName(locality.getCity().getName());
                if(locality.getCity().getState() != null) {
                    localityDto.setStateName(locality.getCity().getState().getStateName());
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
