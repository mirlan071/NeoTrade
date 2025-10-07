package com.neotrade.repository;




import com.neotrade.model.Ad;
import com.neotrade.model.Category;
import com.neotrade.model.Region;
import com.neotrade.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRepository extends JpaRepository <Ad, Long> {

    Page <Ad> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page <Ad> findByTitleContainingIgnoreCaseAndRegionIn(String title, List<Region> regions, Pageable pageable);

    Page <Ad> findByCategoryInAndPriceBetweenAndRegionIn(List<Category> categories, Double min, Double max, List<Region> regions, Pageable pageable);

    Page <Ad> findByPriceBetweenAndRegionIn(Double min, Double max, List<Region> regions, Pageable pageable);

    Page <Ad> findByRegionIn(List<Region> regions, Pageable pageable);

    Page <Ad> findByOwner (User owner, Pageable pageable);

    Page <Ad> findByRegion (Region region, Pageable pageable);

    Page <Ad> findAllAdc (Pageable pageable);

    List <Ad> findById (List<Long> id);
}

