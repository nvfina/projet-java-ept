package com.ept.gestion.procurement.repository;

import com.ept.gestion.common.enums.StatutLot;
import com.ept.gestion.procurement.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    List<Lot> findByMarcheId(Long marcheId);
    List<Lot> findByStatut(StatutLot statut);
    List<Lot> findByFournisseurId(Long fournisseurId);
    long countByStatut(StatutLot statut);
}
