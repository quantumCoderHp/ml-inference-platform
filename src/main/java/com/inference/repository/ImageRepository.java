package com.inference.repository;

import com.inference.model.ImageEntity;
import com.inference.model.ImageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long>{

    Optional<ImageEntity> findByS3Key(String s3Key);

    Optional<ImageEntity> findByImageUrl(String imageUrl);

    List<ImageEntity> findByStatus(ImageStatus status);

    @Query("SELECT i FROM ImageEntity i WHERE i.status = :status AND i.createdAt >= :from")
    List<ImageEntity> findRecentByStatus(@Param("status") ImageStatus status,
                                         @Param("from") LocalDateTime from);

    @Query("SELECT COUNT(i) FROM ImageEntity i WHERE i.status = :status")
    long countByStatus(@Param("status") ImageStatus status);
}
