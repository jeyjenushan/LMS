package org.ai.server.repository;

import org.ai.server.enumpackage.Status;
import org.ai.server.model.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity,Long> {
    List<PurchaseEntity> findByCourseIdInAndStatus(List<Long> courseIds, Status status);
}
