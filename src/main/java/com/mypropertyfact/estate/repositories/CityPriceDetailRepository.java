package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.CityPriceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityPriceDetailRepository extends JpaRepository<CityPriceDetail, Integer> {
    @Query(value = """
                SELECT
                    cd.id AS category_id,
                    c.category AS category,                    
                    c.category_display_name AS categoryDisplayName,
                    null as header,
                    null as headerDisplayName,
                    null as priority,
                    null as subHeader,
                    af.aggregation_from AS aggregationFrom,
                    af.aggregation_from_display_name AS aggregationFromDisplayName,
                    af.legend_header AS legendHeader,
                    city.name AS city,                
                    cd.no_of_projects AS noOfProjects,
                    cd.no_of_transactions AS noOfTransactions,
                    cd.current_rate AS currentRate,
                    cd.change_value AS changeValue,
                    cd.change_percentage AS changePercentage,
                    cd.location AS location,
                    cd.locality AS locality,
                    cd.location_url AS locationUrl,
                    CONCAT('₹ ', FORMAT(cd.sale_rent_value, 0), ' Cr.') AS saleRentValue
                FROM price_detail cd
                JOIN category c ON cd.category_id = c.id
                JOIN aggregation_from af ON cd.aggregation_from_id = af.id
                JOIN city ON cd.city = city.id
                where cd.location is null
                ORDER BY af.id, c.id, city.name;
            """, nativeQuery = true)
    List<Object[]> getCityPriceList();

    @Query(value = """
                SELECT
                    cd.id AS category_id,
                    c.category AS category,                    
                    c.category_display_name AS categoryDisplayName,
                    null as header,
                    null as headerDisplayName,
                    null as priority,
                    null as subHeader,
                    af.aggregation_from AS aggregationFrom,
                    af.aggregation_from_display_name AS aggregationFromDisplayName,
                    af.legend_header AS legendHeader,
                    city.name AS city,                
                    cd.no_of_projects AS noOfProjects,
                    cd.no_of_transactions AS noOfTransactions,
                    cd.current_rate AS currentRate,
                    cd.change_value AS changeValue,
                    cd.change_percentage AS changePercentage,
                    cd.location AS location,
                    cd.locality AS locality,
                    cd.location_url AS locationUrl,
                    CONCAT('₹ ', FORMAT(cd.sale_rent_value, 0), ' Cr.') AS saleRentValue
                FROM price_detail cd
                JOIN category c ON cd.category_id = c.id
                JOIN aggregation_from af ON cd.aggregation_from_id = af.id
                JOIN city ON cd.city = city.id
                where cd.location is not null
                ORDER BY af.id, c.id, city.name;
            """, nativeQuery = true)
    List<Object[]> topGainersLocations();

//    List<Object[]> getCityPriceList();

    @Query(value = "SELECT pc.id, pc.change_percentage, pc.change_value, c.name, " +
            "pc.current_rate, pc.location_url, " +
            "pc.no_of_projects, pc.no_of_transactions, " +
            "ag.aggregation_from, cat.category " +
            "FROM price_detail pc " +
            "JOIN category cat ON pc.category_id = cat.id " +
            "JOIN city c ON pc.city = c.id " +
            "JOIN aggregation_from ag ON pc.aggregation_from_id = ag.id " +
            "order by field(ag.aggregation_from, '1-year', '6-months', '3-months')",
            nativeQuery = true)
    List<Object[]> findAllData();
}