package com.mypropertyfact.estate.services;

import com.mypropertyfact.estate.configs.dtos.FloorPlanDto;
import com.mypropertyfact.estate.entities.FloorPlan;
import com.mypropertyfact.estate.entities.Project;
import com.mypropertyfact.estate.models.Response;
import com.mypropertyfact.estate.repositories.FloorPlanRepository;
import com.mypropertyfact.estate.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FloorPlanService {
    @Autowired
    private FloorPlanRepository floorPlanRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<Map<String, Object>> getAllPlans() {
        List<FloorPlan> allFloorPlans = this.floorPlanRepository.findAll();

        Map<Integer, Map<String, Object>> groupedByProject = new HashMap<>();

        for (FloorPlan floorPlan : allFloorPlans) {
            int projectId = floorPlan.getProject().getId();

            Map<String, Object> projectMap = groupedByProject.computeIfAbsent(projectId, id -> {
                Map<String, Object> newProjectMap = new HashMap<>();
                newProjectMap.put("projectId", id);
                newProjectMap.put("projectName", floorPlan.getProject().getProjectName());
                newProjectMap.put("plans", new ArrayList<Map<String, Object>>());
                return newProjectMap;
            });

            List<Map<String, Object>> plans = (List<Map<String, Object>>) projectMap.get("plans");

            Map<String, Object> planMap = new HashMap<>();
            planMap.put("id", floorPlan.getId());
            planMap.put("planType", floorPlan.getPlanType());
            planMap.put("areaSqft", floorPlan.getAreaSqft());
            planMap.put("areaSqMt", floorPlan.getAreaSqmt());
            plans.add(planMap);
        }

        return new ArrayList<>(groupedByProject.values());
    }

    public Response addUpdatePlan(FloorPlanDto floorPlan) {
        Response response = new Response();
        try {
            if (floorPlan == null || floorPlan.getPlanType().isEmpty()) {
                response.setMessage("Plan type is required");
                return response;
            }
            double areaSqMt = floorPlan.getAreaSqFt() * 0.092903;
            floorPlan.setAreaSqMt(areaSqMt);
            Optional<Project> projectById = this.projectRepository.findById(floorPlan.getProjectId());
            if (floorPlan.getFloorId() > 0) {
                Optional<FloorPlan> plan = this.floorPlanRepository.findById(floorPlan.getFloorId());
                if (plan.isPresent()) {
                    FloorPlan dbFloorPlan = plan.get();
                    dbFloorPlan.setPlanType(floorPlan.getPlanType());
                    dbFloorPlan.setAreaSqft(floorPlan.getAreaSqFt());
                    dbFloorPlan.setAreaSqmt(floorPlan.getAreaSqMt());
                    projectById.ifPresent(dbFloorPlan::setProject);
                    this.floorPlanRepository.save(dbFloorPlan);
                    response.setMessage("Floor Plan Updated Successfully...");
                    response.setIsSuccess(1);
                }
            } else {
                FloorPlan floorPlanObj = new FloorPlan();
                floorPlanObj.setPlanType(floorPlan.getPlanType());
                floorPlanObj.setAreaSqmt(floorPlan.getAreaSqMt());
                floorPlanObj.setAreaSqft(floorPlan.getAreaSqFt());
                projectById.ifPresent(floorPlanObj::setProject);
                this.floorPlanRepository.save(floorPlanObj);
                response.setMessage("Floor Plan Saved Successfully...");
                response.setIsSuccess(1);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public Response deleteFloorPlan(int id) {
        try {
            this.floorPlanRepository.deleteById(id);
            return new Response(1, "Deleted successfully...");
        } catch (Exception e) {
            return new Response(0, e.getMessage());
        }
    }
}
