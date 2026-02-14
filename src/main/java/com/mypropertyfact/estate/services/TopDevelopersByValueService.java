package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.Headers;
import com.mypropertyfact.estate.entities.TopDevelopersByValue;
import com.mypropertyfact.estate.models.AggregationFromList;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.models.TopDevelopersByValueResponse;
import com.mypropertyfact.estate.repositories.HeaderRepository;
import com.mypropertyfact.estate.repositories.TopDevelopersByValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopDevelopersByValueService {
    private final TopDevelopersByValueRepository topDevelopersByValueRepository;
    private final HeaderRepository headerRepository;

    public List<TopDevelopersByValue> getAllTopDevelopersByValue(){
        List<Object[]> res = topDevelopersByValueRepository.getAllData();
        return res.stream().map(item->{
            TopDevelopersByValue response = new TopDevelopersByValue();
            response.setId((int)item[0]);
            response.setDeveloperName((String)item[1]);
            response.setNoOfTransactions((String) item[2]);
            response.setSaleRentValue((String) item[3]);
            return response;
        }).collect(Collectors.toList());
    }

    public Response addUpdateTopDevelopersByValue(TopDevelopersByValue topDevelopersByValue){
        Response response = new Response();
        try{
            if(topDevelopersByValue.getId() > 0){
                TopDevelopersByValue dbTopDevelopersByValue = topDevelopersByValueRepository.findById(topDevelopersByValue.getId()).orElse(null);
                if(dbTopDevelopersByValue != null){
                    dbTopDevelopersByValue.setDeveloperName(topDevelopersByValue.getDeveloperName());
                    dbTopDevelopersByValue.setSaleRentValue(topDevelopersByValue.getSaleRentValue());
                    dbTopDevelopersByValue.setCity(topDevelopersByValue.getCity());
                    dbTopDevelopersByValue.setLocality(topDevelopersByValue.getLocality());
                    dbTopDevelopersByValue.setLocation(topDevelopersByValue.getLocation());
                    dbTopDevelopersByValue.setPriority(topDevelopersByValue.getPriority());
                    dbTopDevelopersByValue.setNoOfTransactions(topDevelopersByValue.getNoOfTransactions());
                    topDevelopersByValueRepository.save(dbTopDevelopersByValue);
                    response.setMessage("TopDevelopersByValue updated successfully...");
                    response.setIsSuccess(1);
                }else{
                    response.setMessage("No data found!");
                }
            }else{
                topDevelopersByValueRepository.save(topDevelopersByValue);
                response.setMessage("TopDevelopersByValue saved successfully...");
                response.setIsSuccess(1);
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteTopDevelopersByValue(int id){
        Response response = new Response();
        try{
            topDevelopersByValueRepository.deleteById(id);
            response.setMessage("TopDevelopersByValue deleted successfully");
            response.setIsSuccess(1);
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<TopDevelopersByValueResponse> getTopDevelopersByValue(){
        List<Object[]> topDevelopersByValueResponse =
                topDevelopersByValueRepository.getAllTopDevelopersByValue();
        try{
            Map<String, TopDevelopersByValueResponse> dataMap =
                    new HashMap<>();

            for(Object[] data: topDevelopersByValueResponse){
                String category = (String)data[0];
                String categoryDisplayName = (String)data[1];

                TopDevelopersByValueResponse response = dataMap.computeIfAbsent(category, k -> {
                    TopDevelopersByValueResponse res = new TopDevelopersByValueResponse();
                    res.setCategory(category);
                    res.setCategoryDisplayName(categoryDisplayName);
                    res.setHeaders(new ArrayList<>());
                    res.setAggregationFromList(new ArrayList<>());
                    return res;
                });

                List<Headers> headers = headerRepository.getTopDevelopersHeaders();

                if(response.getHeaders().isEmpty()){
                    response.setHeaders(headers);
                }

                String aggregationFrom = (String)data[2];
                AggregationFromList aggregationFromList = response.getAggregationFromList().stream()
                        .filter(a ->a.getAggregationFrom().equals(aggregationFrom))
                        .findFirst()
                        .orElse(null);
                if(aggregationFromList == null){
                    aggregationFromList = new AggregationFromList();
                    aggregationFromList.setAggregationFrom(aggregationFrom);
                    aggregationFromList.setAggregationFromDisplayName((String) data[3]);
                    aggregationFromList.setLegendHeader(null);
                    aggregationFromList.setDeveloperDetails(new ArrayList<>());
                    response.getAggregationFromList().add(aggregationFromList);
                }

                TopDevelopersByValue developersByValue = new TopDevelopersByValue();
                developersByValue.setLocation(null);
                developersByValue.setLocality(null);
                developersByValue.setDeveloperName((String) data[5]);
                developersByValue.setSaleRentValue((String) data[6]);
                developersByValue.setNoOfTransactions((String) data[7]);
                aggregationFromList.getDeveloperDetails().add(developersByValue);
            }
            return new ArrayList<>(dataMap.values());
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }
}
