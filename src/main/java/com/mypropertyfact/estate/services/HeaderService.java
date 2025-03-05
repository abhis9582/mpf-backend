package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.Constants;
import com.mypropertyfact.estate.entities.Headers;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.HeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeaderService {
    @Autowired
    private HeaderRepository headerRepository;

    public List<Headers> getAllHeaders(){
        return headerRepository.findAll();
    }
    public Response addUpdateHeader(Headers headers) {
        Response response = new Response();
        try {
            if (headers.getId() > 0) {
                Headers dbHeaders = headerRepository.findById(headers.getId()).orElse(null);
                if (dbHeaders != null) {
                    dbHeaders.setHeaderDisplayName(headers.getHeaderDisplayName());
                    dbHeaders.setSubHeader(headers.getSubHeader());
                    dbHeaders.setPriority(headers.getPriority());
                    dbHeaders.setHeader(headers.getHeader());
                    headerRepository.save(dbHeaders);
                    response.setMessage("Header updated successfully...");
                    response.setIsSuccess(1);
                }else{
                    response.setMessage(Constants.SOMETHING_WENT_WRONG);
                }
            } else {
                headerRepository.save(headers);
                response.setMessage("Header saved successfully...");
                response.setIsSuccess(1);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteHeader(int id){
        Response response = new Response();
        try{
            headerRepository.deleteById(id);
            response.setMessage("Header deleted successfully...");
            response.setIsSuccess(1);
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
