package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.entities.Category;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }

    public Response addUpdateCategory(Category category){
        Response response = new Response();
        try{
            if(category.getId() > 0){
                Category dbCategory = categoryRepository.findById(category.getId()).orElse(null);
                if(dbCategory != null){
                    dbCategory.setCategory(category.getCategory());
                    dbCategory.setCategoryDisplayName(category.getCategoryDisplayName());
                    categoryRepository.save(dbCategory);
                    response.setMessage("Category updated successfully...");
                    response.setIsSuccess(1);
                }else{
                    response.setMessage("No data found !");
                }
            }else{
                categoryRepository.save(category);
                response.setIsSuccess(1);
                response.setMessage("Category saved successfully...");
            }
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }
    public Response deleteCategory(int id){
        Response response = new Response();
        try{
            categoryRepository.deleteById(id);
            response.setMessage("Category deleted successfully...");
            response.setIsSuccess(1);
        }catch (Exception e){
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
