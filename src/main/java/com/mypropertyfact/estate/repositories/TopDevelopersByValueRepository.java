package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.TopDevelopersByValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopDevelopersByValueRepository extends JpaRepository<TopDevelopersByValue, Integer> {
    @Query(value = """
            select
            	c.category,
            	c.category_display_name,
            	ag.aggregation_from,
            	ag.aggregation_from_display_name,
            	cy.name as city,
            	tt.developer_name,
            	concat("₹ ", Format(tt.sale_rent_value, 0)) as  saleValue,
            	Format(tt.no_of_transactions, 0) as transactions
            from top_developers_by_value as tt left join category as c on tt.category_id = c.id
            left join aggregation_from as ag on tt.aggregation_from = ag.id
            left join city cy on tt.city = cy.id order by field(aggregation_from_display_name, '1Yr', '6M', '3M') desc;
            """, nativeQuery = true)
    List<Object[]> getAllTopDevelopersByValue();
    @Query(value = "SELECT t.developer_name, FORMAT(t.no_of_transactions, 0) AS formattedTransactions, " +
            "CONCAT('₹ ', FORMAT(t.sale_rent_value, 0)) AS formattedSaleValue " +
            "FROM top_developers_by_value t", nativeQuery = true)
    List<Object[]> getAllData();
}
