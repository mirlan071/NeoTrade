package com.neotrade.repository;

import com.neotrade.model.Ad;
import com.neotrade.model.Category;
import com.neotrade.model.Region;
import com.neotrade.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {

    // Базовые методы поиска
    Page<Ad> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Ad> findByTitleContainingIgnoreCaseAndRegionIn(String title, List<Region> regions, Pageable pageable);
    Page<Ad> findByCategoryInAndPriceBetweenAndRegionIn(List<Category> categories, Double min, Double max, List<Region> regions, Pageable pageable);
    Page<Ad> findByPriceBetweenAndRegionIn(Double min, Double max, List<Region> regions, Pageable pageable);
    Page<Ad> findByRegionIn(List<Region> regions, Pageable pageable);
    Page<Ad> findByOwner(User owner, Pageable pageable);
    Page<Ad> findByRegion(Region region, Pageable pageable);

    // Метод для получения только активных объявлений
    @Query("SELECT a FROM Ad a WHERE a.owner.enabled = true ORDER BY a.createdAt DESC")
    Page<Ad> findAllActiveAds(Pageable pageable);

    // Поиск с фильтрацией по статусу
    @Query("SELECT a FROM Ad a WHERE " +
            "(:query IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "(:regions IS NULL OR a.region IN :regions) AND " +
            "(:categories IS NULL OR a.category IN :categories) AND " +
            "(:minPrice IS NULL OR a.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR a.price <= :maxPrice) AND " +
            "a.owner.enabled = true")
    Page<Ad> searchAdsWithFilters(
            @Param("query") String query,
            @Param("regions") List<Region> regions,
            @Param("categories") List<Category> categories,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    // Получение объявлений конкретного пользователя
    @Query("SELECT a FROM Ad a WHERE a.owner.phoneNumber = :phoneNumber")
    Page<Ad> findByUserPhone(@Param("phoneNumber") String phoneNumber, Pageable pageable);
}