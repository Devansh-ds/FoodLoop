package com.devansh.repo;

import com.devansh.model.Donation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Integer> {

    @Query("""
    SELECT d FROM Donation d
    WHERE (d.status = 'AVAILABLE' OR d.status = 'REQUESTED')
    AND (:category IS NULL OR d.category.name = :category)
    AND (:minQuantity IS NULL OR d.quantity >= :minQuantity)
    AND (CAST(:beforeExpiry AS timestamp) IS NULL OR d.expiresAt <= :beforeExpiry)
""")
    Page<Donation> findAvailableDonations(
            @Param("category") String category,
            @Param("minQuantity") Integer minQuantity,
            @Param("beforeExpiry") LocalDateTime beforeExpiry,
            Pageable pageable
    );


    List<Donation> findByDonorId(Integer userId);
}
