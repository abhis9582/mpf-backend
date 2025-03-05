package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.Headers;
import com.mypropertyfact.estate.entities.TopLocationsByTransaction;
import com.mypropertyfact.estate.models.AggregationFromList;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.models.TopLocationByTransactionResponse;
import com.mypropertyfact.estate.models.TopLocationsByTransactionMapper;
import com.mypropertyfact.estate.repositories.HeaderRepository;
import com.mypropertyfact.estate.repositories.TopLocationsByTransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TopLocationsByTransactionService {

    private TopLocationsByTransactionRepository topLocationsByTransactionRepository;
    private HeaderRepository headerRepository;
    TopLocationsByTransactionService(TopLocationsByTransactionRepository topLocationsByTransactionRepository,
                                     HeaderRepository headerRepository){
        this.topLocationsByTransactionRepository = topLocationsByTransactionRepository;
        this.headerRepository = headerRepository;
    }

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

    public List<TopLocationByTransactionResponse> getAllCategoryWiseData(){
        List<Object[]> rowData = topLocationsByTransactionRepository.getAllCategoryWiseData();
        try{
            Map<String, TopLocationByTransactionResponse> dataMap = new HashMap<>();
            for(Object[] data: rowData){
                String category = (String)data[0];
                String categoryDisplayName = (String)data[1];

                TopLocationByTransactionResponse response = dataMap.computeIfAbsent(category, k -> {
                    TopLocationByTransactionResponse res = new TopLocationByTransactionResponse();
                    res.setCategory(category);
                    res.setCategoryDisplayName(categoryDisplayName);
                    res.setHeaders(new ArrayList<>());
                    res.setAggregationFromList(new ArrayList<>());
                    return res;
                });

                response.setCategory(response.getCategory());
                response.setCategoryDisplayName(response.getCategoryDisplayName());
                response.setAggregationFromList(new ArrayList<>());
                response.setHeaders(new ArrayList<>());

                List<Headers> headers = headerRepository.getTopLocationsByTransactionHeaders();

                if(response.getHeaders().size() < 1){
                    response.setHeaders(headers);
                }

                String aggregationFrom = (String)data[2];
                AggregationFromList aggregation = response.getAggregationFromList().stream()
                        .filter(a-> a.getAggregationFrom().equals(aggregationFrom))
                        .findFirst()
                        .orElse(null);

                if(aggregation == null){
                    aggregation = new AggregationFromList();
                    aggregation.setAggregationFrom(aggregationFrom);
                    aggregation.setAggregationFromDisplayName((String) data[3]);
                    aggregation.setLegendHeader(null);
                    aggregation.setLocationDetails(new ArrayList<>());
                    response.getAggregationFromList().add(aggregation);
                }

                TopLocationsByTransactionMapper topLocationsByTransactionMapper = new TopLocationsByTransactionMapper();
                topLocationsByTransactionMapper.setCity((String) data[4]);
                topLocationsByTransactionMapper.setCurrentPrice((String)data[5]);
                topLocationsByTransactionMapper.setLocation((String) data[6]);
                topLocationsByTransactionMapper.setSaleValue((int) data[7]);
                topLocationsByTransactionMapper.setTransactions((String)data[8]);
                aggregation.getLocationDetails().add(topLocationsByTransactionMapper);
            }
            return new ArrayList<>(dataMap.values());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
