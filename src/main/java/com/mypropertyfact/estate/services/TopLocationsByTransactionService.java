package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.Headers;
import com.mypropertyfact.estate.entities.TopLocationsByTransaction;
import com.mypropertyfact.estate.models.AggregationFromList;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.models.TopLocationByTransactionResponse;
import com.mypropertyfact.estate.models.TopLocationsByTransactionMapper;
import com.mypropertyfact.estate.repositories.HeaderRepository;
import com.mypropertyfact.estate.repositories.TopLocationsByTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopLocationsByTransactionService {

    private final TopLocationsByTransactionRepository topLocationsByTransactionRepository;
    private final HeaderRepository headerRepository;

    public List<TopLocationsByTransaction> getAllTopLocationsByTransaction(){
        return topLocationsByTransactionRepository.findAll();
    }

    public Response addUpdateTopLocationsByTransaction(TopLocationsByTransaction topLocationsByTransaction){
        Response response = new Response();
        try{
            if(topLocationsByTransaction.getId() > 0){
                TopLocationsByTransaction dbTopLocationsByTransaction =
                        topLocationsByTransactionRepository.findById(topLocationsByTransaction.getId()).orElse(null);
                if(dbTopLocationsByTransaction != null){
                    dbTopLocationsByTransaction.setTransactions(topLocationsByTransaction.getTransactions());
                    dbTopLocationsByTransaction.setLocation(topLocationsByTransaction.getLocation());
                    dbTopLocationsByTransaction.setCity(topLocationsByTransaction.getCity());
                    dbTopLocationsByTransaction.setCurrentPrice(topLocationsByTransaction.getCurrentPrice());
                    dbTopLocationsByTransaction.setSaleValue(topLocationsByTransaction.getSaleValue());
                }else{
                    response.setMessage("No data found !");
                }
            }else{
                topLocationsByTransactionRepository.save(topLocationsByTransaction);
                response.setMessage("TopLocationsByTransaction saved successfully...");
                response.setIsSuccess(1);
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteTopLocationsByTransaction(int id){
        Response response = new Response();
        try{
            topLocationsByTransactionRepository.deleteById(id);
        }catch(Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<TopLocationByTransactionResponse> getAllCategoryWiseData() {
        List<Object[]> rowData = topLocationsByTransactionRepository.getAllCategoryWiseData();
        Map<String, TopLocationByTransactionResponse> dataMap = new HashMap<>();

        try {
            List<Headers> headers = headerRepository.getTopLocationsByTransactionHeaders(); // Fetch headers once

            for (Object[] data : rowData) {
                String category = (String) data[0];
                String categoryDisplayName = (String) data[1];

                // Use computeIfAbsent to create or retrieve the response object
                TopLocationByTransactionResponse response = dataMap.computeIfAbsent(category, k -> {
                    TopLocationByTransactionResponse res = new TopLocationByTransactionResponse();
                    res.setCategory(category);
                    res.setCategoryDisplayName(categoryDisplayName);
                    res.setHeaders(new ArrayList<>(headers)); // Set headers only once
                    res.setAggregationFromList(new ArrayList<>());
                    return res;
                });

                // Get or create AggregationFromList using computeIfAbsent
                String aggregationFrom = (String) data[2];
                AggregationFromList aggregation = response.getAggregationFromList().stream()
                        .filter(a -> a.getAggregationFrom().equals(aggregationFrom))
                        .findFirst()
                        .orElseGet(() -> {
                            AggregationFromList newAggregation = new AggregationFromList();
                            newAggregation.setAggregationFrom(aggregationFrom);
                            newAggregation.setAggregationFromDisplayName((String) data[3]);
                            newAggregation.setLegendHeader(null);
                            newAggregation.setLocationDetails(new ArrayList<>());
                            response.getAggregationFromList().add(newAggregation);
                            return newAggregation;
                        });

                // Map data to TopLocationsByTransactionMapper
                TopLocationsByTransactionMapper locationDetails = new TopLocationsByTransactionMapper();
                locationDetails.setCity((String) data[4]);
                locationDetails.setCurrentPrice((String) data[5]);
                locationDetails.setLocation((String) data[6]);
                locationDetails.setSaleValue((int) data[7]);
                locationDetails.setTransactions((String) data[8]);

                aggregation.getLocationDetails().add(locationDetails);
            }

            return new ArrayList<>(dataMap.values());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Collections.emptyList(); // Return an empty list instead of null to prevent NullPointerException
        }
    }

}
