package com.ept.gestion.procurement.repository;

import com.ept.gestion.common.enums.StatutOffre;
import com.ept.gestion.procurement.entity.OffreTarifaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreTarifaireRepository extends JpaRepository<OffreTarifaire, Long> {
    List<OffreTarifaire> findByLotId(Long lotId);
    List<OffreTarifaire> findByLotIdOrderByMontantTotalAsc(Long lotId);
    List<OffreTarifaire> findByFournisseurId(Long fournisseurId);
    List<OffreTarifaire> findByStatut(StatutOffre statut);
}
