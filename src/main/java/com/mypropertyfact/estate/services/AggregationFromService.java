package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.AggregationFrom;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.AggregationFromRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AggregationFromService {

    private final AggregationFromRepository aggregationFromRepository;

    public List<AggregationFrom> getAllAggregationFrom(){
        return aggregationFromRepository.findAll();
    }
    public Response addUpdateAggregationFrom(AggregationFrom aggregationFrom){
        Response response = new Response();
        try{
            if(aggregationFrom.getId() > 0){
                AggregationFrom dbAggregationFrom = aggregationFromRepository.findById(aggregationFrom.getId()).orElse(null);
                if(dbAggregationFrom != null){
                    dbAggregationFrom.setAggregationFrom(aggregationFrom.getAggregationFrom());
                    dbAggregationFrom.setLegendHeader(aggregationFrom.getLegendHeader());
                    dbAggregationFrom.setAggregationFromDisplayName(aggregationFrom.getAggregationFromDisplayName());
                    aggregationFromRepository.save(dbAggregationFrom);
                    response.setMessage("AggregationFrom updated successfully...");
                    response.setIsSuccess(1);
                }else{
                    response.setMessage("No data found !");
                }
            }else{
                aggregationFromRepository.save(aggregationFrom);
                response.setIsSuccess(1);
                response.setMessage("AggregationFrom saved successfully...");
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteAggregationFrom(int id){
        Response response = new Response();
        try{
            aggregationFromRepository.deleteById(id);
            response.setMessage("AggregationFrom deleted successfully...");
            response.setIsSuccess(1);
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
