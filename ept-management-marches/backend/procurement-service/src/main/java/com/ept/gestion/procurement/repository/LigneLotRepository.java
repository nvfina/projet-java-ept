package com.ept.gestion.procurement.repository;

import com.ept.gestion.procurement.entity.LigneLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneLotRepository extends JpaRepository<LigneLot, Long> {
    List<LigneLot> findByLotId(Long lotId);
}
