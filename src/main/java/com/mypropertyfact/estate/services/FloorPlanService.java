package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.FloorPlansDto;
import com.mypropertyfact.estate.entities.FloorPlan;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.FloorPlanRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FloorPlanService {
    @Autowired
    private FloorPlanRepository floorPlanRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<FloorPlansDto> getAllPlans() {
        List<Object[]> plans = this.floorPlanRepository.getAllFloorPlans();
        return plans.stream().map(result -> new FloorPlansDto(
                        (int) result[0],
                        (String) result[1],
                        (String) result[2],
                        (Double) result[3],
                        (Double) result[4],
                        (int) result[5]
                )
        ).collect(Collectors.toList());
    }

    public Response addUpdatePlan(FloorPlan floorPlan) {
        Response response = new Response();
        try {
            if (floorPlan == null || floorPlan.getPlanType().isEmpty()) {
                response.setMessage("Plan type is required");
                return response;
            }
            double areaSqMt = floorPlan.getAreaSqft() * 0.092903;
            floorPlan.setAreaSqmt(areaSqMt);
            Project project = this.projectRepository.findById(floorPlan.getProjectId()).get();
            floorPlan.setSlugUrl(project.getSlugURL());
            if (floorPlan.getId() > 0) {
                FloorPlan savedFloorPlan = this.floorPlanRepository.findById(floorPlan.getId()).get();
                if (savedFloorPlan != null) {
                    savedFloorPlan.setPlanType(floorPlan.getPlanType());
                    savedFloorPlan.setAreaSqft(floorPlan.getAreaSqft());
                    savedFloorPlan.setAreaSqmt(floorPlan.getAreaSqmt());
                    savedFloorPlan.setSlugUrl(floorPlan.getSlugUrl());
                    savedFloorPlan.setUpdatedAt(LocalDateTime.now());
                    this.floorPlanRepository.save(savedFloorPlan);
                    response.setMessage("Floor Plan Updated Successfully...");
                    response.setIsSuccess(1);
                }
            } else {
                this.floorPlanRepository.save(floorPlan);
                response.setMessage("Floor Plan Saved Successfully...");
                response.setIsSuccess(1);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public List<FloorPlan> getBySlugUrl(String url) {
        return this.floorPlanRepository.findBySlugUrl(url);
    }
    public Response deleteFloorPlan(int id){
        Response response = new Response();
        try{
            this.floorPlanRepository.deleteById(id);
            new Response(1, "Deleted successfully...");
        }catch (Exception e){
            new Response(0, e.getMessage());
        }
        return response;
    }
}
