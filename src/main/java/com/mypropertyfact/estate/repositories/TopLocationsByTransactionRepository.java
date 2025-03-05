package com.mypropertyfact.estate.repositories;

import com.mypropertyfact.estate.entities.TopLocationsByTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopLocationsByTransactionRepository extends JpaRepository<TopLocationsByTransaction, Integer> {
    @Query(value = """
            SELECT
                c.category,
            	c.category_display_name,
            	ag.aggregation_from,
            	ag.aggregation_from_display_name,
            	cy.name as city,
            	concat("₹ ", Format(tt.current_price, 0)) as currentPrice,
            	tt.location,
            	tt.sale_value,
            	concat("₹ ", Format(tt.transactions, 0)) as transactions
            from top_locations_by_transaction as tt left join category as c on tt.category_id = c.id
            left join aggregation_from as ag on tt.aggregation_from = ag.id
            left join city cy on tt.city = cy.id order by field(aggregation_from_display_name, '1Yr', '6M', '3M') desc;
            """, nativeQuery = true)
    List<Object[]> getAllCategoryWiseData();
}
