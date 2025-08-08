package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.Enquery;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.EnqueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnquiryService {
    @Autowired
    private EnqueryRepository enqueryRepository;
    @Autowired
    private SendEmailHandler sendEmailHandler;
    public List<Enquery> getAll(){
        return enqueryRepository.findAll();
    }

    public Response addUpdate(Enquery enquery){
        Response response = new Response();
        try{
            if(enquery.getId() > 0){
                Enquery dbEnquery = enqueryRepository.findById(enquery.getId()).orElse(null);
                if(dbEnquery != null){
                    dbEnquery.setName(enquery.getName());
                    dbEnquery.setEmail(enquery.getEmail());
                    dbEnquery.setPhone(enquery.getPhone());
                    dbEnquery.setMessage(enquery.getMessage());
                    dbEnquery.setUpdatedAt(LocalDateTime.now());
                    enqueryRepository.save(dbEnquery);
                    response.setIsSuccess(1);
                    response.setMessage("Data updated successfully...");
                }else{
                    response.setMessage("No data found !!");
                }
            }else{
                enqueryRepository.save(enquery);
//                sendEmailHandler.sendEmail(enquery.getEmail(), "Thank you for giving details", "Hi, Thank you out team will get back to you");
                response.setIsSuccess(1);
                response.setMessage("Data saved successfully...");
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteEnquiry(int id){
        try{
            Enquery dbEnquery = enqueryRepository.findById(id).orElse(null);
            if(dbEnquery != null){
                enqueryRepository.deleteById(id);
                return new Response(1, "Enquiry deleted successfully...", 0);
            }else{
                throw new Exception("data already deleted or not found !!");
            }
        }catch (Exception e){
            return new Response(0, e.getMessage(), 0);
        }
    }
}
