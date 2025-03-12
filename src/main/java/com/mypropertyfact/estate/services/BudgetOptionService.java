package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.BudgetOption;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.BudgetOptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BudgetOptionService {
    @Autowired
    private BudgetOptionRepository budgetOptionRepository;

    public List<BudgetOption> getAll(){
        return budgetOptionRepository.findAll();
    }

    public Response addUpdate(BudgetOption budgetOption){
        Response response = new Response();
        try{
            if(budgetOption.getId() > 0){
                BudgetOption dbBudgetOption = budgetOptionRepository.findById(budgetOption.getId()).orElse(null);
                if(dbBudgetOption != null){
                    dbBudgetOption.setBudgetValue(budgetOption.getBudgetValue());
                    dbBudgetOption.setUpdatedAt(LocalDateTime.now());
                    budgetOptionRepository.save(dbBudgetOption);
                    response.setMessage("BudgetOption updated successfully...");
                    response.setIsSuccess(1);
                }else{
                    response.setMessage("No data found !!");
                }
            }else{
                budgetOptionRepository.save(budgetOption);
                response.setMessage("BudgetOption saved successfully...");
                response.setIsSuccess(1);
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteBudgetOption(int id){
        Response response = new Response();
        try{
            BudgetOption budgetOption = budgetOptionRepository.findById(id).orElse(null);
            if(budgetOption != null){
                budgetOptionRepository.deleteById(id);
                response.setIsSuccess(1);
                response.setMessage("BudgetOption deleted successfully...");
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
