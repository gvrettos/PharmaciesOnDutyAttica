package com.dstym.pharmaciesondutyattica.repository;

import com.dstym.pharmaciesondutyattica.entity.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PharmacyRepository extends JpaRepository<Pharmacy, Integer> {
    @Query(value = "select p from Pharmacy p " +
            "where (p.region=:region or :region is null)")
    Page<Pharmacy> findAll(String region, Pageable pageable);
}
