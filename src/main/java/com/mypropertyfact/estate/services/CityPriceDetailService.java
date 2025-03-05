package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.CityPriceDetail;
import com.mypropertyfact.estate.entities.Headers;
import com.mypropertyfact.estate.models.AggregationFromList;
import com.mypropertyfact.estate.models.CityPriceData;
import com.mypropertyfact.estate.models.CityPriceDataResponse;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CityPriceDetailRepository;
import com.mypropertyfact.estate.repositories.HeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CityPriceDetailService {
    @Autowired
    private CityPriceDetailRepository cityPriceDetailRepository;
    @Autowired
    private HeaderRepository headerRepository;

    public List<CityPriceDataResponse> getAllCityPriceDetail() {
        List<CityPriceDataResponse> response = new ArrayList<>();
        try {
            List<Object[]> rowData = cityPriceDetailRepository.findAllData();
            return rowData.stream().map(i ->
                    new CityPriceDataResponse(
                            (int) i[0],
                            (String) i[1],
                            (String) i[2],
                            (String) i[3],
                            (String) i[4],
                            (String) i[5],
                            (String) i[6],
                            (String) i[7],
                            (String) i[8],
                            (String) i[9]
                    )).collect(Collectors.toList());
        } catch (Exception e) {

        }
        return response;
    }

    public Response addUpdateCityPriceDetail(CityPriceDetail cityPriceDetail) {
        Response response = new Response();
        try {
            double percentage = ((double) Integer.parseInt(cityPriceDetail.getChangeValue()) /
                    Integer.parseInt(cityPriceDetail.getCurrentRate())) * 100;

            if(percentage > 0){
                cityPriceDetail.setChangePercentage("+"+String.format("%.2f", percentage*(-1))+"%");
            }else{
                cityPriceDetail.setChangePercentage("+"+String.format("%.2f", percentage*(-1))+"%");
            }
            if(cityPriceDetail.getChangeValue().startsWith("-")){
                cityPriceDetail.setChangeValue("-₹ "+Integer.parseInt(cityPriceDetail.getChangeValue())*(-1));
            }else{
                cityPriceDetail.setChangeValue("+₹ "+cityPriceDetail.getChangeValue());
            }
            if (cityPriceDetail.getId() > 0) {
                CityPriceDetail dbCityPriceDetail = cityPriceDetailRepository.findById(cityPriceDetail.getId()).orElse(null);
                if (dbCityPriceDetail != null) {
                    dbCityPriceDetail.setCity(cityPriceDetail.getCity());
                    dbCityPriceDetail.setLocality(cityPriceDetail.getLocality());
                    dbCityPriceDetail.setLocation(cityPriceDetail.getLocation());
                    dbCityPriceDetail.setChangeValue(cityPriceDetail.getChangeValue());
                    dbCityPriceDetail.setChangePercentage(cityPriceDetail.getChangePercentage());
                    dbCityPriceDetail.setCurrentRate(cityPriceDetail.getCurrentRate());
                    dbCityPriceDetail.setPriority(cityPriceDetail.getPriority());
                    dbCityPriceDetail.setLocationUrl(cityPriceDetail.getLocationUrl());
                    dbCityPriceDetail.setNoOfProjects(cityPriceDetail.getNoOfProjects());
                    dbCityPriceDetail.setNoOfTransactions(cityPriceDetail.getNoOfTransactions());
                    dbCityPriceDetail.setSaleRentValue(cityPriceDetail.getSaleRentValue());
                    cityPriceDetailRepository.save(dbCityPriceDetail);
                    response.setMessage("CityPriceDetail updated successfully...");
                    response.setIsSuccess(1);
                } else {
                    response.setMessage("No data found !");
                }
            } else {
                cityPriceDetailRepository.save(cityPriceDetail);
                response.setIsSuccess(1);
                response.setMessage("CityPriceDetail saved successfully...");
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteCityPriceDetail(int id) {
        Response response = new Response();
        try {
            cityPriceDetailRepository.deleteById(id);
            response.setMessage("CityPriceDetail deleted successfully...");
            response.setIsSuccess(1);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<CityPriceData> cityPriceData() {
        List<Object[]> rawData = cityPriceDetailRepository.getCityPriceList();
        try {
            // Grouping data by Category and Category Display Name
            Map<String, CityPriceData> categoryMap = new LinkedHashMap<>();

            for (Object[] row : rawData) {
                String category = (String) row[1];
                String categoryDisplayName = (String) row[2];

                // Fetch or create the category entry
                CityPriceData cityPriceData = categoryMap.computeIfAbsent(category, k -> {
                    CityPriceData data = new CityPriceData();
                    data.setCategory(category);
                    data.setCategoryDisplayName(categoryDisplayName);
                    data.setHeaders(new ArrayList<>());
                    data.setAggregationFromList(new ArrayList<>());
                    return data;
                });
                List<Headers> list = headerRepository.findAll();
                List<Headers> limitedList = list.size() > 4 ? list.subList(0, 4) : list;
//                list.forEach();
//                // Create or fetch header object
//                Headers header = new Headers();
//                header.setHeaderDisplayName((String) row[3]);
//                header.setHeader((String) row[4]);
//                header.setPriority(((String) row[5]));
//                header.setSubHeader((String) row[6]);

                if (cityPriceData.getHeaders().size() < 1) {
                    cityPriceData.setHeaders(limitedList);
                }

                // Create or fetch aggregation object
                String aggregationFrom = (String) row[7];
                AggregationFromList aggregation = cityPriceData.getAggregationFromList().stream()
                        .filter(a -> a.getAggregationFrom().equals(aggregationFrom))
                        .findFirst()
                        .orElse(null);

                if (aggregation == null) {
                    aggregation = new AggregationFromList();
                    aggregation.setAggregationFrom(aggregationFrom);
                    aggregation.setAggregationFromDisplayName((String) row[8]);
                    aggregation.setLegendHeader((String) row[9]);
                    aggregation.setDetails(new ArrayList<>());
                    cityPriceData.getAggregationFromList().add(aggregation);
                }

                // Create city price details
                CityPriceDetail cityPriceDetail = new CityPriceDetail();
                cityPriceDetail.setCity((String) row[10]);
                cityPriceDetail.setNoOfProjects(((String) row[11]));
                cityPriceDetail.setNoOfTransactions(((String) row[12]));
                cityPriceDetail.setCurrentRate((String) row[13]);
                cityPriceDetail.setChangeValue((String) row[14]);
                cityPriceDetail.setChangePercentage((String) row[15]);
                cityPriceDetail.setLocation((String) row[16]);
                cityPriceDetail.setLocality((String) row[17]);
                cityPriceDetail.setLocationUrl((String) row[18]);
                cityPriceDetail.setSaleRentValue((String) row[19]);
                aggregation.getDetails().add(cityPriceDetail);
            }
            return new ArrayList<>(categoryMap.values());
        } catch (Exception e) {
            String message = e.getMessage();
            System.out.printf(message);
        }
        return null;
    }

    public List<CityPriceData> topGainersLocations() {
        List<Object[]> rawData = cityPriceDetailRepository.topGainersLocations();
        try {
            // Grouping data by Category and Category Display Name
            Map<String, CityPriceData> categoryMap = new LinkedHashMap<>();

            for (Object[] row : rawData) {
                String category = (String) row[1];
                String categoryDisplayName = (String) row[2];

                // Fetch or create the category entry
                CityPriceData cityPriceData = categoryMap.computeIfAbsent(category, k -> {
                    CityPriceData data = new CityPriceData();
                    data.setCategory(category);
                    data.setCategoryDisplayName(categoryDisplayName);
                    data.setHeaders(new ArrayList<>());
                    data.setAggregationFromList(new ArrayList<>());
                    return data;
                });
                List<Headers> list = headerRepository.getAll();
//                List<Headers> limitedList = list.size() > 4 ? list.subList(0, 4) : list;
//                list.forEach();
//                // Create or fetch header object
//                Headers header = new Headers();
//                header.setHeaderDisplayName((String) row[3]);
//                header.setHeader((String) row[4]);
//                header.setPriority(((String) row[5]));
//                header.setSubHeader((String) row[6]);

                if (cityPriceData.getHeaders().size() < 1) {
                    cityPriceData.setHeaders(list);
                }

                // Create or fetch aggregation object
                String aggregationFrom = (String) row[7];
                AggregationFromList aggregation = cityPriceData.getAggregationFromList().stream()
                        .filter(a -> a.getAggregationFrom().equals(aggregationFrom))
                        .findFirst()
                        .orElse(null);

                if (aggregation == null) {
                    aggregation = new AggregationFromList();
                    aggregation.setAggregationFrom(aggregationFrom);
                    aggregation.setAggregationFromDisplayName((String) row[8]);
                    aggregation.setLegendHeader((String) row[9]);
                    aggregation.setDetails(new ArrayList<>());
                    cityPriceData.getAggregationFromList().add(aggregation);
                }

                // Create city price details
                CityPriceDetail cityPriceDetail = new CityPriceDetail();
                cityPriceDetail.setCity((String) row[10]);
                cityPriceDetail.setNoOfProjects(((String) row[11]));
                cityPriceDetail.setNoOfTransactions(((String) row[12]));
                cityPriceDetail.setCurrentRate((String) row[13]);
                cityPriceDetail.setChangeValue((String) row[14]);
                cityPriceDetail.setChangePercentage((String) row[15]);
                cityPriceDetail.setLocation((String) row[16]);
                cityPriceDetail.setLocality((String) row[17]);
                cityPriceDetail.setLocationUrl((String) row[18]);
                cityPriceDetail.setSaleRentValue((String) row[19]);
                aggregation.getDetails().add(cityPriceDetail);
            }
            return new ArrayList<>(categoryMap.values());
        } catch (Exception e) {
            String message = e.getMessage();
            System.out.printf(message);
        }
        return null;
    }
}
