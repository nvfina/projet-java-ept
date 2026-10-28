package com.ept.gestion.procurement.repository;

import com.ept.gestion.procurement.entity.LigneBesoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneBesoinRepository extends JpaRepository<LigneBesoin, Long> {
    List<LigneBesoin> findByExpressionBesoinId(Long expressionBesoinId);
}
